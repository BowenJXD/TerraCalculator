package com.example.tmcalculator;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import androidx.recyclerview.widget.ItemTouchHelper;

import com.example.tmcalculator.util.ActionManager;
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
    private ItemTouchHelper itemTouchHelper;
    private RecyclerView recyclerView;
    private int dragFrom = -1;
    private int dragTo   = -1;

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

        itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(@NonNull RecyclerView rv, @NonNull RecyclerView.ViewHolder vh) {
                int pos = vh.getBindingAdapterPosition();
                // Disable interactions on the header (pos 0) and the last placeholder row
                if (pos == 0 || pos == adapter.getItemCount() - 1) {
                    return makeMovementFlags(0, 0);
                }
                return makeMovementFlags(
                        ItemTouchHelper.UP | ItemTouchHelper.DOWN,
                        ItemTouchHelper.RIGHT);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView rv, @NonNull RecyclerView.ViewHolder dragged, @NonNull RecyclerView.ViewHolder target) {
                int from = dragged.getBindingAdapterPosition();
                int to   = target.getBindingAdapterPosition();
                // Don't allow dropping on header or last placeholder
                if (to == 0 || to == adapter.getItemCount() - 1) return false;
                if (dragFrom == -1) dragFrom = from;
                dragTo = to;
                adapter.onItemMove(from, to);
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder vh, int direction) {
                int adapterPos = vh.getBindingAdapterPosition();
                if (adapterPos == RecyclerView.NO_ID) return;
                int actionIndex = adapterPos - 1;
                // Remove from shared lists and animate gap closing
                adapter.onItemRemove(adapterPos);
                // Defer recalculation until the gap-close animation finishes so
                // notifyDataSetChanged doesn't interrupt it
                recyclerView.post(() -> {
                    RecyclerView.ItemAnimator anim = recyclerView.getItemAnimator();
                    if (anim != null && anim.isRunning()) {
                        anim.isRunning(() -> viewModel.recalculateFrom(actionIndex));
                    } else {
                        viewModel.recalculateFrom(actionIndex);
                    }
                });
            }

            @Override
            public boolean isLongPressDragEnabled() {
                return false; // drag is triggered manually from tvIndex long-press
            }

            @Override
            public void onSelectedChanged(@Nullable RecyclerView.ViewHolder vh, int actionState) {
                super.onSelectedChanged(vh, actionState);
                if (actionState == ItemTouchHelper.ACTION_STATE_DRAG && vh != null) {
                    vh.itemView.setElevation(16f);
                    vh.itemView.setAlpha(0.85f);
                }
            }

            @Override
            public void clearView(@NonNull RecyclerView rv, @NonNull RecyclerView.ViewHolder vh) {
                super.clearView(rv, vh);
                vh.itemView.setElevation(0f);
                vh.itemView.setAlpha(1f);
                if (dragFrom != -1 && dragTo != -1 && dragFrom != dragTo) {
                    int minActionIndex = Math.min(dragFrom, dragTo) - 1; // subtract header offset
                    viewModel.recalculateFrom(minActionIndex);
                }
                dragFrom = -1;
                dragTo   = -1;
            }
        });
        itemTouchHelper.attachToRecyclerView(recyclerView);

        return rootView;
    }

    @Override
    public void onDragStarted(RecyclerView.ViewHolder holder) {
        itemTouchHelper.startDrag(holder);
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
                String lookupKey = actionKey.endsWith("#") ? actionKey.substring(0, actionKey.length() - 1) : actionKey;
                String actionName = localisationManager.getActionLocalisation(lookupKey);
                if (actionName == null) actionName = lookupKey; // fallback
                subMenu.add(Menu.NONE, idCounter, Menu.NONE, actionName);
                idCounter++;
            }
        }

        popupMenu.setOnMenuItemClickListener(item -> {
            String actionKey = idToKeyMap.get(item.getItemId());
            if (actionKey == null) return true;

            if (actionKey.endsWith("#")) {
                final String cleanActionKey = actionKey.substring(0, actionKey.length() - 1);

                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setTitle("Enter Multiplier");

                final EditText input = new EditText(requireContext());
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                builder.setView(input);

                builder.setPositiveButton("OK", (dialog, which) -> {
                    int multiplier = 1;
                    try {
                        multiplier = Integer.parseInt(input.getText().toString());
                    } catch (NumberFormatException e) {
                        // ignore, use default 1
                    }
                    viewModel.setAction(cleanActionKey, position, multiplier);
                });
                builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

                builder.show();
            } else {
                viewModel.setAction(actionKey, position, 1);
            }
            return true;
        });

        popupMenu.show();
    }
}
