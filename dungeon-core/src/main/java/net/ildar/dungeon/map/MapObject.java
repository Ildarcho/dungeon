package net.ildar.dungeon.map;

import lombok.NonNull;

/**
 * Объект на игровой карте.
 */
public interface MapObject{

    /**
     * Получить позицию объекта.
     */
    @NonNull
    Position getPosition();

    /**
     * Проверяет пересечение объекта с точкой.
     *
     * @param position позиция точки.
     * @return true при наличии пересечения
     */
    boolean interferes(@NonNull Position position);
}
