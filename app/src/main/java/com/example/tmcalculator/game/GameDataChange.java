package com.example.tmcalculator.game;

import androidx.annotation.NonNull;

/**
 * Reflects a change of game data, according to {@link com.example.tmcalculator.game.GameSnapshot}
 */
public class GameDataChange implements Cloneable {
    public int coin;
    public int worker;
    public int priest;
    public int vp;
    public int power;
    public int shipping;
    public int shovel;
    public int dwelling;
    public int tradingHouse;
    public int temple;
    public int stronghold;
    public int sanctuary;

    public GameDataChange(){
        coin = 0;
        worker = 0;
        priest = 0;
        vp = 0;
        power = 0;
        shipping = 0;
        shovel = 0;
        dwelling = 0;
        tradingHouse = 0;
        temple = 0;
        stronghold = 0;
        sanctuary = 0;
    }

    public void addChange(GameDataChange change) {
        coin += change.coin;
        worker += change.worker;
        priest += change.priest;
        vp += change.vp;
        power += change.power;
        shipping += change.shipping;
        shovel += change.shovel;
        dwelling += change.dwelling;
        tradingHouse += change.tradingHouse;
        temple += change.temple;
        stronghold += change.stronghold;
        sanctuary += change.sanctuary;
    }

    @NonNull
    @Override
    public GameDataChange clone() {
        try {
            GameDataChange clone = (GameDataChange) super.clone();
            // TODO: copy mutable state here, so the clone can't change the internals of the original
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    public static class Builder {
        private GameDataChange change = new GameDataChange();

        public Builder coin(int coin) {
            change.coin = coin;
            return this;
        }

        public Builder worker(int worker) {
            change.worker = worker;
            return this;
        }

        public Builder priest(int priest) {
            change.priest = priest;
            return this;
        }

        public Builder vp(int vp) {
            change.vp = vp;
            return this;
        }

        public Builder power(int power) {
            change.power = power;
            return this;
        }

        public Builder shipping(int shipping) {
            change.shipping = shipping;
            return this;
        }

        public Builder shovel(int shovel) {
            change.shovel = shovel;
            return this;
        }

        public Builder dwelling(int dwelling) {
            change.dwelling = dwelling;
            return this;
        }

        public Builder tradingHouse(int tradingHouse) {
            change.tradingHouse = tradingHouse;
            return this;
        }

        public Builder temple(int temple) {
            change.temple = temple;
            return this;
        }

        public Builder stronghold(int stronghold) {
            change.stronghold = stronghold;
            return this;
        }

        public Builder sanctuary(int sanctuary) {
            change.sanctuary = sanctuary;
            return this;
        }

        public GameDataChange build() {
            return change;
        }
    }
}
