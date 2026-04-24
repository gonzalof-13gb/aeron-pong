package com.weareadaptive.pong.server.state;

import org.agrona.collections.IntArrayList;

public record GameState(Bar player1,
                        Bar player2,
                        Ball ball,
                        IntArrayList scores)
{
    public Bar getPlayerById(final short playerId)
    {
        return switch (playerId) {
            case 1 -> player1;
            case 2 -> player2;
            default -> null;
        };
    }
}
