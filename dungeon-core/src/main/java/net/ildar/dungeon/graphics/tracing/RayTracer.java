package net.ildar.dungeon.graphics.tracing;

import lombok.RequiredArgsConstructor;
import net.ildar.dungeon.CollisionDetector;
import net.ildar.dungeon.config.Configuration;
import net.ildar.dungeon.map.MapObject;
import net.ildar.dungeon.map.MovableMapObject;
import net.ildar.dungeon.map.Position;
import net.ildar.dungeon.provider.MapProvider;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Трассировщик.
 */
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class RayTracer {
    private final MapProvider mapProvider;
    private final Configuration configuration;
    private final CollisionDetector collisionDetector;

    /**
     * Выполнить трассировку.
     *
     * @param position     исходная позиция луча трассировки
     * @param tracingAngle угол трассировки
     * @return результат выполнения трассировки.
     */
    public TraceResult trace(Position position, double tracingAngle) {
        List<InterceptedMovableObject> interceptedMovableObjects = new ArrayList<>();

        Optional<MapObject> interferingMapObject = collisionDetector.getInterferingMapObject(
                mapProvider.getDungeonMap(), position);
        // вернем результат трассировки если препятствие обнаружено на нулевом шаге.
        if (interferingMapObject.isPresent() && !(interferingMapObject.get() instanceof MovableMapObject)) {
            return new TraceResult(0, interferingMapObject.get(), interceptedMovableObjects);
        }

        double viewDistance = Math.min(
                configuration.getViewDistance(),
                Math.max(mapProvider.getDungeonMap().getWidth(), mapProvider.getDungeonMap().getHeight()));
        double currentRayLength = configuration.getTracingStep();
        while (currentRayLength <= viewDistance) {
            Position rayPosition = position.shift(currentRayLength, tracingAngle);
            // выясним находится ли на пути луча некий объект
            interferingMapObject = collisionDetector.getInterferingMapObject(mapProvider.getDungeonMap(), rayPosition);
            if (interferingMapObject.isPresent()) {
                MapObject mapObject = interferingMapObject.get();
                if (mapObject instanceof MovableMapObject) {
                    // при обнаружении мобильных объектов трассировка продолжается
                    interceptedMovableObjects.add(
                            new InterceptedMovableObject((MovableMapObject) mapObject, tracingAngle));
                    Optional<MapObject> nextImo;
                    do {
                        currentRayLength += configuration.getTracingStep();
                        nextImo = collisionDetector.getInterferingMapObject(
                                mapProvider.getDungeonMap(), position.shift(currentRayLength, tracingAngle));
                    } while (nextImo.isPresent() && nextImo.get().equals(mapObject));
                    continue;
                } else {
                    // найдено пересечение со статическим объектом, возвращаем результат трассировки
                    return new TraceResult(currentRayLength, mapObject, interceptedMovableObjects);
                }
            }
            // пересечений не обнаружено, увеличиваем длину луча трассировки
            currentRayLength += configuration.getTracingStep();
        }
        // превышена максимальная длина луча трассировки
        return new TraceResult(Double.MAX_VALUE, null, interceptedMovableObjects);
    }

}
