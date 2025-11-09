package com.example.tmcalculator.game.vpmods;

import com.example.tmcalculator.game.GameSnapshot;
import com.example.tmcalculator.game.GameAction;

/**
 * Modifies
 */
public abstract class GameVPMod {
    public abstract int modVP(GameAction action, GameSnapshot ss);
}
