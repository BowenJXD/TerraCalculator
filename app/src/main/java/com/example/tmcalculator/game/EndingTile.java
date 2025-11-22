package com.example.tmcalculator.game;

/**
 * Give data change based on a {@link GameSnapshot}.
 * Usually reflects the tiles that gives scores at the end of round.
 */
public abstract class EndingTile {
    public abstract GameDataChange getChange(GameSnapshot ss);
}
