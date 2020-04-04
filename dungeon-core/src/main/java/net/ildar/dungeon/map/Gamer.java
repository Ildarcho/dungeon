package net.ildar.dungeon.map;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

/**
 * Игрок.
 */
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Gamer extends Mob {

    /**
     * Угол взгляда игрока. Отсчитывается от северного направления по часовой стрелке.
     */
    @Getter
    @Setter
    private double viewAngle;

    public Gamer(@NonNull Position position, double radius, double height, @NonNull State state, double viewAngle) {
        super(position, radius, height, state);
        this.viewAngle = viewAngle;
    }
}
