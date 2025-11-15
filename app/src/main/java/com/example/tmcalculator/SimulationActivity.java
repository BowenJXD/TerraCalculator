package com.example.tmcalculator;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

/**
 * Activity for the simulation.
 * This activity contains two fragments: {@link SnapshotFragment} and {@link InputFragment}.
 */
public class SimulationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simulation);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frag_list, new SnapshotFragment())
                    .replace(R.id.bottom_sheet_container, new InputFragment())
                    .commit();
        }

        // --- Configure the Bottom Sheet Behavior ---
        View bottomSheet = findViewById(R.id.bottom_sheet_container);
        final BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheet);

        // Set the height of the 'peek' state after the layout has been drawn
        bottomSheet.post(() -> {
            View includedLayout = bottomSheet.findViewById(R.id.fragment_input);
            if (includedLayout != null) {
                int peekHeight = includedLayout.getHeight();
                behavior.setPeekHeight(peekHeight);

                // Set the padding on the main content to avoid overlap
                View mainContent = findViewById(R.id.frag_list);
                mainContent.setPadding(0, 0, 0, peekHeight);
            }
        });

        // Prevent the user from fully swiping it down and hiding it
        behavior.setHideable(false);

        // Ensure it starts in the collapsed (peeked) state
        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }
}
