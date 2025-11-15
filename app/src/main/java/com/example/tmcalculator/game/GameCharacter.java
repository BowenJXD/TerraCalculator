package com.example.tmcalculator.game;

import java.util.HashMap;

/**
 * Stores an actionChangeMap that would override the default base actions.
 * E.g. Fakir's stronghold is 4-10, so its BUILD_STRONGHOLD would have a different {@link com.example.tmcalculator.game.GameDataChange},
 * and refuses to upgrade shovel by mapping UPGRADE_SHOVEL_L2_L3 to null.
 */
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
