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
        vel = 250;
    }

    public void update(final float deltaTime, final Bar player1, final Bar player2)
    {
        x += (dirX * vel) * deltaTime;
        y += (dirY * vel) * deltaTime;

        if((player1.y() <= y && y <= player1.y() + 100) && (player1.x() + 20 <= x && x <= player1.x() + 30))
        {
            lateralBounce();
        }

        if((player2.y() <= y && y <= player2.y() + 100) && (player2.x() >= x && x >= player2.x() - 10))
        {
            lateralBounce();
        }

        if(Math.abs(y - SCREEN_HEIGHT) < 40 || Math.abs(y - 0) < 20)
        {
            verticalBounce();
        }
    }

    public void reset()
    {
        vel = 250;
        x = (float) SCREEN_WIDTH / 2;
        y = (float) SCREEN_HEIGHT / 2;
        dirX = Math.random() < 0.5 ? 1f : -1f;
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
        vel = vel + 10;
        dirX = -dirX;
        convertInUnitaryDirection();
    }

    public void verticalBounce()
    {
        vel = vel + 10;
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
