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

import com.example.tmcalculator.game.GameSnapshot;
import com.example.tmcalculator.game.MainGame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A fragment representing a list of Items.
 */
public class SnapshotFragment extends Fragment implements SnapshotRecyclerViewAdapter.OnSnapshotActionListener {
    private SnapshotViewModel viewModel;
    private SnapshotRecyclerViewAdapter adapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private MainGame mainGame;
    private ActionManager actionManager;

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
        recyclerView = rootView.findViewById(R.id.list);
        adapter = new SnapshotRecyclerViewAdapter(this);
        adapter.setSnapshots(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(requireActivity()).get(SnapshotViewModel.class);
        viewModel.getSimulation().observe(getViewLifecycleOwner(), simulation -> {
            adapter.setSnapshots(simulation.getSnapshots());
            adapter.notifyDataSetChanged();
        });

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        mainGame = MainGame.getInstance(getContext());
        actionManager = ActionManager.getInstance(getContext());
        return rootView;
    }

    @Override
    public void onAction(GameSnapshot ss, View anchor, Button btnAction, int position) {
        if (actionManager == null) {
            Toast.makeText(getContext(), "Actions not loaded", Toast.LENGTH_SHORT).show();
            return;
        }

        PopupMenu popupMenu = new PopupMenu(getContext(), anchor);
        Menu menu = popupMenu.getMenu();
        Map<Integer, String> idToKeyMap = new HashMap<>();
        int idCounter = 1;

        for (Map.Entry<String, Map<String, String>> category : actionManager.getActionTree().entrySet()) {
            SubMenu subMenu = menu.addSubMenu(category.getKey());
            for (Map.Entry<String, String> action : category.getValue().entrySet()) {
                idToKeyMap.put(idCounter, action.getKey());
                subMenu.add(Menu.NONE, idCounter, Menu.NONE, action.getValue());
                idCounter++;
            }
        }

        popupMenu.setOnMenuItemClickListener(item -> {
            String selectedKey = idToKeyMap.get(item.getItemId());
            if (selectedKey == null) return true;
            String selectedAction = actionManager.getActionName(selectedKey);
            if (selectedAction == null) return true;
            boolean succeeded = viewModel.setAction(selectedKey, position);
            if (succeeded) {
                btnAction.setText(selectedAction);
            } else {
                Toast.makeText(getContext(), "Calculation Failed", Toast.LENGTH_SHORT).show();
            }
            return true;
        });

        popupMenu.show();
    }
}
