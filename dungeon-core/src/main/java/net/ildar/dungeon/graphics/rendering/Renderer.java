package net.ildar.dungeon.graphics.rendering;

import lombok.NonNull;

import java.awt.*;

/**
 * Интерфейс всех игровых рендереров.
 */
public interface Renderer {

    /**
     * Отрисовать необходимые объекты.
     *
     * @param g контекст рендеринга.
     */
    void render(@NonNull Graphics g);
}
