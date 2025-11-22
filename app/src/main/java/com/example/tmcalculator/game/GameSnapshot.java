package com.example.tmcalculator.game;

import androidx.annotation.NonNull;
import androidx.room.Entity;

import java.util.ArrayList;
import java.util.List;

/**
 * A snapshot of the game, including number of different resources and building numbers.
 */
@Entity(tableName = "snapshot")
public class GameSnapshot implements Cloneable {
    public int coin;
    public int worker;
    public int priest;
    public int vp;
    public int power1;
    public int power2;
    public int power3;
    public int shipping;
    public int shovel = 1;
    public int dwelling;
    public int tradingHouse;
    public int temple;
    public int stronghold;
    public int sanctuary;
    /**
     * Tile keys
     */
    public List<String> tiles;

    public GameSnapshot() {
        tiles = new ArrayList<>();
    }

    @NonNull
    @Override
    public GameSnapshot clone() {
        try {
            GameSnapshot clone = (GameSnapshot) super.clone();
            clone.tiles = new ArrayList<>(tiles);
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
