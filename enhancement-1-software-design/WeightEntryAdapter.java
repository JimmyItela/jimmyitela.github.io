package com.example.weighttracker.ui.dashboard;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weighttracker.R;
import com.example.weighttracker.data.model.WeightEntry;

/** Renders the weight history as a RecyclerView list, replacing the manually built TableLayout rows. */
public class WeightEntryAdapter extends RecyclerView.Adapter<WeightEntryAdapter.ViewHolder> {

    /** Notifies the Activity when the user taps Edit or Delete on a row. */
    public interface OnEntryActionListener {
        void onEdit(WeightEntry entry);

        void onDelete(WeightEntry entry);
    }

    private final OnEntryActionListener listener;
    private List<WeightEntry> entries = Collections.emptyList();

    public WeightEntryAdapter(OnEntryActionListener listener) {
        this.listener = listener;
    }

    public void submitList(List<WeightEntry> newEntries) {
        entries = newEntries != null ? newEntries : Collections.emptyList();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_weight_entry, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WeightEntry entry = entries.get(position);
        holder.date.setText(entry.getEntryDate());
        holder.weight.setText(String.format(Locale.US, "%.1f lbs", entry.getWeight()));
        holder.edit.setOnClickListener(v -> listener.onEdit(entry));
        holder.delete.setOnClickListener(v -> listener.onDelete(entry));
    }

    @Override
    public int getItemCount() {
        return entries.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView date;
        final TextView weight;
        final Button edit;
        final Button delete;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.txtEntryDate);
            weight = itemView.findViewById(R.id.txtEntryWeight);
            edit = itemView.findViewById(R.id.btnEntryEdit);
            delete = itemView.findViewById(R.id.btnEntryDelete);
        }
    }
}
