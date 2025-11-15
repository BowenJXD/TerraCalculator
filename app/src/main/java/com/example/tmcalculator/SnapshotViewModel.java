package com.example.tmcalculator;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.tmcalculator.game.GameSnapshot;
import com.example.tmcalculator.game.MainGame;
import com.example.tmcalculator.game.SimResult;
import com.example.tmcalculator.game.Simulation;

import java.util.List;

/**
 * Stores a simulation that could be listened to.
 * Recalculates the snapshots in the simulation by calling {@link MainGame#simulateAll(Simulation, int)}}
 */
public class SnapshotViewModel extends AndroidViewModel {
    private final MutableLiveData<Simulation> simulation = new MutableLiveData<>();
    private MainGame mainGame;

    public SnapshotViewModel(@NonNull Application application) {
        super(application);
        simulation.setValue(new Simulation());
        mainGame = MainGame.getInstance(application.getBaseContext());
    }

    public LiveData<Simulation> getSimulation() {
        return simulation;
    }

    /**
     * Sets the snapshot at the given index, and set the action[index-1] to be CUSTOM if index is not 0.
     * Recalculate the snapshots after
     *
     * @param snapshot
     * @param index
     * @return
     */
    public boolean setSnapshot(GameSnapshot snapshot, int index) {
        Simulation sim = this.simulation.getValue();
        if (sim == null) return false;
        List<GameSnapshot> currentList = sim.getSnapshots();
        if (currentList == null || currentList.size() < index) return false;
        if (index == currentList.size()) {
            currentList.add(snapshot);
        } else {
            currentList.set(index, snapshot);
        }
        sim.setSnapshots(currentList);
        Simulation result = mainGame.simulateAll(sim, index);
        if (result == null) return false;
        this.simulation.setValue(result);
        return true;
    }

    public SimResult setAction(String action, int index) {
        Simulation sim = this.simulation.getValue();
        if (sim == null) return SimResult.UNKNOWN_ERROR;
        List<String> currentList = sim.getActions();
        if (currentList == null || currentList.size() < index) return SimResult.UNKNOWN_ERROR;
        if (index == currentList.size()) {
            currentList.add(action);
        } else {
            currentList.set(index, action);
        }
        sim.setActions(currentList);
        Simulation result = mainGame.simulateAll(sim, index);
        if (result == null) return SimResult.UNKNOWN_ERROR;
        this.simulation.setValue(result);
        if (result.getLength() < sim.getLength()) return SimResult.UNKNOWN_ERROR;
        return result.getSimResult();
    }
}
