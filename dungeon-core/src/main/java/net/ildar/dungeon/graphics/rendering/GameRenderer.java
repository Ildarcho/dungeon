package net.ildar.dungeon.graphics.rendering;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.ildar.dungeon.config.Colors;
import net.ildar.dungeon.config.Configuration;
import net.ildar.dungeon.graphics.tracing.InterceptedMovableObject;
import net.ildar.dungeon.graphics.tracing.RayTracer;
import net.ildar.dungeon.graphics.tracing.TraceResult;
import net.ildar.dungeon.map.Gamer;
import net.ildar.dungeon.map.Mob;
import net.ildar.dungeon.map.MovableMapObject;
import net.ildar.dungeon.map.Position;
import net.ildar.dungeon.map.Wall;

import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


/**
 * Главный рендерер игры для отображения 3д-объектов на экране.
 */
@Log4j2
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class GameRenderer implements Renderer {
    /**
     * Загруженные текстуры мобов.
     */
    @NonNull
    private static final Map<String, BufferedImage> cachedMobSprites = new HashMap<>();
    
    private final Gamer gamer;
    private final RayTracer rayTracer;
    private final Configuration configuration;
    private final ImageResourceLoader imageResourceLoader;

    @Override
    public void render(@NonNull Graphics g) {
        int width = (int) g.getClipBounds().getWidth();
        int height = (int) g.getClipBounds().getHeight();

        for (int x = 0; x < width; x += configuration.getViewColumnWidth()) {
            // производим трассировку
            double tracingAngle = gamer.getViewAngle() - configuration.getFov() / 2.0 + ((double) x) / width * configuration.getFov();
            TraceResult traceResult = rayTracer.trace(gamer.getPosition(), tracingAngle);

            // если на пути луча трассировки ничего не обнаружено, рисуем небо и пол и переходим к следующей трассировке
            if (traceResult.getDistance() >= configuration.getViewDistance()) {
                g.setColor(Colors.SKY_COLOR);
                g.fillRect(x, 0, configuration.getViewColumnWidth(), height / 2);
                renderFloor(g, x, height / 2);
                continue;
            }

            // определяем границы рендеринга статического объекта
            int objectTopEdge = (int) (height / 2.0 * (configuration.getScreenDistance() - 1 / traceResult.getDistance()));
            if (objectTopEdge < 0) {
                objectTopEdge = 0;
            }
            int objectBottomEdge = (int) (height / 2.0 * (configuration.getScreenDistance() + 1 / traceResult.getDistance()));
            if (objectBottomEdge > height) {
                objectBottomEdge = height;
            }

            // рисуем небо
            g.setColor(Colors.SKY_COLOR);
            g.fillRect(x, 0, configuration.getViewColumnWidth(), objectTopEdge);

            // рисуем статический объект
            if (traceResult.getTouchedObject() instanceof Wall) {
                g.setColor(Colors.WALL_COLOR);
            } else {
                g.setColor(Colors.UNKNOWN_OBJECT_COLOR);
            }
            g.setColor(shade(g.getColor(), traceResult.getDistance() / configuration.getViewDistance()));
            g.fillRect(x, objectTopEdge, configuration.getViewColumnWidth(), objectBottomEdge - objectTopEdge);

            // рисуем пол
            renderFloor(g, x, objectBottomEdge);

            // рисуем встреченные на пути луча трассировки мобильные объекты
            renderPartiallyTransparentObjects(g, x, traceResult.getInterceptedMovableObjects());
        }

        // рисуем прицел
        g.setColor(Colors.CROSSHAIR_COLOR);
        g.fillRect((int) (width / 2. - configuration.getCrosshairThickness() / 2),
                (int) (height / 2. - configuration.getCrosshairSize() / 2),
                (int) configuration.getCrosshairThickness(),
                (int) configuration.getCrosshairSize());
        g.fillRect((int) (width / 2. - configuration.getCrosshairSize() / 2),
                (int) (height / 2. - configuration.getCrosshairThickness() / 2),
                (int) configuration.getCrosshairSize(),
                (int) configuration.getCrosshairThickness());
    }

    /**
     * Нарисовать пол.
     *
     * @param g контекст рендеринга
     * @param x положение полосы рендеринга.
     * @param y верхняя граница отрисовки пола
     */
    private void renderFloor(@NonNull Graphics g, int x, int y) {
        int height = (int) g.getClipBounds().getHeight();
        if (g instanceof Graphics2D) {
            // настраиваем градиент для затемнения более далеких участков пола
            GradientPaint gradientPaint = new GradientPaint(
                    x + configuration.getViewColumnWidth() / 2f, height / 2f,
                    Color.BLACK,
                    x + configuration.getViewColumnWidth() / 2f, height,
                    Colors.FLOOR_COLOR);
            ((Graphics2D) g).setPaint(gradientPaint);
        } else {
            g.setColor(Colors.FLOOR_COLOR);
        }
        g.fillRect(x, y, configuration.getViewColumnWidth(), height - y);
    }

    /**
     * Отрисовать мобильные объекты на экране.
     *
     * @param g     контекст рендеринга.
     * @param x     положения полосы рендеринга.
     * @param iptos список мобильных объектов замеченных на полосе рендеринга.
     */
    private void renderPartiallyTransparentObjects(@NonNull Graphics g,
                                                   int x,
                                                   @NonNull List<InterceptedMovableObject> iptos) {
        // сортируем объекты по убыванию расстояния до них
        iptos.sort(Comparator.comparingDouble(ipto -> gamer.getPosition().distance(ipto.getMapObject().getPosition())));
        Collections.reverse(iptos);

        iptos.forEach(pto -> renderMovableMapObject(g, x, pto));
    }

    /**
     * Отрисовать мобильный объект на экране.
     *
     * @param g   контекст рендеринга.
     * @param x   положение полосы рендеринга.
     * @param imo мобильный объект.
     */
    private void renderMovableMapObject(@NonNull Graphics g, int x, @NonNull InterceptedMovableObject imo) {
        int width = (int) g.getClipBounds().getWidth();
        int height = (int) g.getClipBounds().getHeight();
        Position gamerPosition = gamer.getPosition();

        // точка пересечения луча трассировки с мобом
        Position objectPartLeftEdgePosition = findViewLineIntersection(
                imo.getMapObject().getPosition(),
                imo.getViewAngle(),
                gamerPosition);
        // точка пересечения следующего луча трассировки с мобом
        Position objectPartRightEdgePosition = findViewLineIntersection(
                imo.getMapObject().getPosition(),
                imo.getViewAngle() + (double) configuration.getViewColumnWidth() / width * configuration.getFov(),
                gamerPosition);
        // крайняя левая точка моба
        Position objectLeftEdgePosition = imo.getMapObject().getPosition().shift(
                imo.getMapObject().getRadius(),
                imo.getViewAngle() - 90);

        // загружаем текстуру моба
        Optional<BufferedImage> mobSpriteOptional = getMobSprite(imo.getMapObject());
        if (mobSpriteOptional.isEmpty()) {
            return;
        }
        BufferedImage imoSpriteImage = mobSpriteOptional.get();

        // определяем левый край части текстуры для отрисовки
        double spriteLeftEdgeX = imoSpriteImage.getWidth()
                * objectLeftEdgePosition.distance(objectPartLeftEdgePosition)
                / (2 * imo.getMapObject().getRadius());
        if (spriteLeftEdgeX >= imoSpriteImage.getWidth() - 1) {
            spriteLeftEdgeX = imoSpriteImage.getWidth() - 1;
        }
        // определяем правый край части текстуры для отрисовки
        double spriteRightEdgeX = imoSpriteImage.getWidth()
                * objectLeftEdgePosition.distance(objectPartRightEdgePosition)
                / (2 * imo.getMapObject().getRadius());
        if (spriteRightEdgeX >= imoSpriteImage.getWidth() - 1) {
            spriteRightEdgeX = imoSpriteImage.getWidth() - 1;
        }
        if (spriteRightEdgeX < spriteLeftEdgeX) {
            log.error("Unable to raster a mob. Left sprite edge - {}, right sprite edge - {}", spriteLeftEdgeX, spriteRightEdgeX);
            return;
        }

        // определяем часть текстуры по высоте для отрисовки на экране
        double distanceToObject = gamer.getPosition().distance(imo.getMapObject().getPosition());
        double imoSpritePartHeight = imoSpriteImage.getHeight();
        if (distanceToObject < configuration.getScreenDistance()) {
            imoSpritePartHeight *= distanceToObject;
        }

        // определяем часть текстуры моба по полученным параметрам
        BufferedImage imoSpritePart = imoSpriteImage.getSubimage(
                (int) spriteLeftEdgeX,
                0,
                (int) (spriteRightEdgeX - spriteLeftEdgeX + 1),
                (int) imoSpritePartHeight);

        // определяем верхний край отрисовки текстуры
        int objectTopEdge = (int) (height / 2.0 * (configuration.getScreenDistance() + 1 / distanceToObject - 2 * imo.getMapObject().getHeight() / distanceToObject));
        if (objectTopEdge < 0) {
            objectTopEdge = 0;
        }

        // определяем нижний край отрисовки текстуры
        int objectBottomEdge = (int) (height / 2.0 * (configuration.getScreenDistance() + 1 / distanceToObject));
        if (objectBottomEdge > height) {
            objectBottomEdge = height;
        }

        // отрисовываем текстуру
        g.drawImage(imoSpritePart, x, objectTopEdge, configuration.getViewColumnWidth(), objectBottomEdge - objectTopEdge, null);
    }

    /**
     * Получить текстуру моба по его пути в classpath.
     *
     * @param movableMapObject@return Optional изображения.
     */
    private Optional<BufferedImage> getMobSprite(MovableMapObject movableMapObject) {
        String spritePath = "mob/unknownMob.png";
        if (movableMapObject instanceof Mob) {
            if (((Mob) movableMapObject).getState() == Mob.State.ALIVE) {
                spritePath = "mob/virus.png";
            } else {
                spritePath = "mob/virus_killed.png";
            }
        }
        BufferedImage mobSprite = cachedMobSprites.get(spritePath);
        if (mobSprite == null) {
            Optional<BufferedImage> mobSpriteOptional = imageResourceLoader.loadImageResource(spritePath);
            if (mobSpriteOptional.isEmpty()) {
                log.error("Unable to find a sprite with path {}", spritePath);
                return Optional.empty();
            }
            mobSprite = mobSpriteOptional.get();
            cachedMobSprites.put(spritePath, mobSprite);
        }
        return Optional.of(mobSprite);
    }

    /**
     * Найти пересечение линии взгляда с перпендикулярной ей прямой проходящей через указанную точку.
     *
     * @param pointPosition позиция удалённой точки.
     * @param viewAngle     угол взгляда, отсчитываемый от северного направления по часовой стрелке.
     * @param viewPosition  позиция точки взгляда.
     * @return позиция точки пересечения
     */
    @NonNull
    private Position findViewLineIntersection(@NonNull Position pointPosition,
                                              double viewAngle,
                                              @NonNull Position viewPosition) {
        double zeroMargin = 1E-10;
        if (Math.abs(Math.cos(viewAngle / 180 * Math.PI)) <= zeroMargin) {
            return new Position(pointPosition.getX(), viewPosition.getY());
        }

        double viewLineFactor = 1 / Math.tan(viewAngle / 180 * Math.PI);
        double viewLineFreeFactor = -(viewPosition.getX() * viewLineFactor + viewPosition.getY());
        double intersectionX = ((pointPosition.getX() / viewLineFactor) - pointPosition.getY() - viewLineFreeFactor)
                / (viewLineFactor + 1 / viewLineFactor);
        double intersectionY = ((pointPosition.getY() * viewLineFactor) - pointPosition.getX() - (viewLineFreeFactor / viewLineFactor))
                / (viewLineFactor + 1 / viewLineFactor);
        return new Position(intersectionX, intersectionY);
    }

    /**
     * Получить более тёмный цвет в соответствии с коэффициентом.
     *
     * @param color  цвет
     * @param factor коэффициент затемнения
     * @return затемнённый цвет
     */
    @NonNull
    private Color shade(@NonNull Color color, double factor) {
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();
        return new Color(
                Math.min((int) (r * (1 - factor)), 255),
                Math.min((int) (g * (1 - factor)), 255),
                Math.min((int) (b * (1 - factor)), 255),
                color.getAlpha());
    }
}
