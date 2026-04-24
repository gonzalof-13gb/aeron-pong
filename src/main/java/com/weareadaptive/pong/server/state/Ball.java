package com.weareadaptive.pong.server.state;

import static com.weareadaptive.pong.Globals.SCREEN_HEIGHT;
import static com.weareadaptive.pong.Globals.SCREEN_WIDTH;

public class Ball
{
    private float x;
    private float y;

    private int radius;

    private float dirX;
    private float dirY;

    private int vel;

    public Ball(final float x, final float y, final int radius, final float dirX, final float dirY)
    {
        this.dirX = dirX;
        this.dirY = dirY;
        this.radius = radius;
        this.y = y;
        this.x = x;
        vel = 200;
    }

    public void update(final float deltaTime, final Bar player1, final Bar player2)
    {
        x += (dirX * vel) * deltaTime;
        y += (dirY * vel) * deltaTime;

        if(Math.abs(x - player1.x()) < 25 && Math.abs(y - player1.y()) < 25)
        {
            lateralBounce();
        }

        if(Math.abs(x - player2.x()) < 25 && Math.abs(y - player2.y()) < 25)
        {
            lateralBounce();
        }

        if(Math.abs(y - SCREEN_HEIGHT) < 40 || Math.abs(y - 0) < 20)
        {
            verticalBounce();
        }
        //TODO: bounces
    }

    public void reset()
    {
        x = (float) SCREEN_WIDTH / 2;
        y = (float) SCREEN_HEIGHT / 2;
        dirX = (float) (Math.random() * 3 - 1);
        dirY = (float) (Math.random() * 2 - 1);
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
        vel = vel + 10;
        dirY = -dirY;
        convertInUnitaryDirection();
    }

    public void resetPos()
    {
        x = SCREEN_WIDTH;
        y = SCREEN_HEIGHT;
        dirX = (float) (Math.random() * 3 - 1);
        dirY = (float) (Math.random() * 2 - 1);
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
