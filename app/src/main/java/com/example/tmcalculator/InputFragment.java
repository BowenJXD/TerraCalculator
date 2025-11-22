package com.example.tmcalculator;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
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
import com.example.tmcalculator.game.TileManager;
import com.example.tmcalculator.util.LocalisationManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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

    private List<String> favTiles = new ArrayList<>();
    private String bonTile;
    private String scoTile;
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
        setupBonMenu();
        setupFavMenu();
        setupScoMenu();

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
            ss.tiles.addAll(favTiles);
            ss.tiles.add(bonTile);
            ss.tiles.add(scoTile);
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
            String[] favorKeys = TileManager.getInstance().getFavorTiles().toArray(new String[0]);
            String[] favorValues = Arrays.stream(favorKeys)
                    .map(key -> {
                        String localized = localisationManager.getFavorLocalisation(key);
                        return localized != null ? localized : key;
                    })
                    .toArray(String[]::new);
            boolean[] checkedItems = new boolean[favorKeys.length];
            for(int i = 0; i < favorKeys.length; i++) {
                checkedItems[i] = favTiles.contains(favorKeys[i]);
            }

            new AlertDialog.Builder(requireContext())
                    .setMultiChoiceItems(favorValues, checkedItems, (dialog, which, isChecked) -> {
                        checkedItems[which] = isChecked;
                    })
                    .setPositiveButton("OK", (dialog, which) -> {
                        favTiles.clear();
                        for (int i = 0; i < favorKeys.length; i++) {
                            if (checkedItems[i]) {
                                favTiles.add(favorKeys[i]);
                            }
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .create()
                    .show();
        });
    }

    private void setupBonMenu() {
        binding.btnBonus.setOnClickListener(v -> {
            List<String> bonKeys = TileManager.getInstance().getBonusTiles();
            PopupMenu popupMenu = new PopupMenu(requireContext(), binding.btnBonus);
            Menu menu = popupMenu.getMenu();
            for (int i = 0; i < bonKeys.size(); i++) {
                menu.add(Menu.NONE, i, i, localisationManager.getBonusLocalisation(bonKeys.get(i)));
            }
            popupMenu.setOnMenuItemClickListener(item -> {
                int i = item.getItemId();
                bonTile = bonKeys.get(i);
                binding.btnBonus.setText(item.getTitle());
                return true;
            });
            popupMenu.show();
        });
    }

    private void setupScoMenu() {
        binding.btnScoring.setOnClickListener(v -> {
            List<String> scoKeys = TileManager.getInstance().getScoringTiles();
            PopupMenu popupMenu = new PopupMenu(requireContext(), binding.btnScoring);
            Menu menu = popupMenu.getMenu();
            for (int i = 0; i < scoKeys.size(); i++) {
                menu.add(Menu.NONE, i, i, localisationManager.getScoringLocalisation(scoKeys.get(i)));
            }
            popupMenu.setOnMenuItemClickListener(item -> {
                int i = item.getItemId();
                scoTile = scoKeys.get(i);
                binding.btnScoring.setText(item.getTitle());
                return true;
            });
            popupMenu.show();
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
