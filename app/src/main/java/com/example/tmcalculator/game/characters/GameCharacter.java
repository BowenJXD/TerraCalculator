package com.example.tmcalculator.game.characters;

import com.example.tmcalculator.game.GameDataChange;
import com.example.tmcalculator.game.GameAction;

import java.util.Dictionary;
import java.util.HashMap;

public class GameCharacter {
    public String name;
    public HashMap<String, GameDataChange> actionChangeMap;

    public GameCharacter(HashMap<String, GameDataChange> actionChangeMap) {
        this.actionChangeMap = actionChangeMap;
    }

    public GameDataChange getChange(String action) {
        return actionChangeMap.get(action);
    }

    public void setChange(String action, GameDataChange change) {
        actionChangeMap.put(action, change);
    }
}
