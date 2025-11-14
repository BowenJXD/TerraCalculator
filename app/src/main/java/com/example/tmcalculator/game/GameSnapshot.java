package com.example.tmcalculator.game;

import androidx.annotation.NonNull;
import androidx.room.Entity;

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

    @NonNull
    @Override
    public GameSnapshot clone() {
        try {
            return (GameSnapshot) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
