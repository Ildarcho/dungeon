package net.ildar.dungeon;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.ildar.dungeon.config.Configuration;
import net.ildar.dungeon.map.Mob;
import net.ildar.dungeon.provider.MapProvider;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Обработчик убийств мобов.
 */
@Log4j2
@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class MobKiller {
    private final MapProvider mapProvider;
    private final Configuration configuration;

    /**
     * Список убитых мобов, ожидающих удаления с карты. Значение мапы - количество оставшихся кадров.
     */
    private Map<Mob, Integer> killedMobMap = new HashMap<>();

    /**
     * Выстрелить в моба.
     *
     * @param mob моб
     */
    public synchronized void shootAtMob(Mob mob) {
        if (mob.getState() != Mob.State.KILLED) {
            mob.setState(Mob.State.KILLED);
            killedMobMap.put(mob, configuration.getMobKillingTicks());
            log.info("{} was killed", mob);
        } else {
            log.info("{} is already dead", mob);
        }
    }

    /**
     * Обновить состояние убитых мобов.
     */
    public synchronized void updateKilledMobStates() {
        Iterator<Mob> mobIterator = killedMobMap.keySet().iterator();
        while(mobIterator.hasNext()) {
            Mob mob = mobIterator.next();
            int remainedTicks = killedMobMap.get(mob);
            if (remainedTicks == 0) {
                mobIterator.remove();
                mapProvider.getDungeonMap().removeMovableMapObject(mob);
                log.info("{} disappeared", mob);
            } else {
                killedMobMap.put(mob, remainedTicks - 1);
            }
        }
    }

    /**
     * Узнать мертвы ли все мобы.
     */
    public boolean allMobsAreDead() {
        return mapProvider.getDungeonMap().getMovableObjectSet().stream()
                .filter(mmo -> mmo instanceof Mob)
                .map(mmo -> ((Mob) mmo).getState())
                .noneMatch(s -> s == Mob.State.ALIVE);
    }
}
