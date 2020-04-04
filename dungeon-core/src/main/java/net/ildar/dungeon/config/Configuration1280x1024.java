package net.ildar.dungeon.config;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.inject.Inject;

/**
 * Конфигурация игры под разрешение 800 х 600.
 */
@Data
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class Configuration1280x1024 implements Configuration {
    private final String title = "Dungeon";
    private final int windowWidth = 1280;
    private final int windowHeight = 1024;
    private final int minimapHeight = 200;
    private final int minimapWidth = 200;
    private final int maxFps = 50;
    private final int fov = 60;
    private final int viewColumnWidth = 10;
    private final int screenDistance = 1;
    private final int viewDistance = 20;
    private final double minimapGamerSize = 0.7;
    private final double minimapMobSize = 0.7;
    private final double minimapViewSectorLength = 5;
    private final double crosshairSize = 40;
    private final double crosshairThickness = 4;
    private final double timerHeight = 100;
    private final double timerWidth = 150;
    private final float timerFontSize = 40;
    private final int timerMargin = 20;
    private final double tracingStep = 0.06;
    private final double generatorMapWidth = 20;
    private final double generatorMapHeight = 20;
    private final double generatorStaticMapObjectsPercentage = 0.3;
    private final int generatorMobNumber = 10;
    private final boolean generatorWallSizeRandomizing = false;
    private final double wallSize = 1;
    private final double mobSize = 0.3;
    private final double mobHeight = 0.5;
    private final double gamerMovingStep = 0.2;
    private final double mouseSensitivity = 0.5;
    private final int gunSpriteHeight = 400;
    private final double gunShakeSpeed = 0.5;
    private final double gunShakeHeightPart = 5;
    private final long gunReloadingTicks = 6;
    private final long gunShootingTicks = 2;
    private final int mobKillingTicks = 15;
}
