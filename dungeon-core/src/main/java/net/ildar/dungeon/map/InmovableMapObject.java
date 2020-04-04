package net.ildar.dungeon.map;

import lombok.NonNull;

/**
 * Статичный объект игровой карты. Представляет собой ориентированный по осям координат прямоугольник.
 */
public abstract class InmovableMapObject implements MapObject {
    /**
     * Ширина объекта (по горизонтали).
     */
    public abstract double getWidth();

    /**
     * Длина объекта (по вертикали).
     */
    public abstract double getLength();

    @Override
    public boolean interferes(@NonNull Position position) {
        return getPosition().getX() < position.getX() &&
                getPosition().getX() + getWidth() > position.getX() &&
                getPosition().getY() < position.getY() &&
                getPosition().getY() + getLength() > position.getY();
    }
}
