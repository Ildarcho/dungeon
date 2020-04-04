package net.ildar.dungeon.input;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.ildar.dungeon.CollisionDetector;
import net.ildar.dungeon.config.Configuration;
import net.ildar.dungeon.map.Gamer;
import net.ildar.dungeon.map.MapObject;
import net.ildar.dungeon.map.Position;
import net.ildar.dungeon.provider.MapProvider;

import javax.inject.Inject;
import java.util.Optional;
import java.util.Random;

/**
 * Обработчик передвижений игрока.
 */
@RequiredArgsConstructor(onConstructor_ = {@Inject})
@Log4j2
public class GamerMover {
    private final Gamer gamer;
    private final Configuration configuration;
    private final CollisionDetector collisionDetector;
    private final MapProvider mapProvider;

    /**
     * Изменить положение на карте в зависимости от нажатой клавиши.
     *
     * @param inputKey нажатая клавиша
     */
    public void move(@NonNull InputKey inputKey) {
        double moveAngle;
        switch (inputKey) {
            case LEFT:
                moveAngle = gamer.getViewAngle() - 90;
                break;
            case BACKWARD:
                moveAngle = gamer.getViewAngle() + 180;
                break;
            case RIGHT:
                moveAngle = gamer.getViewAngle() + 90;
                break;
            case FORWARD:
                moveAngle = gamer.getViewAngle();
                break;
            default:
                return;
        }

        // проверка пересечения новой позиции с объектами на карте
        Position newGamerPosition = gamer.getPosition().shift(
                configuration.getGamerMovingStep(),
                moveAngle);
        Optional<MapObject> interferingMapObject = collisionDetector.getInterferingMapObject(mapProvider.getDungeonMap(), newGamerPosition);
        if (interferingMapObject.isEmpty()) {
            gamer.setPosition(newGamerPosition);
            log.info("Changed position to {}, view angle to {}",
                    gamer.getPosition(), gamer.getViewAngle());
        } else {
            log.info("Gamer can't move to {}", newGamerPosition);
        }

    }

    /**
     * Повернуть игрока в соответствии с передвижением мыши на указанное количество пикселей по горизонтали вправо.
     *
     * @param diff количество пикселей смещения мыши по горизонтали вправо
     */
    public void turn(int diff) {
        if (diff == 0) {
            return;
        }
        double newViewAngle = gamer.getViewAngle() + diff * configuration.getMouseSensitivity();
        if (newViewAngle < 0) {
            newViewAngle += 360;
        }
        if (newViewAngle >= 360) {
            newViewAngle -= 360;
        }
        gamer.setViewAngle(newViewAngle);
        log.info("Gamer turned for {} degrees", newViewAngle);
    }

    /**
     * Телепортировать игрока на случайное свободное место на карте.
     */
    public void random() {
        gamer.setPosition(collisionDetector.findRandomEmptySpot(mapProvider.getDungeonMap(), gamer.getRadius()));
        gamer.setViewAngle(new Random().nextDouble() * 360);
        log.info("Changed position to {}, view angle to {}",
                gamer.getPosition(), gamer.getViewAngle());
    }
}

