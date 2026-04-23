package com.weareadaptive.pong.client;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Keyboard extends KeyAdapter
{
    private final Set<Integer> pressedKeys = ConcurrentHashMap.newKeySet();

    @Override
    public void keyPressed(final KeyEvent e)
    {
        pressedKeys.add(e.getKeyCode());
    }

    @Override
    public void keyReleased(final KeyEvent e)
    {
        pressedKeys.remove(e.getKeyCode());
    }

    public boolean isKeyPressed(final int keyCode)
    {
        return pressedKeys.contains(keyCode);
    }
}
