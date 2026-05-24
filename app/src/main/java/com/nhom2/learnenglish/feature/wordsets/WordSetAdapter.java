package com.nhom2.learnenglish.feature.wordsets;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nhom2.learnenglish.R;
import com.nhom2.learnenglish.core.data.local.entity.word.WordSetEntity;

import java.util.ArrayList;
import java.util.List;

public class WordSetAdapter extends RecyclerView.Adapter<WordSetAdapter.ViewHolder> {

    private List<WordSetEntity> items = new ArrayList<>();
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(WordSetEntity item);
        void onItemLongClick(WordSetEntity item);
    }

    public WordSetAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void updateData(List<WordSetEntity> newItems) {
        this.items = newItems;
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
        // For now, word count is not in entity, we might need another way to get it or just show description
        holder.tvWordCount.setText(item.getDescription());
        
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(item);
            }
        });
        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) {
                listener.onItemLongClick(item);
                return true;
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvSetName;
        TextView tvWordCount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSetName = itemView.findViewById(R.id.tv_set_name);
            tvWordCount = itemView.findViewById(R.id.tv_word_count);
        }
    }
}
