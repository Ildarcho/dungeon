package net.ildar.dungeon.runtime;

import dagger.Component;
import net.ildar.dungeon.graphics.GameWindow;

import javax.inject.Singleton;

@Singleton
@Component(modules = GameModule.class)
public interface GameComponent {
    GameWindow getGameWindow();
}
