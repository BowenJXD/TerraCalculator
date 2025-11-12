package com.example.tmcalculator.game;

import com.example.tmcalculator.game.characters.GameCharacter;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;

public class GameSetting {
    public HashMap<String, GameDataChange> actionChangeMap;

    public List<GameAction> conversionPriority = Arrays.asList(
            GameAction.CONVERT_POWER_TO_COIN,
            GameAction.CONVERT_WORKER_TO_COIN,
            // GameAction.CONVERT_POWER2_TO_POWER3,
            // GameAction.CONVERT_POWER_TO_COIN,
            GameAction.CONVERT_POWER_TO_WORKER,
            GameAction.CONVERT_POWER_TO_PRIEST,
            GameAction.CONVERT_PRIEST_TO_WORKER,
            GameAction.CONVERT_PRIEST_TO_COIN
            );


    public GameDataChange getChange(String action) {
        return actionChangeMap.get(action);
    }

    public void setChange(String action, GameDataChange change) {
        actionChangeMap.put(action, change);
    }
}
