package net.ildar.dungeon.graphics.rendering;

import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import net.ildar.dungeon.Gun;
import net.ildar.dungeon.config.Configuration;

import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Рендерер оружия.
 */
@Log4j2
public class GunRenderer implements Renderer {

    private final Gun gun;
    private final Configuration configuration;
    private final ImageResourceLoader imageResourceLoader;

    /**
     * Загруженные текстуры стреляющего оружия.
     */
    private List<BufferedImage> shootingGunSprites;

    /**
     * Загруженные текстуры перезаряжающегося оружия.
     */
    private List<BufferedImage> reloadingGunSprites;

    /**
     * Текстура оружия в неактивном состоянии.
     */
    private BufferedImage stableGunSprite;

    /**
     * Флаг успешной загрузки текстур.
     */
    private boolean spritesLoadedSuccessfully;

    /**
     * Высота загруженных текстур.
     */
    private int spriteHeight;

    /**
     * Ширина загруженных текстур.
     */
    private int spriteWidth;


    @Inject
    public GunRenderer(Gun gun, Configuration configuration, ImageResourceLoader imageResourceLoader) {
        this.gun = gun;
        this.configuration = configuration;
        this.imageResourceLoader = imageResourceLoader;
        loadSprites();
        if (spritesLoadedSuccessfully) {
            this.spriteWidth = stableGunSprite.getWidth();
            this.spriteHeight = stableGunSprite.getHeight();
        }
    }

    /**
     * Загрузить из classpath изображения оружия.
     */
    private void loadSprites() {
        spritesLoadedSuccessfully = true;
        Optional<BufferedImage> stableGunSprite = imageResourceLoader.loadImageResource("gun/stable.png");
        if (stableGunSprite.isPresent()) {
            this.stableGunSprite = stableGunSprite.get();
        } else {
            spritesLoadedSuccessfully = false;
            return;
        }

        shootingGunSprites = loadSpritePack("gun/shooting");
        if (shootingGunSprites.isEmpty()) {
            log.error("Unable to load any shooting gun sprites with prefix");
            spritesLoadedSuccessfully = false;
            return;
        }

        reloadingGunSprites = loadSpritePack("gun/reloading");
        if (reloadingGunSprites.isEmpty()) {
            log.error("Unable to load any reloading gun sprites with prefix");
            spritesLoadedSuccessfully = false;
        }
    }

    /**
     * Загрузить набор спрайтов с указанным префиксом.
     * <p>
     * Будут найдены все спрайты по шаблону [prefix]_[N],
     * <p>
     * где вместо N будут подставлены натуральные числа по возрастанию начиная с 1
     *
     * @param spritePrefix префикс пути к изображению в classpath
     * @return список спрайтов с указанным префиксом
     */
    @NonNull
    private List<BufferedImage> loadSpritePack(@NonNull String spritePrefix) {
        List<BufferedImage> spritePack = new ArrayList<>();
        int shootingGunSpriteIndex = 1;
        Optional<BufferedImage> shootingGunSprite = imageResourceLoader.loadImageResource(
                String.format("%s_%d.png", spritePrefix, shootingGunSpriteIndex));
        while (shootingGunSprite.isPresent()) {
            spritePack.add(shootingGunSprite.get());
            shootingGunSpriteIndex++;
            shootingGunSprite = imageResourceLoader.loadImageResource(
                    String.format("%s_%d.png", spritePrefix, shootingGunSpriteIndex));
        }
        return spritePack;
    }

    @Override
    public void render(@NonNull Graphics g) {
        if (!spritesLoadedSuccessfully) {
            return;
        }
        Image gunSprite = getGunSprite();
        int shakingDelta = (int) (configuration.getGunSpriteHeight() * configuration.getGunShakeHeightPart() / 100 *
                Math.abs(Math.sin(System.currentTimeMillis() / 1000. * configuration.getGunShakeSpeed())));
        int spriteDrawWidth = (int) (spriteWidth * ((double) configuration.getGunSpriteHeight() / spriteHeight));
        g.drawImage(
                gunSprite,
                0,
                shakingDelta,
                spriteDrawWidth,
                configuration.getGunSpriteHeight(),
                null);
    }

    /**
     * Получить спрайт оружия, в соответствии с текущим состоянием {@link this#gun}.
     * <p>
     * Предполагается что все необходимые текстуры были успешно загружены ранее.
     *
     * @return изображение оружия.
     */
    @NonNull
    private Image getGunSprite() {
        switch (gun.getState()) {
            case SHOOTING:
                int shootingGunSpriteIndex = (int) (shootingGunSprites.size() * gun.getShootingProgress() / 100);
                return shootingGunSprites.get(shootingGunSpriteIndex);
            case RELOADING:
                int reloadingGunSpriteIndex = (int) (reloadingGunSprites.size() * gun.getReloadProgress() / 100);
                return reloadingGunSprites.get(reloadingGunSpriteIndex);
            case INACTIVE:
            default:
                return stableGunSprite;
        }
    }
}
