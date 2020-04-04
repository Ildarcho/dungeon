package net.ildar.dungeon.map;

import lombok.NonNull;
import lombok.Value;

import java.util.Collections;
import java.util.Set;

/**
 * Игровая карта.
 */
@Value
public class DungeonMap {
    /**
     * Высота карты.
     */
    double height;

    /**
     * Ширина карты.
     */
    double width;

    /**
     * Множество статических объектов на карте.
     */
    @NonNull
    Set<InmovableMapObject> staticObjectSet;

    /**
     * Множество передвигающихся объектов на карте.
     */
    @NonNull
    Set<MovableMapObject> movableObjectSet;

    /**
     * Удалить мобильный объект из соответствующего множества.
     */
    public synchronized void removeMovableMapObject(@NonNull MovableMapObject movableMapObject) {
        this.movableObjectSet.remove(movableMapObject);
    }

    /**
     * Прибавить мобильный объект к соответствующему множеству.
     */
    public synchronized void addMovableMapObject(@NonNull MovableMapObject movableMapObject) {
        this.movableObjectSet.add(movableMapObject);
    }

    /**
     * Запретить неконтролируемое изменение множества мобильных объектов.
     */
    public synchronized Set<MovableMapObject> getMovableObjectSet() {
        return Collections.unmodifiableSet(movableObjectSet);
    }
}
