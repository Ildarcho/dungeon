package net.ildar.dungeon.map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

/**
 * Стена на игровой карте.
 */
@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
public class Wall extends InmovableMapObject {
    @NonNull
    private final Position position;
    private double width;
    private double length;
}
