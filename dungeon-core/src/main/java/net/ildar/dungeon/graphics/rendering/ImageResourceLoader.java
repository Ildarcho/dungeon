package net.ildar.dungeon.graphics.rendering;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;

/**
 * Утилитарный класс для загрузки текстур.
 */
@Log4j2
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class ImageResourceLoader {

    /**
     * Получить текстуру по пути в classpath.
     *
     * @param path путь к текстуре в classpath
     * @return Optional текстуры
     */
    @NonNull
    public Optional<BufferedImage> loadImageResource(@NonNull String path) {
        URL resource = ImageResourceLoader.class.getClassLoader().getResource(path);
        if (resource == null) {
            return Optional.empty();
        } else {
            return loadImageResource(resource);
        }
    }

    /**
     * Получить текстуру по URL ресурса.
     *
     * @param resourceUrl URL ресурса
     * @return Optional текстуры.
     */
    @NonNull
    public Optional<BufferedImage> loadImageResource(@NonNull URL resourceUrl) {
        BufferedImage image = null;
        try {
            image = ImageIO.read(resourceUrl);
        } catch (IOException e) {
            log.error("Can't load image resource", e);
        }

        return Optional.ofNullable(image);
    }

}
