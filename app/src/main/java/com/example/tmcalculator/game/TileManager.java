package com.example.tmcalculator.game;

import android.content.Context;

import com.example.tmcalculator.util.ContextManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * BONUS, FAVOR & SCORING tiles are all managed here.
 */
public class TileManager {
    private static TileManager instance;
    private Context context;

    /**
     * Store tiles that would give vp or data change when an action is played.
     * Maps action key to {@link GameDataChange} object.
     */
    private Map<String, InstantTile> instantScoringTiles;
    /**
     * Store tiles that would give vp or data change when the turn ends.
     * Maps action key to {@link EndingTile} object.
     */
    private Map<String, EndingTile> endingScoringTiles;
    private List<String> bonusTiles;
    private List<String> favorTiles;
    private List<String> scoringTiles;
    private static final String BASE_TILE_MAP_PATH = "json/settings/instantTile.json";

    private TileManager() {
        this.context = ContextManager.getContext();
        initialiseTileLists();
        initialiseInstantTiles();
        initialiseEndingTiles();
    }

    private void initialiseTileLists() {
        bonusTiles = List.of(GameTile.BON_DVP.name(), GameTile.BON_SHIPVP.name(), GameTile.BON_SVP.name(), GameTile.BON_THVP.name());
        favorTiles = List.of(GameTile.FAV_EARTH_1.name(), GameTile.FAV_WATER_1.name(), GameTile.FAV_AIR_1.name());
        scoringTiles = List.of(GameTile.SCO_DVP.name(), GameTile.SCO_SHOVP.name(), GameTile.SCO_SVP.name(), GameTile.SCO_THVP.name(), GameTile.SCO_TOWNVP.name());
    }

    private void initialiseInstantTiles() {
        instantScoringTiles = new HashMap<>();
        Gson gson = new Gson();
        Type type = new TypeToken<HashMap<String, HashMap<String, GameDataChange>>>() {
        }.getType();
        try {
            InputStream inputStream = context.getAssets().open(BASE_TILE_MAP_PATH);
            InputStreamReader reader = new InputStreamReader(inputStream);
            HashMap<String, HashMap<String, GameDataChange>> rawTiles = gson.fromJson(reader, type);

            for (Map.Entry<String, HashMap<String, GameDataChange>> entry : rawTiles.entrySet()) {
                String tileName = entry.getKey();
                HashMap<String, GameDataChange> actionMap = entry.getValue();

                instantScoringTiles.put(tileName, new InstantTile() {
                    @Override
                    public GameDataChange getChange(String actionKey) {
                        GameDataChange change = actionMap.get(actionKey);
                        if (change != null) {
                            return change;
                        }

                        for (Map.Entry<String, GameDataChange> actionEntry : actionMap.entrySet()) {
                            String key = actionEntry.getKey();
                            if (key.endsWith("*") && actionKey.startsWith(key.substring(0, key.length() - 1))) {
                                return actionEntry.getValue();
                            }
                        }

                        return null;
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initialiseEndingTiles() {
        endingScoringTiles = new HashMap<>();
        endingScoringTiles.put(GameTile.BON_DVP.name(), new EndingTile() {
            @Override
            public GameDataChange getChange(GameSnapshot ss) {
                return new GameDataChange.Builder().vp(1 * ss.dwelling).build();
            }
        });
        endingScoringTiles.put(GameTile.BON_THVP.name(), new EndingTile() {
            @Override
            public GameDataChange getChange(GameSnapshot ss) {
                return new GameDataChange.Builder().vp(2 * ss.tradingHouse).build();
            }
        });
        endingScoringTiles.put(GameTile.BON_SVP.name(), new EndingTile() {
            @Override
            public GameDataChange getChange(GameSnapshot ss) {
                int vp = 0;
                if (ss.stronghold > 0) vp += 4;
                if (ss.sanctuary > 0) vp += 4;
                return new GameDataChange.Builder().vp(vp).build();
            }
        });
        endingScoringTiles.put(GameTile.BON_SHIPVP.name(), new EndingTile() {
            @Override
            public GameDataChange getChange(GameSnapshot ss) {
                return new GameDataChange.Builder().vp(3 * ss.shipping).build();
            }
        });
        endingScoringTiles.put(GameTile.FAV_AIR_1.name(), new EndingTile() {
            @Override
            public GameDataChange getChange(GameSnapshot ss) {
                int vp = 0;
                switch (ss.tradingHouse) {
                    case 1:
                        vp = 2;
                        break;
                    case 2:
                    case 3:
                        vp = 3;
                        break;
                    case 4:
                        vp = 4;
                        break;
                    default:
                        break;
                }
                return new GameDataChange.Builder().vp(vp).build();
            }
        });
    }

    public static synchronized TileManager getInstance() {
        if (instance == null) {
            instance = new TileManager();
        }
        return instance;
    }

    public GameDataChange getInstantTileChange(String tileKey, String actionKey) {
        GameDataChange result = null;
        if (instantScoringTiles.containsKey(tileKey)) {
            InstantTile tile = instantScoringTiles.get(tileKey);
            if (tile != null) {
                result = tile.getChange(actionKey);
            }
        }
        return result;
    }

    public GameDataChange getEndingTileChange(String tileKey, GameSnapshot ss) {
        GameDataChange result = null;
        if (endingScoringTiles.containsKey(tileKey)) {
            EndingTile tile = endingScoringTiles.get(tileKey);
            if (tile != null) {
                result = tile.getChange(ss);
            }
        }
        return result;
    }

    public List<String> getBonusTiles() {
        return bonusTiles;
    }

    public List<String> getFavorTiles() {
        return favorTiles;
    }

    public List<String> getScoringTiles() {
        return scoringTiles;
    }
}
