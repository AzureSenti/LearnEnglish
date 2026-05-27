package com.nhom2.learnenglish.feature.wordsets;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.card.MaterialCardView;
import com.nhom2.learnenglish.R;
import com.nhom2.learnenglish.core.data.local.entity.word.WordSetEntity;
import java.util.List;

public class WordSetSelectionAdapter extends RecyclerView.Adapter<WordSetSelectionAdapter.ViewHolder> {
    private final List<WordSetEntity> items;
    private int selectedPosition = -1; // -1 là chưa chọn gì
    private OnItemSelectedListener listener;

    public interface OnItemSelectedListener {
        void onSelected(WordSetEntity item);
    }

    public WordSetSelectionAdapter(List<WordSetEntity> items, OnItemSelectedListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_word_set_selection, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WordSetEntity item = items.get(position);
        holder.tvName.setText(item.getName());

        boolean isSelected = (selectedPosition == position);

        // Update UI khi chọn
        if (isSelected) {
            holder.card.setStrokeColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.green_tag_text));
            holder.card.setStrokeWidth(4);
            holder.ivCheck.setVisibility(View.VISIBLE);
        } else {
            holder.card.setStrokeColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.border_light));
            holder.card.setStrokeWidth(2);
            holder.ivCheck.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            int previous = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(previous);
            notifyItemChanged(selectedPosition);
            if (listener != null) listener.onSelected(item);
        });
    }

    public long getSelectedSetId() {
        if (selectedPosition != -1) return items.get(selectedPosition).getId();
        return -1;
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        ImageView ivCheck;
        MaterialCardView card;
        ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_selection_name);
            ivCheck = itemView.findViewById(R.id.iv_check);
            card = itemView.findViewById(R.id.card_word_set_selection);
        }
    }
}