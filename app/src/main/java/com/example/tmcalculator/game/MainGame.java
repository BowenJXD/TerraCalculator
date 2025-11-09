package com.example.tmcalculator.game;

import com.example.tmcalculator.game.characters.GameCharacter;

import java.util.Dictionary;
import java.util.Iterator;
import java.util.HashMap;
import java.util.List;

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

    public static final String BASE_CHANGE_MAP_PATH = "base_change_map.json";

    public MainGame() {
        loadBaseChangeMap();
        loadAllCharacters();
    }

    public void loadBaseChangeMap() {
        // TODO: load base change map from a json file
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
        result = applyChange(result, change);
        if (!checkValid(result)) {
            return null;
        }
        GameDataChange settingChange = setting.getChange(action);
        result = applyChange(result, settingChange);
        return result;
    }

    public GameSnapshot applyChange(GameSnapshot ss, GameDataChange change){
        GameSnapshot result = ss.clone();
        // TODO: conversion logic, character specific conversion logic
        return result;
    }

    public boolean checkValid(GameSnapshot ss) {
        // TODO: check if snapshot can exist
        return true;
    }
}
