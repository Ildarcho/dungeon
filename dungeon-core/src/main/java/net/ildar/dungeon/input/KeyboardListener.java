package net.ildar.dungeon.input;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;

/**
 * Слушатель событий с клавиатуры.
 */
@Log4j2
@RequiredArgsConstructor(onConstructor_ = {@Inject})
@Singleton
public class KeyboardListener implements KeyListener {
    /**
     * Множество событий многоразовой обрабоки. См. {@link InputKey#isSingleTypeMode()}.
     */
    private EnumSet<InputKey> pressedKeys = EnumSet.noneOf(InputKey.class);

    /**
     * Множество событий одинарной обработки. См. {@link InputKey#isSingleTypeMode()}.
     */
    private EnumSet<InputKey> typedKeys = EnumSet.noneOf(InputKey.class);

    @Override
    public synchronized void keyTyped(KeyEvent e) {
        log.info("{} typed", e);
        findCorrespondingInputKey(e).filter(InputKey::isSingleTypeMode).ifPresent(inputKey -> typedKeys.add(inputKey));
    }

    @Override
    public synchronized void keyPressed(KeyEvent e) {
        log.info("{} pressed", e);
        findCorrespondingInputKey(e)
                .filter(inputKey -> !inputKey.isSingleTypeMode())
                .ifPresent(inputKey -> pressedKeys.add(inputKey));
    }

    @Override
    public synchronized void keyReleased(KeyEvent e) {
        log.info("{} released", e);
        findCorrespondingInputKey(e)
                .filter(inputKey -> !inputKey.isSingleTypeMode())
                .ifPresent(inputKey -> pressedKeys.remove(inputKey));
    }

    /**
     * Получить множество активных клавиш многоразовой обработки.
     */
    @NonNull
    public synchronized Set<InputKey> getPressedKeys() {
        return pressedKeys;
    }

    /**
     * Получить множество нажатых клавиш одноразовой обработки.
     * <p>
     * Статус активности клавиш сбрасывается после вызова данного метода.
     */
    @NonNull
    public synchronized EnumSet<InputKey> getTypedKeys() {
        EnumSet<InputKey> toReturn = typedKeys.clone();
        this.typedKeys.clear();
        return toReturn;
    }

    /**
     * Получить игровое действие по событию клавиатуры.
     *
     * @param e событие клавиатуры
     * @return Optional игрового события
     */
    @NonNull
    private Optional<InputKey> findCorrespondingInputKey(@NonNull KeyEvent e) {
        for (InputKey inputKey : InputKey.values()) {
            if (inputKey.getKeyChars().indexOf(e.getKeyChar()) >= 0) {
                return Optional.of(inputKey);
            }
            for (int inputKeyCode : inputKey.getKeyCodes()) {
                if (inputKeyCode == e.getKeyCode()) {
                    return Optional.of(inputKey);
                }
            }
        }
        return Optional.empty();
    }

}
