package com.weareadaptive.pong.server.state;

import src.main.resources.InputType;

public class Bar
{
    private float x;
    private float y;
    private int width;
    private int height;

    private final float velocity = 500.0f;
    private InputType currentInput = InputType.NULL_VAL;

    public Bar(final int x, final int y, final int width, final int height)
    {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void setInput(final InputType inputType)
    {
        this.currentInput = inputType;
    }

    public void update(final float deltaTime)
    {
        switch (currentInput)
        {
            case UP -> y -= velocity * deltaTime;
            case DOWN -> y += velocity * deltaTime;
        }
    }

    public int x()
    {
        return Math.round(x);
    }

    public int y()
    {
        return (int)y;
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
