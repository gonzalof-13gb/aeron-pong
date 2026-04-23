package com.weareadaptive.pong.server.state;

import com.weareadaptive.pong.utils.Vector2;
import src.main.resources.InputType;

public class Bar
{
    private Vector2 position;
    private Vector2 size;

    private final int velocity = 1;

    public void move(InputType inputType)
    {
        switch (inputType)
        {
            case UP -> position.add(0, velocity);
            case DOWN -> position.add(0, -velocity);
        }
    }
}
