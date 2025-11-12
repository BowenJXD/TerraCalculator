package com.example.tmcalculator;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.tmcalculator.game.GameSnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 */
public class SnapshotFragment extends Fragment implements SnapshotRecyclerViewAdapter.OnSnapshotActionListener {
    private SnapshotViewModel viewModel;
    private SnapshotRecyclerViewAdapter adapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;

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
        return rootView;
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
}
