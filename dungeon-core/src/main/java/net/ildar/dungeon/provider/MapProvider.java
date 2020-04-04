package net.ildar.dungeon.provider;

import lombok.RequiredArgsConstructor;
import net.ildar.dungeon.map.DungeonMap;
import net.ildar.dungeon.map.MapGenerator;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class MapProvider {
    private final MapGenerator mapGenerator;

    private DungeonMap dungeonMap;

    public DungeonMap getDungeonMap() {
        if (dungeonMap == null) {
            dungeonMap = mapGenerator.generateMap();
        }
        return dungeonMap;
    }

    public void regenerateMap() {
        this.dungeonMap = mapGenerator.generateMap();
    }
}
