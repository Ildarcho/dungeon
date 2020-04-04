package net.ildar.dungeon;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.ildar.dungeon.map.DungeonMap;
import net.ildar.dungeon.map.InmovableMapObject;
import net.ildar.dungeon.map.MapObject;
import net.ildar.dungeon.map.MovableMapObject;
import net.ildar.dungeon.map.Position;

import javax.inject.Inject;
import java.util.Optional;
import java.util.Random;

@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class CollisionDetector {

    /**
     * Определить факт пересечения двух статичных объектов карты.
     */
    public boolean intersects(@NonNull InmovableMapObject o1, @NonNull InmovableMapObject o2) {
        return Math.abs(o1.getPosition().getX() - o2.getPosition().getX()) * 2
                < (o1.getWidth() + o2.getWidth())
                && Math.abs(o1.getPosition().getY() - o2.getPosition().getY()) * 2
                < (o1.getLength() + o2.getLength());
    }

    /**
     * Определить факт пересечения двух мобильных объектов карты.
     */
    public boolean intersects(@NonNull MovableMapObject o1, @NonNull MovableMapObject o2) {
        return o1.getPosition().distance(o2.getPosition())
                < o1.getRadius() + o2.getRadius();
    }

    /**
     * Определить факт пересечения мобильного и статичного объектов карты.
     *
     * @param mmo мобильный объект (круг)
     * @param imo статичный объект (прямоугольник)
     * @return true в случае пересечения объектов, false иначе
     */
    public boolean intersects(@NonNull MovableMapObject mmo, @NonNull InmovableMapObject imo) {
        // checking if circle center is inside the box
        if (imo.interferes(mmo.getPosition())) {
            return true;
        }
        // checking box corners for intersection with circle
        if (mmo.interferes(imo.getPosition())
                || mmo.interferes(imo.getPosition().shift(imo.getWidth(), 90))
                || mmo.interferes(imo.getPosition().shift(imo.getLength(), 180))
                || mmo.interferes(new Position(imo.getPosition().getX() + imo.getWidth(),
                imo.getPosition().getY() + imo.getLength()))) {
            return true;
        }
        // checking top,bottom,left and right corners of the circle for intersection with the box
        // noinspection RedundantIfStatement
        if (imo.interferes(mmo.getPosition().shift(mmo.getRadius(), 0))
                || imo.interferes(mmo.getPosition().shift(mmo.getRadius(), 90))
                || imo.interferes(mmo.getPosition().shift(mmo.getRadius(), 180))
                || imo.interferes(mmo.getPosition().shift(mmo.getRadius(), 270))) {
            return true;
        }
        return false;
    }

    /**
     * Получить объект карты включающий указанную точку.
     *
     * @param position позиция точки на карте.
     * @return Optional объекта карты.
     */
    @NonNull
    public Optional<MapObject> getInterferingMapObject(@NonNull DungeonMap dungeonMap, @NonNull Position position) {
        Optional<InmovableMapObject> interceptedImo = dungeonMap.getStaticObjectSet().stream()
                .filter(so -> so.interferes(position)).findAny();
        if (interceptedImo.isPresent()) {
            return Optional.of(interceptedImo.get());
        }
        Optional<MovableMapObject> interceptedMmo = dungeonMap.getMovableObjectSet().stream()
                .filter(mo -> mo.interferes(position)).findAny();
        return Optional.ofNullable(interceptedMmo.orElse(null));
    }

    /**
     * Получить объект карты пересекающийся с другим объектом.
     *
     * @param mapObject некий объект на карте.
     * @return Optional объекта карты.
     */
    @NonNull
    public Optional<MapObject> getInterferingMapObject(@NonNull DungeonMap dungeonMap, @NonNull MapObject mapObject) {
        if (mapObject instanceof InmovableMapObject) {
            Optional<InmovableMapObject> iimo = dungeonMap.getStaticObjectSet().stream()
                    .filter(imo -> intersects(imo, (InmovableMapObject) mapObject))
                    .findAny();
            if (iimo.isPresent()) {
                return Optional.of(iimo.get());
            }
            Optional<MovableMapObject> immo = dungeonMap.getMovableObjectSet().stream()
                    .filter(mmo -> intersects(mmo, (InmovableMapObject) mapObject)).findAny();
            return Optional.ofNullable(immo.orElse(null));
        } else if (mapObject instanceof MovableMapObject) {
            Optional<MovableMapObject> immo = dungeonMap.getMovableObjectSet().stream().filter(mmo ->
                    intersects(mmo, (MovableMapObject) mapObject))
                    .findAny();
            if (immo.isPresent()) {
                return Optional.of(immo.get());
            }
            Optional<InmovableMapObject> iimo = dungeonMap.getStaticObjectSet().stream()
                    .filter(imo -> intersects((MovableMapObject) mapObject, imo)).findAny();
            return Optional.ofNullable(iimo.orElse(null));
        } else {
            return getInterferingMapObject(dungeonMap, mapObject.getPosition());
        }
    }

    /**
     * Получить пустую позицию на игровой карте.
     *
     * @param spotRadius радиус пустого места для поиска на карте.
     * @return позиция пустого места нужного радиуса
     */
    @NonNull
    public Position findRandomEmptySpot(@NonNull DungeonMap dungeonMap, double spotRadius) {
        Random random = new Random();
        GhostObject ghostObject = new GhostObject();
        ghostObject.radius = spotRadius;

        while (true) {
            ghostObject.setPosition(new Position(
                    random.nextDouble() * (dungeonMap.getWidth() - spotRadius),
                    random.nextDouble() * (dungeonMap.getHeight() - spotRadius)));
            if (getInterferingMapObject(dungeonMap, ghostObject).isPresent()) {
                continue;
            }
            return ghostObject.getPosition();
        }
    }

    /**
     * Вспомогательный мобильный объект для поиска вакантных мест на карте.
     */
    @Data
    @EqualsAndHashCode(callSuper = false)
    private static class GhostObject extends MovableMapObject {
        double radius;
        Position position;
        double height = 1;
        String textureResourcePath;
        String id = "";
    }
}
