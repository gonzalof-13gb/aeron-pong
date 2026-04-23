package com.weareadaptive.pong.server.state;

import src.main.resources.InputType;

public class Bar
{
    private int x;
    private int y;
    private int width;
    private int height;

    private final int velocity = 1;

    public Bar(final int x, final int y, final int width, final int height)
    {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void move(InputType inputType)
    {
        switch (inputType)
        {
            case UP -> y += velocity;
            case DOWN -> y -= velocity;
        }
    }

    public int x()
    {
        return x;
    }

    public int y()
    {
        return y;
    }

    public int width()
    {
        return width;
    }

    public int height()
    {
        return height;
    }
}
