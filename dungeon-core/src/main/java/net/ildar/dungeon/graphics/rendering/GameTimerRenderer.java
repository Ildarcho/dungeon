package net.ildar.dungeon.graphics.rendering;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.ildar.dungeon.GameTimer;
import net.ildar.dungeon.config.Colors;
import net.ildar.dungeon.config.Configuration;

import javax.inject.Inject;
import java.awt.*;
import java.time.Duration;

/**
 * Рендерер таймера
 */
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class GameTimerRenderer implements Renderer {
    private final GameTimer gameTimer;
    private final Configuration configuration;

    @Override
    public void render(@NonNull Graphics g) {
        int width = (int) g.getClipBounds().getWidth();
        int height = (int) g.getClipBounds().getHeight();

        Duration elapsedTime = gameTimer.getElapsedTime();
        g.setColor(Colors.TIMER_COLOR);
        g.setFont(Font.decode("SansSerif").deriveFont(configuration.getTimerFontSize()));
        String formattedDuration = formatDuration(elapsedTime);
        int stringWidth = g.getFontMetrics().stringWidth(formattedDuration);
        int stringDrawPosition = 0;
        if (width > stringWidth + configuration.getTimerMargin()) {
            stringDrawPosition = width - stringWidth - configuration.getTimerMargin();
        }
        g.drawString(formattedDuration, stringDrawPosition, height / 2);
    }

    /**
     * Получить строку для отображения прошедшего времени на экране
     *
     * @param duration промежуток времени
     * @return строковое представление промежутка времени
     */
    private String formatDuration(Duration duration) {
        long minutes = duration.toMinutes();
        long seconds = duration.getSeconds() - minutes * 60;
        if (minutes > 0) {
            return String.format("%d:%02d", minutes, seconds);
        } else {
            return String.valueOf(seconds);
        }
    }
}
