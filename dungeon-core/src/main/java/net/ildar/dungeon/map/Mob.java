package net.ildar.dungeon.map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

import java.util.UUID;

/**
 * Существо.
 */
@Data
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
@AllArgsConstructor
public class Mob extends MovableMapObject {
    @NonNull
    @EqualsAndHashCode.Include
    private final String id = UUID.randomUUID().toString();

    @NonNull
    private Position position;
    private double radius;
    private double height;
    @NonNull
    private State state;

    /**
     * Состояние моба.
     */
    public enum State {
        /**
         * Моб жив.
         */
        ALIVE,

        /**
         * Моб мертв.
         */
        KILLED
    }
}
