package com.example.tmcalculator.game;

import android.content.Context;

import com.example.tmcalculator.game.characters.GameCharacter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainGame {
    public HashMap<String, GameDataChange> actionChangeMap;
    /**
     * Game setting, additional changes, assume always be valid after change
     */
    public GameSetting setting;
    /**
     * Game character changes, overrides actionChangeMap
     */
    public GameCharacter character;
    public HashMap<String, GameCharacter> allCharacters;

    private final Context context;

    public static final String BASE_CHANGE_MAP_PATH = "json/characters/base.json";

    public MainGame(Context context) {
        this.context = context;
        setting = new GameSetting();
        loadBaseChangeMap();
        loadAllCharacters();
    }

    public void loadBaseChangeMap() {
        Gson gson = new Gson();
        Type type = new TypeToken<HashMap<String, GameDataChange>>() {
        }.getType();
        try {
            InputStream inputStream = context.getAssets().open(BASE_CHANGE_MAP_PATH);
            InputStreamReader reader = new InputStreamReader(inputStream);
            actionChangeMap = gson.fromJson(reader, type);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadAllCharacters() {
        // TODO: load all characters from json files
    }

    public void applyCharacterChanges(String name) {
        character = allCharacters.get(name);
        if (character == null) {
            return;
        }
        actionChangeMap.putAll(character.actionChangeMap);
        actionChangeMap.values().removeIf(java.util.Objects::isNull);
    }

    public GameSnapshot calculate(String action, GameSnapshot ss) {
        GameSnapshot result = ss.clone();
        GameDataChange change = actionChangeMap.get(action);
        if (change != null) {
            applyChange(result, change);
            convert(ss);
        }
        if (!checkValid(result)) {
            return null;
        }
        GameDataChange settingChange = setting.getChange(action);
        if (settingChange != null) {
            applyChange(result, settingChange);
        }
        return result;
    }

    public void applyChange(GameSnapshot ss, GameDataChange change) {
        ss.coin += change.coin;
        ss.worker += change.worker;
        ss.priest += change.priest;
        ss.vp += change.vp;
        ss.shipping += change.shipping;
        ss.shovel += change.shovel;

        // conversion logic
        int overflow;
        if (change.power > 0) {
            overflow = gainPower(ss, change.power);
            ss.coin += overflow / 2;
            overflow = 0;
        } else {
            overflow = losePower(ss, -change.power);
        }
    }

    public int gainPower(GameSnapshot ss, int amount) {
        if (ss.power1 > 0) {
            if (ss.power1 >= amount) {
                ss.power2 += amount;
                ss.power1 -= amount;
                return 0;
            } else {
                ss.power2 += ss.power1;
                amount -= ss.power1;
                ss.power1 = 0;
            }
        }
        if (ss.power2 > 0) {
            if (ss.power2 >= amount) {
                ss.power3 += amount;
                ss.power2 -= amount;
                return 0;
            } else {
                ss.power3 += ss.power2;
                amount -= ss.power2;
                ss.power2 = 0;
            }
        }
        return amount;
    }

    public int losePower(GameSnapshot ss, int amount) {
        if (ss.power3 < amount) {
            removePower2ForPower3(ss, amount - ss.power3);
        }
        int powerToLose = Math.min(ss.power3, amount);
        ss.power1 += powerToLose;
        ss.power3 -= powerToLose;
        amount -= powerToLose;
        return amount;
    }

    public int removePower2ForPower3(GameSnapshot ss, int amount) {
        int powerToRemove = Math.min(ss.power2 / 2, amount);
        ss.power2 -= powerToRemove * 2;
        ss.power3 += powerToRemove;
        amount -= powerToRemove;
        return amount;
    }

    public void convert(GameSnapshot ss) {
        for (GameAction action : setting.conversionPriority) {
            switch (action) {
                case CONVERT_POWER_TO_COIN:
                    if (ss.coin < 0) {
                        convertPowerToCoin(ss, -ss.coin);
                    }
                    break;
                case CONVERT_POWER_TO_WORKER:
                    if (ss.worker < 0) {
                        convertPowerToWorker(ss, -ss.worker);
                    }
                    break;
                case CONVERT_POWER_TO_PRIEST:
                    if (ss.priest < 0) {
                        convertPowerToPriest(ss, -ss.priest);
                    }
                    break;
                case CONVERT_PRIEST_TO_WORKER:
                    if (ss.worker < 0) {
                        convertPriestToWorker(ss, -ss.worker);
                    }
                    break;
                case CONVERT_WORKER_TO_COIN:
                    if (ss.coin < 0) {
                        convertWorkerToCoin(ss, -ss.coin);
                    }
                    break;
                case CONVERT_PRIEST_TO_COIN:
                    if (ss.coin < 0) {
                        convertPriestToCoin(ss, -ss.coin);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    /**
     *
     * @param ss
     * @param amount the amount needed
     */
    public void convertPowerToPriest(GameSnapshot ss, int amount) {
        if (ss.priest >= 0) return;
        GameDataChange change = actionChangeMap.get(GameAction.CONVERT_POWER_TO_PRIEST.toString());
        if (change != null) {
            while (ss.power3 > -change.power || amount > 0) {
                applyChange(ss, change);
                amount -= change.priest;
            }
        }
    }

    public void convertPowerToWorker(GameSnapshot ss, int amount) {
        if (ss.worker >= 0) return;
        GameDataChange change = actionChangeMap.get(GameAction.CONVERT_POWER_TO_WORKER.toString());
        if (change != null) {
            while (ss.power3 > -change.power || amount > 0) {
                applyChange(ss, change);
                amount -= change.worker;
            }
        }
    }

    public void convertPowerToCoin(GameSnapshot ss, int amount) {
        if (ss.coin >= 0) return;
        GameDataChange change = actionChangeMap.get(GameAction.CONVERT_POWER_TO_COIN.toString());
        if (change != null) {
            while (ss.power3 > -change.power || amount > 0) {
                applyChange(ss, change);
                amount -= change.coin;
            }
        }
    }

    public void convertPriestToWorker(GameSnapshot ss, int amount) {
        if (ss.worker >= 0) return;
        GameDataChange change = actionChangeMap.get(GameAction.CONVERT_PRIEST_TO_WORKER.toString());
        if (change != null) {
            while (ss.priest > -change.priest || amount > 0) {
                applyChange(ss, change);
                amount -= change.worker;
            }
        }
    }

    public void convertWorkerToCoin(GameSnapshot ss, int amount) {
        if (ss.coin >= 0) return;
        GameDataChange change = actionChangeMap.get(GameAction.CONVERT_WORKER_TO_COIN.toString());
        if (change != null) {
            while (ss.worker > -change.worker || amount > 0) {
                applyChange(ss, change);
                amount -= change.coin;
            }
        }
    }

    public void convertPriestToCoin(GameSnapshot ss, int amount) {
        if (ss.coin >= 0) return;
        GameDataChange change = actionChangeMap.get(GameAction.CONVERT_PRIEST_TO_COIN.toString());
        if (change != null) {
            while (ss.priest > -change.priest || amount > 0) {
                applyChange(ss, change);
                amount -= change.coin;
            }
        }
    }

    public boolean checkValid(GameSnapshot ss) {
        boolean result = true;
        if (ss.coin < 0 || ss.worker < 0 || ss.priest < 0 || ss.vp < 0 || ss.power3 < 0 || ss.power2 < 0 || ss.power1 < 0) {
            result = false;
        }
        return result;
    }
}
