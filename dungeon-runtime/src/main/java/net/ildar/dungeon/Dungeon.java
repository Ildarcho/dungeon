package net.ildar.dungeon;

import net.ildar.dungeon.graphics.GameWindow;
import net.ildar.dungeon.runtime.DaggerGameComponent;

import java.awt.*;

/**
 * Точка входа в игру с псевдо-3д графикой.
 */
public class Dungeon {

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            GameWindow gameWindow = DaggerGameComponent.builder().build().getGameWindow();
            gameWindow.setVisible(true);
        });
    }
}
