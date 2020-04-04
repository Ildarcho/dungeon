package net.ildar.dungeon.map;

import lombok.NonNull;
import lombok.Value;

/**
 * Позиция объекта на карте.
 */
@Value
public class Position {
    /**
     * Координата по широте карты - горизонтали.
     */
    double x;

    /**
     * Координата по высоте карты - вертикали.
     */
    double y;

    /**
     * Получить позицию на некотором удалении от текущей позиции.
     *
     * @param length   расстояние до требуемой позиции.
     * @param rayAngle угол до требуемой позиции. отсчитываемый от северного направления по часовой стрелке.
     * @return новая позиция.
     */
    @NonNull
    public Position shift(double length, double rayAngle) {
        return new Position(
                getX() + length * Math.sin(rayAngle / 180. * Math.PI),
                getY() - length * Math.cos(rayAngle / 180. * Math.PI));
    }

    /**
     * Найти евклидово расстояние до указанной позиции.
     *
     * @param position позиция точки, до которой нужно вычислить расстояние.
     * @return евклидово расстояние.
     */
    public double distance(Position position) {
        return Math.sqrt(Math.pow(x - position.getX(), 2) + Math.pow(y - position.getY(), 2));
    }
}
