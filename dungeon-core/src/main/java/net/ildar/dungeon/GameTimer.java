package net.ildar.dungeon;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Обработчик игрового таймера.
 */
@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class GameTimer {

    /**
     * Флаг остановки таймера.
     */
    private boolean stopped;

    /**
     * Время старта игрового таймера.
     */
    private LocalDateTime startTime;

    /**
     * Время остановки таймера.
     */
    private LocalDateTime stopTime;

    /**
     * Сбросить таймер.
     */
    public synchronized void reset() {
        stopped = false;
        startTime = LocalDateTime.now();
    }

    /**
     * Получить количество прошедшего времени.
     */
    @NonNull
    public synchronized Duration getElapsedTime() {
        if (stopped) {
            return Duration.between(startTime, stopTime);
        } else {
            return Duration.between(startTime, LocalDateTime.now());
        }
    }

    /**
     * Остановить таймер.
     */
    public synchronized void stop() {
        if (stopped) {
            return;
        }
        this.stopTime = LocalDateTime.now();
        this.stopped = true;
    }
}
