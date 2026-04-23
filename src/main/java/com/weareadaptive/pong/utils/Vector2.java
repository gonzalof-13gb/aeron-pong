package com.weareadaptive.pong.utils;

public class Vector2
{
    private int x;
    private int y;

    public Vector2(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    public void add(final int x, final int y)
    {
        this.x += x;
        this.y += y;
    }

    public int x()
    {
        return x;
    }

    public int y()
    {
        return y;
    }

    public Vector2 x(final int x)
    {
        this.x = x;
        return this;
    }

    public Vector2 y(final int y)
    {
        this.y = y;
        return this;
    }
}
