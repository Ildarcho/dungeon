package net.ildar.dungeon.input;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Игровой адаптер событий мыши.
 */
@Log4j2
@RequiredArgsConstructor(onConstructor_ = {@Inject})
@Singleton
public class GameMouseListener {
    /**
     * Суммарное смещение мыши по горизонтали направо.
     */
    private int diffSum;

    /**
     * Статус нажатия левой кнопки мыши.
     */
    private boolean leftMouseClicked;

    /**
     * Зарегистрировать движение мыши на указанное количество пикселей по горизонтали направо.
     */
    public synchronized void mouseMoved(int diff) {
        diffSum += diff;
        log.info("Mouse moved {} pixels", diff);
    }

    /**
     * Зарегистрировать нажатие левой кнопки мыши.
     */
    public synchronized void leftMouseClicked() {
        log.info("Left mouse button clicked");
        this.leftMouseClicked = true;
    }

    /**
     * Получить суммарное передвижение мыши с последнего вызова метода.
     * <p>
     * Накопленное суммарное смещение мыши обнуляется после вызова данного метода.
     */
    public synchronized int getDiff() {
        int diff = this.diffSum;
        this.diffSum = 0;
        return diff;
    }

    /**
     * Получить статус нажатия левой кнопки мыши с момент прошлого вызова метода.
     * <p>
     * Статус нажатия левой кнопки мыши обнуляется после вызова данного метода.
     */
    public synchronized boolean isLeftMouseClicked() {
        boolean currentState = this.leftMouseClicked;
        this.leftMouseClicked = false;
        return currentState;
    }
}
