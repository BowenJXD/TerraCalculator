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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A fragment representing a list of Items.
 */
public class SnapshotFragment extends Fragment implements SnapshotRecyclerViewAdapter.OnSnapshotActionListener {
    private SnapshotViewModel viewModel;
    private SnapshotRecyclerViewAdapter adapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private MainGame mainGame;
    private Map<String, Map<String, String>> actionMap;

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
        viewModel.getSnapshots().observe(getViewLifecycleOwner(), snapshots -> {
            adapter.setSnapshots(snapshots);
            adapter.notifyDataSetChanged();
        });

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        mainGame = new MainGame(getContext());
        loadActionMap();
        return rootView;
    }

    private void loadActionMap() {
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, Map<String, String>>>() {
        }.getType();
        try {
            InputStream inputStream = getContext().getAssets().open("json/localization/CHS/action.json");
            InputStreamReader reader = new InputStreamReader(inputStream);
            actionMap = gson.fromJson(reader, type);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error loading actions", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onEditSnapshot(GameSnapshot ss) {
        Toast.makeText(getContext(), "Edit: " + ss.vp, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDeleteSnapshot(GameSnapshot ss) {
        Toast.makeText(getContext(), "Delete: " + ss.vp, Toast.LENGTH_SHORT).show();
        viewModel.delete(ss);
    }

    @Override
    public void onAction(GameSnapshot ss, View anchor, Button btnAction) {
        if (actionMap == null) {
            Toast.makeText(getContext(), "Actions not loaded", Toast.LENGTH_SHORT).show();
            return;
        }

        PopupMenu popupMenu = new PopupMenu(getContext(), anchor);
        Menu menu = popupMenu.getMenu();
        Map<Integer, String> idToActionMap = new HashMap<>();
        Map<Integer, String> idToKeyMap = new HashMap<>();
        int idCounter = 1;

        for (Map.Entry<String, Map<String, String>> category : actionMap.entrySet()) {
            SubMenu subMenu = menu.addSubMenu(category.getKey());
            for (Map.Entry<String, String> action : category.getValue().entrySet()) {
                idToActionMap.put(idCounter, action.getValue());
                idToKeyMap.put(idCounter, action.getKey());
                subMenu.add(Menu.NONE, idCounter, Menu.NONE, action.getKey());
                idCounter++;
            }
        }

        popupMenu.setOnMenuItemClickListener(item -> {
            String selectedAction = idToActionMap.get(item.getItemId());
            String selectedKey = idToKeyMap.get(item.getItemId());
            if (selectedAction != null) {
                GameSnapshot newSnapshot = mainGame.calculate(selectedAction, ss);
                if (newSnapshot != null) {
                    viewModel.insert(newSnapshot);
                    btnAction.setText(selectedKey);
                } else {
                    Toast.makeText(getContext(), "Calculation Failed", Toast.LENGTH_SHORT).show();
                }
            }
            return true;
        });

        popupMenu.show();
    }
}
