package net.ildar.dungeon;

import lombok.Data;
import lombok.NonNull;

/**
 * Оружие.
 */
@Data
public class Gun {

    /**
     * Состояние оружия.
     */
    @NonNull
    private State state = State.INACTIVE;

    /**
     * Прогресс прохождения выстрела. От 0 до 99.
     */
    double shootingProgress;

    /**
     * Прогресс прохождения перезарядки. От 0 до 99.
     */
    double reloadProgress;

    /**
     * Перечисление возможных состояний оружия.
     */
    public enum State {
        /**
         * Оружие в режиме ожидания.
         */
        INACTIVE,

        /**
         * Оружие в режиме выстрела.
         */
        SHOOTING,

        /**
         * Оружие в режиме перезарядки.
         */
        RELOADING
    }
}
