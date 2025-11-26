package com.example.tmcalculator.game;

import java.util.HashMap;
import java.util.Map;

/**
 * Stores an actionChangeMap that would override the default base actions.
 * E.g. Fakir's stronghold is 4-10, so its BUILD_STRONGHOLD would have a different {@link com.example.tmcalculator.game.GameDataChange},
 * and refuses to upgrade shovel by mapping UPGRADE_SHOVEL_L2_L3 to null.
 */
public class GameCharacter {
    private HashMap<String, GameDataChange> actionChangeMap;

    public GameCharacter(HashMap<String, GameDataChange> actionChangeMap) {
        this.actionChangeMap = actionChangeMap;
    }

    public GameDataChange addChange(String action, GameDataChange change) {
        GameDataChange result = actionChangeMap.get(action);
        if (result != null) {
            return result;
        }
        for (Map.Entry<String, GameDataChange> entry : actionChangeMap.entrySet()) {
            String k = entry.getKey();
            GameDataChange v = entry.getValue();
            if (k.endsWith("*") && action.startsWith(k.substring(0, k.length() - 1))) {
                change = change.clone();
                change.addChange(v);
            }
        }
        return change;
    }

    public void setChange(String action, GameDataChange change) {
        actionChangeMap.put(action, change);
    }
}
