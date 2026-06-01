package com.nhom2.learnenglish.feature.wordsets;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nhom2.learnenglish.R;
import com.nhom2.learnenglish.core.data.local.entity.word.WordSetEntity;

import java.util.ArrayList;
import java.util.List;

public class WordSetAdapter extends RecyclerView.Adapter<WordSetAdapter.ViewHolder> {

    public interface OnItemActionListener {
        void onItemClick(WordSetEntity item);
        void onMoreClick(WordSetEntity item);
    }

    private List<WordSetEntity> items = new ArrayList<>();
    private final OnItemActionListener listener;

    public WordSetAdapter(OnItemActionListener listener) {
        this.listener = listener;
    }

    public void updateData(List<WordSetEntity> newItems) {
        this.items = newItems != null ? newItems : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_word_set, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WordSetEntity item = items.get(position);
        holder.tvSetName.setText(item.getName());

        // Lấy icon từ trường description
        int iconResId = getIconResId(item.getDescription());
        holder.ivSetIcon.setImageResource(iconResId);
        holder.ivSetIcon.setColorFilter(holder.itemView.getContext().getResources().getColor(R.color.blue_primary, null));

        holder.tvWordCount.setText("Bộ từ vựng");

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(item);
        });

        holder.ivMoreActions.setOnClickListener(v -> {
            if (listener != null) listener.onMoreClick(item);
        });
    }

    private int getIconResId(String iconName) {
        if (iconName == null) return R.drawable.ic_folder;
        switch (iconName) {
            case "airplane": return R.drawable.ic_airplane;
            case "restaurant": return R.drawable.ic_restaurant;
            case "book": return R.drawable.ic_book;
            case "grammar": return R.drawable.ic_grammar;
            default: return R.drawable.ic_folder;
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvSetName, tvWordCount;
        ImageView ivMoreActions, ivSetIcon;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSetName = itemView.findViewById(R.id.tv_set_name);
            tvWordCount = itemView.findViewById(R.id.tv_word_count);
            ivMoreActions = itemView.findViewById(R.id.iv_more_actions);
            ivSetIcon = itemView.findViewById(R.id.iv_set_icon);
        }
    }
}
