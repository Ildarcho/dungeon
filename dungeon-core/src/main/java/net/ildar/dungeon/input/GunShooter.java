package net.ildar.dungeon.input;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.ildar.dungeon.CollisionDetector;
import net.ildar.dungeon.Gun;
import net.ildar.dungeon.Gun.State;
import net.ildar.dungeon.MobKiller;
import net.ildar.dungeon.config.Configuration;
import net.ildar.dungeon.map.Gamer;
import net.ildar.dungeon.map.MapObject;
import net.ildar.dungeon.map.Mob;
import net.ildar.dungeon.provider.MapProvider;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Optional;

/**
 * Обработчик выстрелов оружия.
 */
@Log4j2
@RequiredArgsConstructor(onConstructor_ = {@Inject})
@Singleton
public class GunShooter {
    
    private final Gun gun;
    private final Gamer gamer;
    private final MapProvider mapProvider;
    private final MobKiller mobKiller;
    private final Configuration configuration;
    private final CollisionDetector collisionDetector;

    /**
     * Количество игровых кадров пройденных при выстреле.
     */
    private int shootingTicksElapsed;

    /**
     * Количество игровых кадров пройденных при перезарядке оружия.
     */
    private int reloadingTicksElapsed;

    /**
     * Произвести выстрел.
     */
    public void shoot() {
        if (gun.getState() != State.INACTIVE) {
            return;
        }
        this.shootingTicksElapsed = 0;
        this.reloadingTicksElapsed = 0;
        gun.setShootingProgress(0);
        gun.setReloadProgress(0);
        gun.setState(State.SHOOTING);
        Optional<Mob> shutMob = checkForShutMob();
        shutMob.ifPresent(mobKiller::shootAtMob);
    }

    private Optional<Mob> checkForShutMob() {
        Optional<MapObject> interferingMapObject = collisionDetector.getInterferingMapObject(
                mapProvider.getDungeonMap(), gamer.getPosition());
        if (interferingMapObject.isPresent() && interferingMapObject.get() instanceof Mob) {
            return Optional.of((Mob)interferingMapObject.get());
        }
        double bulletDistance = configuration.getTracingStep();
        while(interferingMapObject.isEmpty() && bulletDistance < configuration.getViewDistance()) {
            interferingMapObject = collisionDetector.getInterferingMapObject(
                    mapProvider.getDungeonMap(), gamer.getPosition().shift(bulletDistance, gamer.getViewAngle()));
            bulletDistance += configuration.getTracingStep();
        }
        if (interferingMapObject.isEmpty()) {
            return Optional.empty();
        }
        if (interferingMapObject.get() instanceof Mob) {
            return Optional.of((Mob)interferingMapObject.get());
        } else {
            return Optional.empty();
        }
    }

    /**
     * Обновить состояние оружия.
     */
    public void update() {
        if (gun.getState() == State.INACTIVE) {
            return;
        }
        if (gun.getState() == State.SHOOTING) {
            if (shootingTicksElapsed >= configuration.getGunShootingTicks()) {
                gun.setState(State.RELOADING);
            } else {
                gun.setShootingProgress(((double) shootingTicksElapsed) / configuration.getGunShootingTicks() * 100);
                shootingTicksElapsed++;
            }
        }
        if (gun.getState() == State.RELOADING) {
            if (reloadingTicksElapsed >= configuration.getGunReloadingTicks()) {
                gun.setState(State.INACTIVE);
            } else {
                gun.setReloadProgress(((double) reloadingTicksElapsed) / configuration.getGunReloadingTicks() * 100);
                reloadingTicksElapsed++;
            }
        }
    }
}