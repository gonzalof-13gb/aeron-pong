package com.weareadaptive.pong.client;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

public class Keyboard {

    private static final Map<Integer, Boolean> pressedKeys = new HashMap<>();

    static {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(event -> {
            synchronized (Keyboard.class) {
                System.out.println(event.getKeyCode());
                if (event.getID() == KeyEvent.KEY_PRESSED) pressedKeys.put(event.getKeyCode(), true);
                else if (event.getID() == KeyEvent.KEY_RELEASED) pressedKeys.put(event.getKeyCode(), false);
                return false;
            }
        });
    }

    public boolean isKeyPressed(int keyCode) { // Any key code from the KeyEvent class
        return pressedKeys.getOrDefault(keyCode, false);
    }
}