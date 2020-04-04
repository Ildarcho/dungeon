package net.ildar.dungeon.graphics.tracing;

import lombok.NonNull;
import lombok.Value;
import net.ildar.dungeon.map.MovableMapObject;

/**
 * Мобильный объект карты, обнаруженный при трассировке.
 */
@Value
public class InterceptedMovableObject {
    /**
     * Мобильный объект.
     */
    @NonNull
    MovableMapObject mapObject;

    /**
     * Угол трассировки или взгляда на объект.
     */
    double viewAngle;
}
