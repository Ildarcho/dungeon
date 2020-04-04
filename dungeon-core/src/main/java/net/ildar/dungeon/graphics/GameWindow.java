package net.ildar.dungeon.graphics;

import lombok.NonNull;
import net.ildar.dungeon.GameTimer;
import net.ildar.dungeon.MobKiller;
import net.ildar.dungeon.config.Configuration;
import net.ildar.dungeon.graphics.rendering.GameRenderer;
import net.ildar.dungeon.graphics.rendering.GameTimerRenderer;
import net.ildar.dungeon.graphics.rendering.GunRenderer;
import net.ildar.dungeon.graphics.rendering.MinimapRenderer;
import net.ildar.dungeon.graphics.rendering.Renderer;
import net.ildar.dungeon.input.GameMouseListener;
import net.ildar.dungeon.input.GamerMover;
import net.ildar.dungeon.input.GunShooter;
import net.ildar.dungeon.input.InputKey;
import net.ildar.dungeon.input.KeyboardListener;
import net.ildar.dungeon.input.MouseObserver;
import net.ildar.dungeon.provider.MapProvider;

import javax.inject.Inject;
import javax.swing.*;
import java.awt.*;
import java.time.Duration;

/**
 * Игровое окно.
 */
public class GameWindow extends JFrame {

    private final Renderer gameRenderer;
    private final Renderer minimapRenderer;
    private final Renderer gunRenderer;

    private final KeyboardListener keyboardListener;
    private final GameMouseListener mouseListener;
    private final MouseObserver mouseObserver;
    private final GamerMover gamerMover;
    private final GunShooter gunShooter;
    private final MobKiller mobKiller;
    private final GameTimer gameTimer;
    private final GameTimerRenderer gameTimerRenderer;
    private final Configuration configuration;
    private final MapProvider mapProvider;

    @Inject
    public GameWindow(GameRenderer gameRenderer,
                      MinimapRenderer minimapRenderer,
                      GunRenderer gunRenderer,
                      KeyboardListener keyboardListener,
                      GameMouseListener mouseListener,
                      GamerMover gamerMover,
                      GunShooter gunShooter,
                      MobKiller mobKiller,
                      GameTimer gameTimer,
                      GameTimerRenderer gameTimerRenderer,
                      Configuration configuration,
                      MapProvider mapProvider) {
        this.gameRenderer = gameRenderer;
        this.minimapRenderer = minimapRenderer;
        this.gunRenderer = gunRenderer;
        this.keyboardListener = keyboardListener;
        this.mouseListener = mouseListener;
        this.mouseObserver = new MouseObserver(this);
        this.gamerMover = gamerMover;
        this.gunShooter = gunShooter;
        this.mobKiller = mobKiller;
        this.gameTimer = gameTimer;
        this.gameTimerRenderer = gameTimerRenderer;
        this.configuration = configuration;
        this.mapProvider = mapProvider;
        init();
    }

    private void init() {
        // задаем заголовок игрового окна
        setTitle(configuration.getTitle());
        // фиксируем размер игрового окна
        setResizable(false);
        // важно знать статус фокусировки на игровом окне, соответственно настраиваем игровое окно
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        // Добавляем на окно JPanel, рисовать все будем в нем. только так мы получим двойную буферизацию кадров.
        add(new GamePanel());
        // устанавливаем оптимальный размер игрового окна
        pack();
        // при закрытии окна весь процесс игры должен завершиться
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // добавляем слушателя для событий с клавиатуры
        addKeyListener(keyboardListener);
        // настраиваем слушатель глобальных событий мыши
        this.mouseObserver.addMouseMotionListener(mouseListener);
        this.mouseObserver.start();

        // создаём таймер для отрисовки кадров игры
        Timer drawTimer = new Timer(1000 / configuration.getMaxFps(), e -> {
            if (mouseListener.isLeftMouseClicked()) {
                gunShooter.shoot();
            } else {
                gunShooter.update();
            }
            keyboardListener.getPressedKeys().forEach(gamerMover::move);
            keyboardListener.getTypedKeys().forEach(inputKey -> {
                if (inputKey == InputKey.SHOOT) {
                    gunShooter.shoot();
                } else if (inputKey == InputKey.RESET) {
                    mapProvider.regenerateMap();
                    gameTimer.reset();
                    gamerMover.random();
                }
            });
            gamerMover.turn(mouseListener.getDiff());
            mobKiller.updateKilledMobStates();
            if (mobKiller.allMobsAreDead()) {
                gameTimer.stop();
            }
            repaint();
        });
        drawTimer.start();

        // перемещаем игрока на случайную позицию карты.
        gamerMover.random();
        // стартуем таймер
        gameTimer.reset();
    }

    /**
     * Панель для рендеринга игры.
     */
    private class GamePanel extends JPanel {
        GamePanel() {
            setPreferredSize(new Dimension(configuration.getWindowWidth(), configuration.getWindowHeight()));
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);

            gameRenderer.render(g);
            minimapRenderer.render(g.create(0, 0, configuration.getMinimapWidth(), configuration.getMinimapHeight()));
            int gunPositionX = configuration.getWindowWidth() * 3 / 5;
            int gunPositionY = configuration.getWindowHeight() - configuration.getGunSpriteHeight();
            gunRenderer.render(g.create(
                    gunPositionX,
                    gunPositionY,
                    configuration.getWindowWidth() - gunPositionX,
                    configuration.getGunSpriteHeight()));
            if (mobKiller.allMobsAreDead()) {
                renderVictoryMessage(g);
            } else {
                gameTimerRenderer.render(g.create(
                        (int) (configuration.getWindowWidth() - configuration.getTimerWidth()),
                        0,
                        (int) configuration.getTimerWidth(),
                        (int) configuration.getTimerHeight()
                ));
            }
            Toolkit.getDefaultToolkit().sync();
        }

        private void renderVictoryMessage(Graphics g) {
            g.setColor(Color.GREEN);
            g.setFont(Font.decode("SansSerif").deriveFont(50f));
            @NonNull Duration elapsedTime = gameTimer.getElapsedTime();
            int lineHeight = g.getFontMetrics().getHeight();
            int textPositionY = configuration.getWindowHeight() / 2;
            String resultMessage = String.format("Твой результат: %.2f секунд!",
                    elapsedTime.getSeconds() + elapsedTime.getNano() / 1E9);
            int lineWidth = g.getFontMetrics().stringWidth(resultMessage);
            g.drawString(resultMessage,
                    configuration.getWindowWidth() / 2 - lineWidth / 2,
                    textPositionY);
            textPositionY += lineHeight;
            g.setFont(g.getFont().deriveFont(20f));
            String whatNextMessage = "Нажми R если хочешь повторить еще раз";
            lineWidth = g.getFontMetrics().stringWidth(whatNextMessage);
            g.drawString(whatNextMessage,
                    configuration.getWindowWidth() / 2 - lineWidth / 2,
                    textPositionY);
        }
    }
}
