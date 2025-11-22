package com.example.tmcalculator.game;


import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Includes an additional actionChangeMap to reflect favor tile, scoring tile and possibly bonus tile.
 */
public class GameSetting {
    /**
     * May allow user to change conversion priority in the future.
     */
    public List<GameAction> conversionPriority = Arrays.asList(
            GameAction.CONVERT_POWER_TO_PRIEST,
            GameAction.CONVERT_POWER_TO_WORKER,
            GameAction.CONVERT_POWER_TO_COIN,
            GameAction.CONVERT_PRIEST_TO_WORKER,
            GameAction.CONVERT_WORKER_TO_COIN,
            // GameAction.CONVERT_POWER2_TO_POWER3,
            // GameAction.CONVERT_POWER_TO_COIN,
            GameAction.CONVERT_PRIEST_TO_COIN
            );
}
