package com.weareadaptive.pong.server.state;

public class Ball
{
    private float x;
    private float y;

    private int radius;

    private float dirX;
    private float dirY;

    public void update(final float deltaTime, final Bar player1, final Bar player2)
    {
        x += dirX * deltaTime;
        y += dirY * deltaTime;
        //TODO: bounces
    }

    private void convertInUnitaryDirection()
    {
        float magnitude = (float) Math.sqrt(dirX *  dirX + dirY * dirY);
        dirX = dirX / magnitude;
        dirY = dirY / magnitude;
    }

    private void lateralBounce()
    {
        //TODO: change direction regards to the distance of the center of the bar
        dirX = -dirX;
        convertInUnitaryDirection();
    }

    public void verticalBounce()
    {
        dirY = -dirY;
        convertInUnitaryDirection();
    }

    public int getX() {
        return  Math.round(x);
    }

    public int getY() {
        return Math.round(y);
    }

    public int getRadius() {
        return radius;
    }
}
