package com.example.tmcalculator.game;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * Singleton, processes the main logic of terra mystica.
 */
public class MainGame {
    /**
     * A map showing what action should be matched to what string.
     * Would load from{@link #BASE_CHANGE_MAP_PATH}
     */
    public HashMap<String, GameDataChange> actionChangeMap;
    /**
     * Game setting, additional changes, assume snapshot would always be valid after change
     */
    public GameSetting setting;
    /**
     * Game character changes, overrides base actionChangeMap
     */
    public GameCharacter character;
    /**
     * A map storing all characters
     */
    public HashMap<String, GameCharacter> allCharacters;
    /**
     * Context of the application
     */
    private final Context context;
    /**
     * Path to the base change map
     */
    public static final String BASE_CHANGE_MAP_PATH = "json/characters/base.json";
    /**
     * Singleton instance
     */
    private static MainGame instance;

    private MainGame(Context context) {
        this.context = context.getApplicationContext();
        setting = new GameSetting();
        loadBaseChangeMap();
        loadAllCharacters();
    }

    public static synchronized MainGame getInstance(Context context) {
        if (instance == null) {
            instance = new MainGame(context);
        }
        return instance;
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

    // TODO: combine simulateChanges and simulateSnapshots together to consider shovel / shipping change.

    /**
     * Re-simulates all snapshots by the given action list and an index to start from.
     * @param simulation
     * @param startFrom
     * @return
     */
    public Simulation simulateAll(Simulation simulation, int startFrom) {
        Simulation result = simulation.clone();
        List<GameDataChange> changes = result.getChanges();
        List<String> actions = result.getActions();
        List<GameSnapshot> snapshots = result.getSnapshots();

        for (int i = startFrom; i < actions.size(); i++) {
            String action = actions.get(i);
            GameSnapshot ss = snapshots.get(i).clone();
            GameDataChange change = getChange(action, ss);
            if (change != null) {
                if (changes.size() <= i) changes.add(change);
                else changes.set(i, change);
            } else if (Objects.equals(action, GameAction.CUSTOM.name())) {
                change = changes.get(i);
            } else {
                result.setSimResult(checkReason(action));
                result.cutTo(i);
                break;
            }

            applyChange(ss, change);
            convert(ss);
            if (snapshots.size() <= i + 1) snapshots.add(ss);
            else snapshots.set(i + 1, ss);
            SimResult simResult = checkValid(ss);
            result.setSimResult(simResult);
            if (simResult != SimResult.SUCCESS) {
                result.cutTo(i);
                break;
            }
        }

        return result;
    }

    /*public Simulation simulateChanges(Simulation simulation, int startFrom) {
        Simulation result = simulation.clone();
        List<GameDataChange> changes = result.getChanges();
        List<String> actions = result.getActions();
        List<GameSnapshot> snapshots = result.getSnapshots();

        // Setup changes
        for (int i = startFrom; i < actions.size(); i++) {
            String action = actions.get(i);
            GameSnapshot ss = snapshots.get(i);
            GameDataChange change = getChange(action, ss);
            if (change != null) {
                if (changes.size() <= i) changes.add(change);
                else changes.set(i, change);
            } else if (!Objects.equals(action, GameAction.CUSTOM.name())) {
                result.cutTo(i);
                break;
            }
        }
        return result;
    }

    public Simulation simulateSnapshots(Simulation simulation, int startFrom) {
        Simulation result = simulation.clone();
        List<GameSnapshot> snapshots = result.getSnapshots();
        List<GameDataChange> changes = result.getChanges();

        // Setup snapshots
        for (int i = startFrom; i < simulation.getChanges().size(); i++) {
            GameSnapshot ss = snapshots.get(i).clone();
            GameDataChange change = changes.get(i);
            applyChange(ss, change);
            convert(ss);
            if (snapshots.size() <= i + 1) snapshots.add(ss);
            else snapshots.set(i + 1, ss);
            if (!checkValid(ss)) {
                result.cutTo(i);
                break;
            }
        }
        return result;
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
    }*/

    public GameDataChange getChange(String action, GameSnapshot ss) {
        GameDataChange change = actionChangeMap.get(action);
        if (change == null) {
            String fullName = ActionManager.getInstance(context).mapToActionBySS(action, ss);
            change = actionChangeMap.get(fullName);
        }
        if (change == null) return null;
        GameDataChange settingChange = setting.getChange(action);
        if (settingChange != null) {
            change = addChange(change, settingChange);
        }
        return change;
    }

    public void applyChange(GameSnapshot ss, GameDataChange change) {
        ss.coin += change.coin;
        ss.worker += change.worker;
        ss.priest += change.priest;
        ss.vp += change.vp;
        ss.shipping += change.shipping;
        ss.shovel += change.shovel;
        ss.dwelling += change.dwelling;
        ss.tradingHouse += change.tradingHouse;
        ss.temple += change.temple;
        ss.stronghold += change.stronghold;
        ss.sanctuary += change.sanctuary;

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

    public GameDataChange addChange(GameDataChange change1, GameDataChange change2) {
        GameDataChange result = new GameDataChange();
        result.coin = change1.coin + change2.coin;
        result.worker = change1.worker + change2.worker;
        result.priest = change1.priest + change2.priest;
        result.power = change1.power + change2.power;
        result.vp = change1.vp + change2.vp;
        result.shipping = change1.shipping + change2.shipping;
        result.shovel = change1.shovel + change2.shovel;
        result.dwelling = change1.dwelling + change2.dwelling;
        result.tradingHouse = change1.tradingHouse + change2.tradingHouse;
        result.temple = change1.temple + change2.temple;
        result.stronghold = change1.stronghold + change2.stronghold;
        result.sanctuary = change1.sanctuary + change2.sanctuary;
        return result;
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
            while (ss.power3 >= -change.power && amount > 0) {
                applyChange(ss, change);
                amount -= change.priest;
            }
        }
    }

    public void convertPowerToWorker(GameSnapshot ss, int amount) {
        if (ss.worker >= 0) return;
        GameDataChange change = actionChangeMap.get(GameAction.CONVERT_POWER_TO_WORKER.toString());
        if (change != null) {
            while (ss.power3 >= -change.power && amount > 0) {
                applyChange(ss, change);
                amount -= change.worker;
            }
        }
    }

    public void convertPowerToCoin(GameSnapshot ss, int amount) {
        if (ss.coin >= 0) return;
        GameDataChange change = actionChangeMap.get(GameAction.CONVERT_POWER_TO_COIN.toString());
        if (change != null) {
            while (ss.power3 >= -change.power && amount > 0) {
                applyChange(ss, change);
                amount -= change.coin;
            }
        }
    }

    public void convertPriestToWorker(GameSnapshot ss, int amount) {
        if (ss.worker >= 0) return;
        GameDataChange change = actionChangeMap.get(GameAction.CONVERT_PRIEST_TO_WORKER.toString());
        if (change != null) {
            while (ss.priest >= -change.priest && amount > 0) {
                applyChange(ss, change);
                amount -= change.worker;
            }
        }
    }

    public void convertWorkerToCoin(GameSnapshot ss, int amount) {
        if (ss.coin >= 0) return;
        GameDataChange change = actionChangeMap.get(GameAction.CONVERT_WORKER_TO_COIN.toString());
        if (change != null) {
            while (ss.worker >= -change.worker && amount > 0) {
                applyChange(ss, change);
                amount -= change.coin;
            }
        }
    }

    public void convertPriestToCoin(GameSnapshot ss, int amount) {
        if (ss.coin >= 0) return;
        GameDataChange change = actionChangeMap.get(GameAction.CONVERT_PRIEST_TO_COIN.toString());
        if (change != null) {
            while (ss.priest >= -change.priest && amount > 0) {
                applyChange(ss, change);
                amount -= change.coin;
            }
        }
    }

    public SimResult checkValid(GameSnapshot ss) {
        SimResult result = SimResult.SUCCESS;
        if (ss.coin < 0){
            result =  SimResult.NO_COIN;
        }
        if (ss.worker < 0){
            result =  SimResult.NO_WORKER;
        }
        if (ss.priest < 0){
            result =  SimResult.NO_PRIEST;
        }
        if (ss.vp < 0){
            result =  SimResult.NO_VP;
        }
        if (ss.power1 < 0){
            result =  SimResult.NO_POWER1;
        }
        if (ss.power2 < 0){
            result =  SimResult.NO_POWER2;
        }
        if (ss.power3 < 0){
            result =  SimResult.NO_POWER3;
        }
        if (ss.dwelling > 8){
            result =  SimResult.TOO_MANY_DWELLING;
        }
        if (ss.tradingHouse > 4){
            result =  SimResult.TOO_MANY_TRADING_HOUSE;
        }
        if (ss.temple > 3){
            result =  SimResult.TOO_MANY_TEMPLE;
        }
        if (ss.stronghold > 1){
            result =  SimResult.TOO_MANY_STRONGHOLD;
        }
        if (ss.sanctuary > 1){
            result =  SimResult.TOO_MANY_SANCTUARY;
        }

        return result;
    }

    public SimResult checkReason(String action) {
        SimResult result = SimResult.ILLEGAL_ACTION;
        if (action.startsWith("UPGRADE_SHOVEL") || action.startsWith("UPGRADE_SHIPPING")) {
            result = SimResult.MAX_LEVEL_REACHED;
        } else if (action.equals(GameAction.NONE.toString())) {
            result = SimResult.SUCCESS;
        }
        return result;
    }
}
