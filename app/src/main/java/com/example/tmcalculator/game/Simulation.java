package com.example.tmcalculator.game;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class Simulation implements Cloneable {
    private List<GameSnapshot> snapshots;
    private List<String> action;
    private List<GameDataChange> change;
    private GameSetting setting;
    private GameCharacter character;

    public Simulation() {
        snapshots = new ArrayList<>();
        action = new ArrayList<>();
        change = new ArrayList<>();
    }

    public Simulation(GameSetting setting, GameCharacter character) {
        snapshots = new ArrayList<>();
        action = new ArrayList<>();
        change = new ArrayList<>();
        this.setting = setting;
        this.character = character;
    }

    public void addSnapshot(GameSnapshot snapshot) {
        snapshots.add(snapshot);
    }

    public void addAction(String action) {
        this.action.add(action);
    }

    public void addChange(GameDataChange change) {
        this.change.add(change);
    }

    public List<GameSnapshot> getSnapshots() {
        return snapshots;
    }

    public List<String> getActions() {
        return action;
    }

    public List<GameDataChange> getChanges() {
        return change;
    }

    public GameSetting getSetting() {
        return setting;
    }

    public GameCharacter getCharacter() {
        return character;
    }

    public void setSnapshots(List<GameSnapshot> snapshots) {
        this.snapshots = snapshots;
    }

    public void setActions(List<String> action) {
        this.action = action;
    }

    public void setChanges(List<GameDataChange> change) {
        this.change = change;
    }

    public void cutTo(int index) {
        snapshots = new ArrayList<>(snapshots.subList(0, index+1));
        action = new ArrayList<>(action.subList(0, index));
        change = new ArrayList<>(change.subList(0, index));
    }

    public int getLength(){
        return snapshots.size();
    }

    @NonNull
    @Override
    public Simulation clone() {
        Simulation clone = new Simulation();
        clone.snapshots = new ArrayList<>(snapshots);
        clone.action = new ArrayList<>(action);
        clone.change = new ArrayList<>(change);
        clone.setting = setting;
        clone.character = character;
        return clone;
    }
}
