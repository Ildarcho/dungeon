package net.ildar.dungeon.runtime;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import net.ildar.dungeon.Gun;
import net.ildar.dungeon.config.Configuration;
import net.ildar.dungeon.config.Configuration800x600;
import net.ildar.dungeon.map.Gamer;
import net.ildar.dungeon.map.Mob;
import net.ildar.dungeon.map.Position;

import javax.inject.Singleton;

@Module
public interface GameModule {
    @Singleton
    @Binds
    Configuration getConfiguration(Configuration800x600 configuration);

    @Singleton
    @Provides
    static Gamer getGamer(Configuration configuration) {
        return new Gamer(
                new Position(0,0),
                configuration.getMobSize(),
                configuration.getMobHeight(),
                Mob.State.ALIVE,
                0);
    }

    @Singleton
    @Provides
    static Gun getGun() {
        return new Gun();
    }
}
