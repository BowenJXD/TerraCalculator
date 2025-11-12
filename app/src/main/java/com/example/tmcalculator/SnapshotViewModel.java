package com.example.tmcalculator;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.tmcalculator.game.GameSnapshot;

import java.util.ArrayList;
import java.util.List;

public class SnapshotViewModel extends AndroidViewModel {
    private final MutableLiveData<List<GameSnapshot>> snapshots = new MutableLiveData<>();

    public SnapshotViewModel(@NonNull Application application) {
        super(application);
        snapshots.setValue(new ArrayList<>());
    }

    public LiveData<List<GameSnapshot>> getSnapshots() {
        return snapshots;
    }

    public void insert(GameSnapshot snapshot) {
        List<GameSnapshot> currentList = snapshots.getValue();
        if (currentList != null) {
            ArrayList<GameSnapshot> updatedList = new ArrayList<>(currentList);
            updatedList.add(snapshot);
            snapshots.setValue(updatedList);
        }
    }

    public void delete(GameSnapshot snapshot) {
        List<GameSnapshot> currentList = snapshots.getValue();
        if (currentList != null) {
            ArrayList<GameSnapshot> updatedList = new ArrayList<>(currentList);
            updatedList.remove(snapshot);
            snapshots.setValue(updatedList);
        }
    }

    public void update(GameSnapshot snapshot) {
        List<GameSnapshot> currentList = snapshots.getValue();
        if (currentList != null) {
            ArrayList<GameSnapshot> updatedList = new ArrayList<>(currentList);
            int index = updatedList.indexOf(snapshot);
            if (index != -1) {
                updatedList.set(index, snapshot);
                snapshots.setValue(updatedList);
            }
        }
    }

}
