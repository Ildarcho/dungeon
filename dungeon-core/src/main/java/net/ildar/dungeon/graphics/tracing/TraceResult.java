package net.ildar.dungeon.graphics.tracing;

import lombok.NonNull;
import lombok.Value;
import net.ildar.dungeon.map.MapObject;

import java.util.List;

@Value
public class TraceResult {
    /**
     * Расстояние до объекта на пути луча.
     */
    double distance;

    /**
     * Объект карты на пути луча.
     * null если {@link this#distance} равен {@link Double#MAX_VALUE}
     */
    MapObject touchedObject;

    /**
     * Список мобильных объектов на пути луча трассировки.
     */
    @NonNull
    List<InterceptedMovableObject> interceptedMovableObjects;
}
