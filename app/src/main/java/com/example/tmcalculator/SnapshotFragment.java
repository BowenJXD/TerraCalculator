package com.example.tmcalculator;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tmcalculator.game.ActionManager;
import com.example.tmcalculator.game.GameSnapshot;
import com.example.tmcalculator.game.SimResult;
import com.example.tmcalculator.util.LocalisationManager;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A fragment showing a {@link SnapshotRecyclerViewAdapter} showing a list of snapshots and actions
 * (a {@link com.example.tmcalculator.game.Simulation}).
 * Listens to the change in the {@link #viewModel}, and pass the change to the {@link #adapter}.
 */
public class SnapshotFragment extends Fragment implements SnapshotRecyclerViewAdapter.OnSnapshotActionListener {
    private SnapshotViewModel viewModel;
    private SnapshotRecyclerViewAdapter adapter;
    private ActionManager actionManager;
    private LocalisationManager localisationManager;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SnapshotFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_snapshot_list, container, false);
        RecyclerView recyclerView = rootView.findViewById(R.id.list);
        adapter = new SnapshotRecyclerViewAdapter(this);
        adapter.setSnapshots(new ArrayList<>());
        adapter.setActions(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(requireActivity()).get(SnapshotViewModel.class);
        viewModel.getSimulation().observe(getViewLifecycleOwner(), simulation -> {
            adapter.setSnapshots(simulation.getSnapshots());
            adapter.setActions(simulation.getActions());
            adapter.notifyDataSetChanged();
            SimResult simResult = simulation.getSimResult();
            if (simResult != null && simResult != SimResult.SUCCESS) {
                String resultText = localisationManager.getWarningLocalisation(simResult.toString());
                Snackbar.make(rootView, resultText, Snackbar.LENGTH_LONG).show();
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        actionManager = ActionManager.getInstance();
        localisationManager = LocalisationManager.getInstance();
        return rootView;
    }

    @Override
    public void onAction(GameSnapshot ss, View anchor, Button btnAction, int position) {
        if (actionManager.getActionTree() == null || localisationManager == null) {
            Toast.makeText(getContext(), "Data not loaded", Toast.LENGTH_SHORT).show();
            return;
        }

        PopupMenu popupMenu = new PopupMenu(getContext(), anchor);
        Menu menu = popupMenu.getMenu();
        Map<Integer, String> idToKeyMap = new HashMap<>();
        int idCounter = 1;

        for (Map.Entry<String, List<String>> category : actionManager.getActionTree().entrySet()) {
            String categoryName = localisationManager.getActionLocalisation(category.getKey());
            SubMenu subMenu = menu.addSubMenu(categoryName);
            for (String actionKey : category.getValue()) {
                idToKeyMap.put(idCounter, actionKey);
                String actionName = localisationManager.getActionLocalisation(actionKey);
                subMenu.add(Menu.NONE, idCounter, Menu.NONE, actionName);
                idCounter++;
            }
        }

        popupMenu.setOnMenuItemClickListener(item -> {
            String actionKey = idToKeyMap.get(item.getItemId());
            if (actionKey == null) return true;
            viewModel.setAction(actionKey, position);
            return true;
        });

        popupMenu.show();
    }
}
