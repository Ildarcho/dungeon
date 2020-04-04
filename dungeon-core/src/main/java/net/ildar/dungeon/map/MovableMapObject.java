package net.ildar.dungeon.map;

import lombok.NonNull;

/**
 * Объект карты, позиция которого может изменяться.
 * <p>
 * Имеет форму круга с центром в {@link this#getPosition()} и радиусом {@link this#getRadius()}
 */
public abstract class MovableMapObject implements MapObject {

    /**
     * Получить идентификатор объекта.
     */
    @NonNull
    public abstract String getId();

    /**
     * Радиус мобильного объекта.
     */
    public abstract double getRadius();

    /**
     * Высота объекта.
     */
    public abstract double getHeight();

    @Override
    public boolean interferes(@NonNull Position position) {
        return getPosition().distance(position) < getRadius();
    }
}
