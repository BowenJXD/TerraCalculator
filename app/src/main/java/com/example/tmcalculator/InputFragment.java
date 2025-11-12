package com.example.tmcalculator;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.tmcalculator.databinding.FragmentInputBinding;
import com.example.tmcalculator.game.GameSnapshot;

public class InputFragment extends Fragment {

    private FragmentInputBinding binding;
    private SnapshotViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentInputBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(SnapshotViewModel.class);

        binding.btnApply.setOnClickListener(v -> {
            GameSnapshot snapshot = new GameSnapshot();
            snapshot.vp = tryParseInt(binding.inputVp.getText().toString());
            snapshot.coin = tryParseInt(binding.inputCoin.getText().toString());
            snapshot.worker = tryParseInt(binding.inputWorker.getText().toString());
            snapshot.priest = tryParseInt(binding.inputPriest.getText().toString());
            snapshot.power1 = tryParseInt(binding.inputPower1.getText().toString());
            snapshot.power2 = tryParseInt(binding.inputPower2.getText().toString());
            snapshot.power3 = tryParseInt(binding.inputPower3.getText().toString());
            viewModel.insert(snapshot);
        });
    }

    public int tryParseInt(String text) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}