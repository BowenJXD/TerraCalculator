package com.example.tmcalculator;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.tmcalculator.game.ActionManager;
import com.example.tmcalculator.game.GameAction;
import com.example.tmcalculator.game.GameSnapshot;
import com.example.tmcalculator.databinding.ItemSnapshotBinding;
import com.example.tmcalculator.databinding.ItemSnapshotHeaderBinding;

import java.util.List;

public class SnapshotRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private List<GameSnapshot> snapshots;
    private List<String> actions;
    private OnSnapshotActionListener listener;
    private Context context;

    public interface OnSnapshotActionListener {
        void onAction(GameSnapshot ss, View anchor, Button btnAction, int position);
    }

    public SnapshotRecyclerViewAdapter(OnSnapshotActionListener listener) {
        this.listener = listener;
    }

    public List<GameSnapshot> getSnapshots() {
        return snapshots;
    }

    public void setSnapshots(List<GameSnapshot> snapshots) {
        this.snapshots = snapshots;
    }

    public List<String> getActions() {
        return actions;
    }

    public void setActions(List<String> actions) {
        this.actions = actions;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        }
        return TYPE_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        if (viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_snapshot_header, parent, false);
            return new HeaderViewHolder(ItemSnapshotHeaderBinding.bind(view));
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_snapshot, parent, false);
        return new ItemViewHolder(ItemSnapshotBinding.bind(view));
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            ItemViewHolder itemHolder = (ItemViewHolder) holder;
            int itemPosition = position - 1; // Adjust for header

            GameSnapshot ss = snapshots.get(itemPosition);
            if (actions.size() <= itemPosition) {
                actions.add(GameAction.NONE.toString());
            }
            if (itemPosition < snapshots.size() - 1) {
                String action = actions.get(itemPosition);
                String actionName = LocalisationManager.getInstance(context).getActionLocalisation(action);
                itemHolder.btnAction.setText(actionName);
            } else if (itemPosition == snapshots.size() - 1) {
                String actionName = LocalisationManager.getInstance(context).getActionLocalisation(GameAction.NONE.name());
                itemHolder.btnAction.setText(actionName);
            }
            itemHolder.tvIndex.setText(String.valueOf(itemPosition + 1));
            itemHolder.tvVp.setText(String.valueOf(ss.vp));
            itemHolder.tvCoin.setText(String.valueOf(ss.coin));
            itemHolder.tvWorker.setText(String.valueOf(ss.worker));
            itemHolder.tvPriest.setText(String.valueOf(ss.priest));
            String powerStr = String.format("%d|%d|%d", ss.power1, ss.power2, ss.power3);
            itemHolder.tvPower.setText(powerStr);

            itemHolder.btnAction.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onAction(ss, v, itemHolder.btnAction, itemPosition);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (snapshots == null) {
            return 0;
        }
        return snapshots.size() + 1; // Add 1 for the header
    }

    // ViewHolder for the header
    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        public HeaderViewHolder(ItemSnapshotHeaderBinding binding) {
            super(binding.getRoot());
        }
    }

    // ViewHolder for the regular items
    public class ItemViewHolder extends RecyclerView.ViewHolder {
        public TextView tvIndex;
        public Button btnAction;
        public TextView tvVp;
        public TextView tvCoin;
        public TextView tvWorker;
        public TextView tvPriest;
        public TextView tvPower;

        public ItemViewHolder(ItemSnapshotBinding binding) {
            super(binding.getRoot());
            tvIndex = binding.tvIndex;
            btnAction = binding.btnAction;
            tvVp = binding.tvVp;
            tvCoin = binding.tvCoin;
            tvWorker = binding.tvWorker;
            tvPriest = binding.tvPriest;
            tvPower = binding.tvPower;
        }
    }
}
