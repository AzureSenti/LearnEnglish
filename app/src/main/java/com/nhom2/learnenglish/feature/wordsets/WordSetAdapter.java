package com.nhom2.learnenglish.feature.wordsets;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nhom2.learnenglish.R;
import com.nhom2.learnenglish.model.WordSet;

import java.util.ArrayList;
import java.util.List;

public class WordSetAdapter extends RecyclerView.Adapter<WordSetAdapter.ViewHolder> {

    private List<WordSet> items = new ArrayList<>();
    private final OnItemClickListener listener;
    private final OnMenuClickListener menuListener;

    public interface OnItemClickListener {
        void onItemClick(WordSet item);
    }

    public interface OnMenuClickListener {
        void onMenuClick(WordSet item, View anchor);
    }

    public WordSetAdapter(OnItemClickListener listener, OnMenuClickListener menuListener) {
        this.listener = listener;
        this.menuListener = menuListener;
    }

    public void updateData(List<WordSet> newItems) {
        this.items = newItems != null ? newItems : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_word_set, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WordSet item = items.get(position);
        holder.tvSetName.setText(item.getTitle());
        holder.tvWordCount.setText(item.getWordCountLabel());
        holder.ivFolder.setImageResource(resolveCategoryIcon(item.getCategoryIcon()));

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(item);
            }
        });

        holder.ivMore.setOnClickListener(v -> {
            if (menuListener != null) {
                menuListener.onMenuClick(item, v);
            }
        });
    }

    private int resolveCategoryIcon(String category) {
        if (category == null) {
            return R.drawable.ic_folder;
        }
        switch (category) {
            case "travel":
                return R.drawable.ic_explore;
            case "food":
                return R.drawable.ic_spa;
            case "study":
            case "book":
                return R.drawable.ic_book;
            case "business":
                return R.drawable.ic_chart;
            case "laptop":
            case "tech":
                return R.drawable.ic_vocab;
            default:
                return R.drawable.ic_folder;
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvSetName;
        TextView tvWordCount;
        ImageView ivMore;
        ImageView ivFolder;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSetName = itemView.findViewById(R.id.tv_set_name);
            tvWordCount = itemView.findViewById(R.id.tv_word_count);
            ivMore = itemView.findViewById(R.id.iv_more);
            ivFolder = itemView.findViewById(R.id.iv_folder);
        }
    }
}
