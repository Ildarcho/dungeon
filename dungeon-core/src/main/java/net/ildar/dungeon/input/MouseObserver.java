package net.ildar.dungeon.input;

import lombok.NonNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.HashSet;
import java.util.Set;

/**
 * Обработки глобальных событий мыши.
 * <p>
 * Используется вместо стандартного механизма {@link MouseMotionListener}
 * так как событие мыши не передаются в программу если курсор не находится поверх игрового окна.
 */
public class MouseObserver extends MouseAdapter implements FocusListener, ActionListener {
    /**
     * Таймаут опроса положения мыши.
     */
    private static final int DELAY = 10;

    /**
     * Флаг активности.
     */
    private boolean started;

    /**
     * Флаг фокусировки игрового окна, при отсутствии фокусировки передвижения мыши игнорируются.
     */
    private boolean focused;

    /**
     * Таймер для опросов положения мыши.
     */
    private Timer timer;

    /**
     * Множество обработчиков событий мыши.
     */
    private final Set<GameMouseListener> mouseMotionListeners = new HashSet<>();

    /**
     * Последняя позиция мыши на экране.
     */
    private Point lastPoint = MouseInfo.getPointerInfo().getLocation();

    public MouseObserver(@NonNull Component component) {
        timer = new Timer(DELAY, this);
        component.addFocusListener(this);
        component.addMouseListener(this);
        focused = component.hasFocus();
    }

    /**
     * Начать обработку глобальных событий мыши.
     */
    public void start() {
        started = true;
        if (focused) {
            timer.start();
        }
    }

    /**
     * Остановить обработку.
     */
    public void stop() {
        started = false;
        timer.stop();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            mouseMotionListeners.forEach(GameMouseListener::leftMouseClicked);
        }
    }

    /**
     * Добавить обработчик событий мыши.
     */
    public void addMouseMotionListener(GameMouseListener listener) {
        synchronized (mouseMotionListeners) {
            mouseMotionListeners.add(listener);
        }
    }

    /**
     * Удалить обработчик событий мыши.
     */
    public void removeMouseMotionListener(GameMouseListener listener) {
        synchronized (mouseMotionListeners) {
            mouseMotionListeners.remove(listener);
        }
    }

    /**
     * Сообщить о передвижении мыши всем обработчикам событий мыши.
     */
    private void fireMouseMotionEvent(int diff) {
        synchronized (mouseMotionListeners) {
            for (final GameMouseListener listener : mouseMotionListeners) {
                SwingUtilities.invokeLater(() -> listener.mouseMoved(diff));
            }
        }
    }

    @Override
    public void focusGained(FocusEvent e) {
        focused = true;
        lastPoint = MouseInfo.getPointerInfo().getLocation();
        if (started) {
            timer.start();
        }
    }

    @Override
    public void focusLost(FocusEvent e) {
        focused = false;
        timer.stop();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Point point = MouseInfo.getPointerInfo().getLocation();

        if (!point.equals(lastPoint)) {
            fireMouseMotionEvent(point.x - lastPoint.x);
        }

        lastPoint = point;
    }
}