package net.ildar.dungeon.map;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.ildar.dungeon.CollisionDetector;
import net.ildar.dungeon.config.Configuration;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Генератор игровой карты.
 */
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class MapGenerator {

    private final Configuration configuration;
    private final CollisionDetector collisionDetector;

    /**
     * Сгенерировать игровую карту.
     */
    @NonNull
    public DungeonMap generateMap() {
        Set<InmovableMapObject> inmovableMapObjects = new HashSet<>();
        addWallsOnEdges(inmovableMapObjects);
        addRandomWalls(inmovableMapObjects);

        DungeonMap dungeonMap = new DungeonMap(
                configuration.getGeneratorMapHeight(),
                configuration.getGeneratorMapWidth(),
                inmovableMapObjects,
                new HashSet<>());
        addRandomMobs(dungeonMap);
        return dungeonMap;
    }

    /**
     * Добавить к множеству объектов случайный стены.
     */
    private void addRandomWalls(@NonNull Set<InmovableMapObject> mapObjects) {
        int numberOfObjects = (int) (configuration.getGeneratorMapWidth() * configuration.getGeneratorMapHeight() * configuration.getGeneratorStaticMapObjectsPercentage());

        Random random = new Random();
        while (mapObjects.size() < numberOfObjects) {
            double randomX;
            double randomY;
            if (configuration.isGeneratorWallSizeRandomizing()) {
                randomX = random.nextDouble() * (configuration.getGeneratorMapWidth() - 1);
                randomY = random.nextDouble() * (configuration.getGeneratorMapHeight() - 1);
            } else {
                randomX = random.nextInt((int) configuration.getGeneratorMapWidth());
                randomY = random.nextInt((int) configuration.getGeneratorMapHeight());
            }
            Position position = new Position(randomX, randomY);
            if (mapObjects.stream().map(MapObject::getPosition).anyMatch(p -> p.equals(position))) {
                continue;
            }
            Wall wall = new Wall(position, configuration.getWallSize(), configuration.getWallSize());
            if (configuration.isGeneratorWallSizeRandomizing()) {
                wall.setWidth(random.nextDouble() * (wall.getWidth() - 0.2) + 0.2);
                wall.setLength(random.nextDouble() * (wall.getLength() - 0.2) + 0.2);
            }
            mapObjects.add(wall);
        }
    }

    /**
     * Добавить к множеству объектов стены по краям карты.
     */
    private void addWallsOnEdges(@NonNull Set<InmovableMapObject> mapObjects) {
        for (int x = 0; x < (int) configuration.getGeneratorMapWidth(); x++) {
            mapObjects.add(new Wall(
                    new Position(x, 0),
                    configuration.getWallSize(),
                    configuration.getWallSize()));
            mapObjects.add(new Wall(
                    new Position(x, (int) configuration.getGeneratorMapHeight() - 1),
                    configuration.getWallSize(),
                    configuration.getWallSize()));
        }
        for (int y = 1; y < configuration.getGeneratorMapHeight() - 1; y++) {
            mapObjects.add(new Wall(
                    new Position(0, y),
                    configuration.getWallSize(),
                    configuration.getWallSize()));
            mapObjects.add(new Wall(
                    new Position(configuration.getGeneratorMapWidth() - 1, y),
                    configuration.getWallSize(),
                    configuration.getWallSize()));
        }
    }

    /**
     * Добавить на карту случайно расположенных монстров.
     */
    public void addRandomMobs(@NonNull DungeonMap dungeonMap) {
        Random random = new Random();
        while (dungeonMap.getMovableObjectSet().size() < configuration.getGeneratorMobNumber()) {
            Position position = new Position(random.nextInt((int) configuration.getGeneratorMapWidth()),
                    random.nextInt((int) configuration.getGeneratorMapHeight()));
            Mob mob = new Mob(position, configuration.getMobSize(), configuration.getMobHeight(), Mob.State.ALIVE);
            if (collisionDetector.getInterferingMapObject(dungeonMap, mob).isPresent()) {
                continue;
            }
            dungeonMap.addMovableMapObject(mob);
        }
    }
}
