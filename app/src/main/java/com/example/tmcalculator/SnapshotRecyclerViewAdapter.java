package com.example.tmcalculator;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.tmcalculator.game.GameSnapshot;
import com.example.tmcalculator.databinding.ItemSnapshotBinding;

import java.util.List;

/**
 *
 */
public class SnapshotRecyclerViewAdapter extends RecyclerView.Adapter<SnapshotRecyclerViewAdapter.ViewHolder> {

    private List<GameSnapshot> snapshots;
    private OnSnapshotActionListener listener;
    private Context context;

    public interface OnSnapshotActionListener{
        void onEditSnapshot(GameSnapshot ss);
        void onDeleteSnapshot(GameSnapshot ss);
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

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_snapshot, parent, false);
        this.context = parent.getContext();
        return new ViewHolder(ItemSnapshotBinding.bind(view));

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        GameSnapshot ss = snapshots.get(position);
        holder.tvVp.setText(String.valueOf(ss.vp));
        holder.tvCoin.setText(String.valueOf(ss.coin));
        holder.tvWorker.setText(String.valueOf(ss.worker));
        holder.tvPriest.setText(String.valueOf(ss.priest));
        String powerStr = String.format("%d|%d|%d", ss.power1, ss.power2, ss.power3);
        holder.tvPower.setText(powerStr);

        holder.btnAction.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAction(ss, v, holder.btnAction, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return snapshots.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public Button btnAction;

        public TextView tvVp;
        public TextView tvCoin;
        public TextView tvWorker;
        public TextView tvPriest;
        public TextView tvPower;

        public ViewHolder(ItemSnapshotBinding binding) {
            super(binding.getRoot());
            btnAction = binding.btnAction;
            tvVp = binding.tvVp;
            tvCoin = binding.tvCoin;
            tvWorker = binding.tvWorker;
            tvPriest = binding.tvPriest;
            tvPower = binding.tvPower;
        }
    }
}
