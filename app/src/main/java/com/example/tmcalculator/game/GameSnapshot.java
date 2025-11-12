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
    public int shovel;

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
