package com.example.tmcalculator;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tmcalculator.databinding.ActivitySimulationBinding;

public class SimulationActivity extends AppCompatActivity {
    private ActivitySimulationBinding binding;
    private InputFragment inputFragment;
    private SnapshotFragment snapshotFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySimulationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        inputFragment = new InputFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.frag_input, inputFragment).addToBackStack("frag_input").commit();
        snapshotFragment = new SnapshotFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.frag_list, snapshotFragment).addToBackStack("frag_list").commit();
    }
}