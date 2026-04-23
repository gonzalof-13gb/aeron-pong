package com.weareadaptive.pong.server.state;

import com.weareadaptive.pong.utils.Vector2;
import src.main.resources.InputType;

public class Bar
{
    private final Vector2 position = new Vector2(100, 400);
    private final Vector2 size = new Vector2(700, 400);

    private final int velocity = 1;

    public void move(InputType inputType)
    {
        switch (inputType)
        {
            case UP -> position.add(0, velocity);
            case DOWN -> position.add(0, -velocity);
        }
    }

    public Vector2 position()
    {
        return position;
    }

    public Vector2 size()
    {
        return size;
    }
}
