package net.ildar.dungeon.config;

/**
 * Конфигурация игры.
 */
public interface Configuration {

    /**
     * Заголовок окна программы.
     */
    String getTitle();

    /**
     * Ширина игрового окна в пикселях.
     */
    int getWindowWidth();

    /**
     * Высота игрового окна в пикселях.
     */
    int getWindowHeight();

    /**
     * Выcота миникарты в пикселях.
     */
    int getMinimapHeight();

    /**
     * Ширина миникарты в пикселях.
     */
    int getMinimapWidth();

    /**
     * Максимальное число кадров в секунду.
     */
    int getMaxFps();

    /**
     * Ширина обзора в градусах.
     */
    int getFov();

    /**
     * Ширина полосы рендеринга в пикселях.
     */
    int getViewColumnWidth();

    /**
     * Расстояние до экрана в единицах длины карты.
     */
    int getScreenDistance();

    /**
     * Максимальная дальность взгляда в единицах длины карты.
     */
    int getViewDistance();

    /**
     * Размер игрока на миникарте в единицах длины карты.
     */
    double getMinimapGamerSize();

    /**
     * Размер существа на миникарте в единицах длины карты.
     */
    double getMinimapMobSize();

    /**
     * Длина боковых лучей обзора на миникарте в единицах длины карты.
     */
    double getMinimapViewSectorLength();

    /**
     * Размер прицела в пикселях.
     */
    double getCrosshairSize();

    /**
     * Толщина прицела.
     */
    double getCrosshairThickness();

    /**
     * Высота игрового таймера.
     */
    double getTimerHeight();

    /**
     * Ширина игрового таймера.
     */
    double getTimerWidth();

    /**
     * Размер шрифта игрового таймера.
     */
    float getTimerFontSize();

    /**
     * Отступ игрового таймера от края экрана.
     */
    int getTimerMargin();

    /**
     * Шаг трассировки при обнаружении коллизий в единицах длины карты.
     */
    double getTracingStep();

    /**
     * Ширина карты при генерации.
     */
    double getGeneratorMapWidth();

    /**
     * Высота карты при генерации.
     */
    double getGeneratorMapHeight();

    /**
     * Доля неподвижных объектов на карте при генерации.
     */
    double getGeneratorStaticMapObjectsPercentage();

    /**
     * Количество мобов на карте при генерации.d
     */
    int getGeneratorMobNumber();

    /**
     * Флаг для генерации стен случайных размеров при создании карты в генераторе.
     */
    boolean isGeneratorWallSizeRandomizing();

    /**
     * Ширина и длина стен в единицах длина карты.
     */
    double getWallSize();

    /**
     * Радиус моба в единицах длины карты.
     */
    double getMobSize();

    /**
     * Высота моба в единицах длины карты.
     */
    double getMobHeight();

    /**
     * Длина шага игрока.
     */
    double getGamerMovingStep();

    /**
     * Чувствительность в движению мыши при повороте.
     */
    double getMouseSensitivity();

    /**
     * Высота текстуры оружия в пикселях.
     */
    int getGunSpriteHeight();

    /**
     * Скорость движения оружия при "дыхании" игрока.
     */
    double getGunShakeSpeed();

    /**
     * Амплитуда движения оружия при "дыхании" игрока, выраженная в процентах от высоты текстуры.
     */
    double getGunShakeHeightPart();

    /**
     * Число игровых кадров для перезарядке оружия.
     */
    long getGunReloadingTicks();

    /**
     * Число игровых кадров для выстрела оружия.
     */
    long getGunShootingTicks();

    /**
     * Число игровых кадров в течение которых умерший моб отображается на экране.
     */
    int getMobKillingTicks();
}
