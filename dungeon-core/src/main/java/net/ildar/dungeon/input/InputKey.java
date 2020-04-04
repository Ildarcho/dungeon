package net.ildar.dungeon.input;

import lombok.Getter;
import lombok.NonNull;

/**
 * Игровые действия, соответствующие нажатиям клавиатуры.
 */
@Getter
public enum InputKey {
    FORWARD("WwцЦ", new int[]{38}, false),
    LEFT("AaфФ", new int[]{37}, false),
    BACKWARD("SsыЫ", new int[]{40}, false),
    RIGHT("DdвВ", new int[]{39}, false),
    SHOOT(" ", new int[]{32}, true),
    RESET("rкКR", new int[0], true),
    ;

    /**
     * Строка с символами на клавиатуре, соответствующим игровому действию.
     */
    @NonNull
    private String keyChars;

    /**
     * Массив клавиатурных кодов, соответствующих игровому действию. Коды платформо-зависимы.
     */
    @NonNull
    private int[] keyCodes;

    /**
     * Флаг одинарной обработки клавиатурного события.
     * <p>
     * При одинарной обработке статус активности игрового действия обнуляется после обработки.
     * <p>
     * Клавиша движения вперед не обладает данным свойством,
     * пока нажата эта клавиша игрок двигается вперед и не останавливается.
     */
    private boolean singleTypeMode;

    InputKey(@NonNull String keyChars, @NonNull int[] keyCodes, boolean singleTypeMode) {
        this.keyChars = keyChars;
        this.keyCodes = keyCodes;
        this.singleTypeMode = singleTypeMode;
    }
}
