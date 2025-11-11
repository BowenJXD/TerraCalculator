package com.example.tmcalculator.game;

import com.example.tmcalculator.game.characters.GameCharacter;
import com.example.tmcalculator.game.vpmods.GameVPMod;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;

public class GameSetting {
    public HashMap<String, GameDataChange> actionChangeMap;

    public List<Conversion> conversionPriority = Arrays.asList(
            Conversion.POWER_TO_COIN,
            Conversion.WORKER_TO_COIN,
            Conversion.POWER2_TO_POWER3,
            Conversion.POWER_TO_COIN,
            Conversion.POWER_TO_WORKER,
            Conversion.POWER_TO_PRIEST,
            Conversion.PRIEST_TO_WORKER,
            Conversion.PRIEST_TO_COIN
            );


    public GameDataChange getChange(String action) {
        return actionChangeMap.get(action);
    }

    public void setChange(String action, GameDataChange change) {
        actionChangeMap.put(action, change);
    }
}
