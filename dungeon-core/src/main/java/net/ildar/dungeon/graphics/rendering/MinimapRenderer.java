package net.ildar.dungeon.graphics.rendering;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.ildar.dungeon.config.Colors;
import net.ildar.dungeon.config.Configuration;
import net.ildar.dungeon.map.Gamer;
import net.ildar.dungeon.map.InmovableMapObject;
import net.ildar.dungeon.map.MovableMapObject;
import net.ildar.dungeon.map.Position;
import net.ildar.dungeon.provider.MapProvider;

import javax.inject.Inject;
import java.awt.*;

/**
 * Рендерер игровой миникарты.
 */
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class MinimapRenderer implements Renderer {
    private final MapProvider mapProvider;
    private final Gamer gamer;
    private final Configuration configuration;

    @Override
    public void render(@NonNull Graphics g) {
        renderMap(g);
        renderGamer(g);
        renderMobs(g);
    }

    /**
     * Нарисовать мобов.
     */
    private void renderMobs(@NonNull Graphics g) {
        int width = (int) g.getClipBounds().getWidth();
        int height = (int) g.getClipBounds().getHeight();

        g.setColor(Colors.MINIMAP_MMO_COLOR);
        for (MovableMapObject mapObject : mapProvider.getDungeonMap().getMovableObjectSet()) {
            g.fillOval(
                    adjustOnWidth(width, mapObject.getPosition().getX()),
                    adjustOnHeight(height, mapObject.getPosition().getY()),
                    adjustOnWidth(width, configuration.getMinimapMobSize()),
                    adjustOnHeight(height, configuration.getMinimapMobSize()));
        }
    }

    /**
     * Нарисовать миникарту.
     */
    private void renderMap(@NonNull Graphics g) {
        int width = (int) g.getClipBounds().getWidth();
        int height = (int) g.getClipBounds().getHeight();

        g.setColor(Colors.MINIMAP_BACKGROUND_COLOR);
        g.fillRect(0, 0, width, height);

        g.setColor(Colors.MINIMAP_WALL_COLOR);
        for (InmovableMapObject mapObject : mapProvider.getDungeonMap().getStaticObjectSet()) {
            g.fillRect(
                    adjustOnWidth(width, mapObject.getPosition().getX()),
                    adjustOnHeight(height, mapObject.getPosition().getY()),
                    adjustOnWidth(width, mapObject.getWidth()),
                    adjustOnHeight(height, mapObject.getLength()));
        }
    }

    /**
     * Нарисовать игрока с сектором обзора.
     */
    private void renderGamer(@NonNull Graphics g) {
        int width = (int) g.getClipBounds().getWidth();
        int height = (int) g.getClipBounds().getHeight();

        // рисуем точку игрока
        g.setColor(Colors.MINIMAP_GAMER_COLOR);
        g.fillOval(
                adjustOnWidth(width, gamer.getPosition().getX() - configuration.getMinimapGamerSize() / 2),
                adjustOnHeight(height, gamer.getPosition().getY() - configuration.getMinimapGamerSize() / 2),
                adjustOnWidth(width, configuration.getMinimapGamerSize()),
                adjustOnWidth(width, configuration.getMinimapGamerSize()));

        // рисуем крайние лучи сектора обзора
        Position viewSectorLeftEdgeStartPosition = gamer.getPosition().shift(
                configuration.getMinimapGamerSize() / 2,
                gamer.getViewAngle() - configuration.getFov() / 2.);
        Position viewSectorLeftEdgeEndPosition = gamer.getPosition().shift(
                configuration.getMinimapGamerSize() / 2 + configuration.getMinimapViewSectorLength(),
                gamer.getViewAngle() - configuration.getFov() / 2.);
        Position viewSectorRightEdgeStartPosition = gamer.getPosition().shift(
                configuration.getMinimapGamerSize() / 2,
                gamer.getViewAngle() + configuration.getFov() / 2.);
        Position viewSectorRightEdgeEndPosition = gamer.getPosition().shift(
                configuration.getMinimapGamerSize() / 2 + configuration.getMinimapViewSectorLength(),
                gamer.getViewAngle() + configuration.getFov() / 2.);
        g.setColor(Colors.MINIMAP_VIEW_SECTOR_COLOR);
        g.drawLine(
                adjustOnWidth(width, viewSectorLeftEdgeStartPosition.getX()),
                adjustOnHeight(height, viewSectorLeftEdgeStartPosition.getY()),
                adjustOnWidth(width, viewSectorLeftEdgeEndPosition.getX()),
                adjustOnHeight(height, viewSectorLeftEdgeEndPosition.getY()));
        g.drawLine(
                adjustOnWidth(width, viewSectorRightEdgeStartPosition.getX()),
                adjustOnHeight(height, viewSectorRightEdgeStartPosition.getY()),
                adjustOnWidth(width, viewSectorRightEdgeEndPosition.getX()),
                adjustOnHeight(height, viewSectorRightEdgeEndPosition.getY()));

    }

    /**
     * Получить x-координату точки на миникарте.
     *
     * @param width ширина миникарты.
     * @param x     координата по горизонтали.
     * @return координата точки на миникарте
     */
    private int adjustOnWidth(int width, double x) {
        return (int) (x / mapProvider.getDungeonMap().getWidth() * width);
    }

    /**
     * Получить y-координату точки на миникарте.
     *
     * @param height высота миникарты.
     * @param y      координата по вертикали.
     * @return координата точки на миникарте
     */
    private int adjustOnHeight(int height, double y) {
        return (int) (y / mapProvider.getDungeonMap().getHeight() * height);
    }
}
