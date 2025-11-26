package com.example.tmcalculator.util;

import android.content.Context;

import com.example.tmcalculator.game.GameCharacter;
import com.example.tmcalculator.game.GameDataChange;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CharacterManager {
    private static CharacterManager instance;
    private Context context;
    private Map<String, GameCharacter> characterMap;
    private static final String CHARACTER_FILE_PATH = "json/characters";
    private CharacterManager() {
        this.context = ContextManager.getContext();
        initialiseCharacters();
    }

    public static synchronized CharacterManager getInstance() {
        if (instance == null) {
            instance = new CharacterManager();
        }
        return instance;
    }

    private void initialiseCharacters() {
        characterMap = new HashMap<>();
        Gson gson = new Gson();
        Type type = new TypeToken<HashMap<String, GameDataChange>>() {}.getType();

        try {
            String[] characterFiles = context.getAssets().list(CHARACTER_FILE_PATH);
            if (characterFiles != null) {
                for (String fileName : characterFiles) {
                    if (!fileName.endsWith(".json")) continue;

                    InputStream inputStream = context.getAssets().open(CHARACTER_FILE_PATH + "/" + fileName);
                    InputStreamReader reader = new InputStreamReader(inputStream);
                    HashMap<String, GameDataChange> actionChangeMap = gson.fromJson(reader, type);

                    GameCharacter character = new GameCharacter(actionChangeMap);
                    String characterName = fileName.substring(0, fileName.lastIndexOf('.'));
                    characterMap.put(characterName, character);

                    reader.close();
                    inputStream.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Map<String, GameCharacter> getCharacterMap() {
        return characterMap;
    }

    public List<String> getCharacterNames() {
        return new ArrayList<>(characterMap.keySet());
    }


    public GameCharacter getCharacter(String name) {
        return characterMap.get(name);
    }
}
