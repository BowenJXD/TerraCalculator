package com.example.tmcalculator.game;

import com.example.tmcalculator.game.characters.GameCharacter;
import com.example.tmcalculator.game.vpmods.GameVPMod;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;

public class GameSetting {
    public HashMap<String, GameDataChange> actionChangeMap;

    public GameDataChange getChange(String action) {
        return actionChangeMap.get(action);
    }

    public void setChange(String action, GameDataChange change) {
        actionChangeMap.put(action, change);
    }
}
