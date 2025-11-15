package com.example.tmcalculator;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.tmcalculator.databinding.FragmentBottomSheetInputBinding;
import com.example.tmcalculator.game.GameSnapshot;
import com.example.tmcalculator.util.LocalisationManager;

/**
 * Takes input from user and sets it as a snapshot (usually the initial snapshot) by passing it to {@link SnapshotViewModel}
 */
public class InputFragment extends Fragment {

    private FragmentBottomSheetInputBinding binding;
    private SnapshotViewModel viewModel;
    private int dwellingRating = 0;
    private int tradingHouseRating = 0;
    private int templeRating = 0;
    private boolean hasSanctuary = false;
    private boolean hasStronghold = false;
    private int shovelLevel = 1;
    private int shippingLevel = 0;
    private boolean[] selectedFavTiles;
    private LocalisationManager localisationManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentBottomSheetInputBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        localisationManager = LocalisationManager.getInstance();

        viewModel = new ViewModelProvider(requireActivity()).get(SnapshotViewModel.class);

        setupRatingBars();
        setupSliders();

        binding.btnStronghold.setOnClickListener(v -> hasStronghold = !hasStronghold);
        binding.btnSanctuary.setOnClickListener(v -> hasSanctuary = !hasSanctuary);

        binding.btnApply.setOnClickListener(v -> {
            GameSnapshot ss = new GameSnapshot();
            ss.vp = tryParseInt(binding.inputVp.getText().toString());
            ss.coin = tryParseInt(binding.inputCoin.getText().toString());
            ss.worker = tryParseInt(binding.inputWorker.getText().toString());
            ss.priest = tryParseInt(binding.inputPriest.getText().toString());
            ss.power1 = tryParseInt(binding.inputPower1.getText().toString());
            ss.power2 = tryParseInt(binding.inputPower2.getText().toString());
            ss.power3 = tryParseInt(binding.inputPower3.getText().toString());
            ss.shovel = shovelLevel;
            ss.shipping = shippingLevel;
            ss.dwelling = dwellingRating;
            ss.tradingHouse = tradingHouseRating;
            ss.temple = templeRating;
            ss.stronghold = hasStronghold ? 1 : 0;
            ss.sanctuary = hasSanctuary ? 1 : 0;
            viewModel.setSnapshot(ss, 0);
        });
    }

    private void setupSliders() {
        binding.sliderShovel.addOnChangeListener((slider, value, fromUser) -> {
            shovelLevel = (int) value;
            binding.tvShovelLevel.setText(String.valueOf(shovelLevel));
        });

        binding.sliderShipping.addOnChangeListener((slider, value, fromUser) -> {
            shippingLevel = (int) value;
            binding.tvShippingLevel.setText(String.valueOf(shippingLevel));
        });
    }

    private void setupRatingBars() {
        setupDwellingRatingBar();
        setupTradingHouseRatingBar();
        setupTempleRatingBar();
    }

    private void updateAllRatingVisuals() {
        updateDwellingRatingVisuals();
        updateTradingHouseRatingVisuals();
        updateTempleRatingVisuals();
    }

    private void setupDwellingRatingBar() {
        LinearLayout ratingContainer = binding.dwellingRatingContainer;
        for (int i = 0; i < ratingContainer.getChildCount(); i++) {
            ImageView star = (ImageView) ratingContainer.getChildAt(i);
            final int rating = i + 1;
            star.setOnClickListener(v -> {
                dwellingRating = rating;
                updateDwellingRatingVisuals();
            });
            star.setOnLongClickListener(v -> {
                dwellingRating = rating - 1;
                updateDwellingRatingVisuals();
                return true;
            });
        }
    }

    private void setupTradingHouseRatingBar() {
        LinearLayout ratingContainer = binding.tradingHouseRatingContainer;
        for (int i = 0; i < ratingContainer.getChildCount(); i++) {
            ImageView star = (ImageView) ratingContainer.getChildAt(i);
            final int rating = i + 1;
            star.setOnClickListener(v -> {
                tradingHouseRating = rating;
                updateTradingHouseRatingVisuals();
            });
            star.setOnLongClickListener(v -> {
                tradingHouseRating = rating - 1;
                updateTradingHouseRatingVisuals();
                return true;
            });
        }
    }

    private void setupTempleRatingBar() {
        LinearLayout ratingContainer = binding.templeRatingContainer;
        for (int i = 0; i < ratingContainer.getChildCount(); i++) {
            ImageView star = (ImageView) ratingContainer.getChildAt(i);
            final int rating = i + 1;
            star.setOnClickListener(v -> {
                templeRating = rating;
                updateTempleRatingVisuals();
            });
            star.setOnLongClickListener(v -> {
                templeRating = rating - 1;
                updateTempleRatingVisuals();
                return true;
            });
        }
    }

    private void updateDwellingRatingVisuals() {
        LinearLayout ratingContainer = binding.dwellingRatingContainer;
        for (int i = 0; i < ratingContainer.getChildCount(); i++) {
            ImageView star = (ImageView) ratingContainer.getChildAt(i);
            if (i < dwellingRating) {
                star.setImageResource(R.drawable.dwellingcontent);
            } else {
                star.setImageResource(R.drawable.dwellingframe);
            }
        }
    }

    private void updateTradingHouseRatingVisuals() {
        LinearLayout ratingContainer = binding.tradingHouseRatingContainer;
        for (int i = 0; i < ratingContainer.getChildCount(); i++) {
            ImageView star = (ImageView) ratingContainer.getChildAt(i);
            if (i < tradingHouseRating) {
                star.setImageResource(R.drawable.th);
            } else {
                star.setImageResource(R.drawable.thframe);
            }
        }
    }

    private void updateTempleRatingVisuals() {
        LinearLayout ratingContainer = binding.templeRatingContainer;
        for (int i = 0; i < ratingContainer.getChildCount(); i++) {
            ImageView star = (ImageView) ratingContainer.getChildAt(i);
            if (i < templeRating) {
                star.setImageResource(R.drawable.temple);
            } else {
                star.setImageResource(R.drawable.templeframe);
            }
        }
    }

    private void setupFavMenu() {
        binding.btnFavor.setOnClickListener(v -> {
            String[] favorList = localisationManager.getFavorList().toArray(new String[0]);
            selectedFavTiles = new boolean[favorList.length];
            AlertDialog alertDialog = new AlertDialog.Builder((requireContext()))
                    .setMultiChoiceItems(favorList, selectedFavTiles, (dialog, which, isChecked) -> {
                        // TODO
                    }).setPositiveButton("OK", (dialog, which) -> {
                        selectedFavTiles = new boolean[favorList.length];

                    }).setNegativeButton("Cancel", (dialog, which) -> {

                    }).create();
            alertDialog.show();
        });
    }

    private void setupBonMenu() {
        binding.btnBonus.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(requireContext(), binding.btnBonus);

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
