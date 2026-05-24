package com.nhom2.learnenglish.feature.wordsets;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
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
        void onEditClick(WordSetEntity item);
        void onDeleteClick(WordSetEntity item);
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
        holder.tvWordCount.setText(item.getDescription());
        
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(item);
            }
        });

        // Set up the "more" icon to show a popup menu for Edit/Delete
        holder.ivMore.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(v.getContext(), holder.ivMore);
            // We can add menu items programmatically or inflate a menu resource. Here we add programmatically for simplicity.
            popup.getMenu().add(0, 1, 0, "Sửa");
            popup.getMenu().add(0, 2, 1, "Xóa");
            
            popup.setOnMenuItemClickListener(menuItem -> {
                if (listener != null) {
                    if (menuItem.getItemId() == 1) {
                        listener.onEditClick(item);
                        return true;
                    } else if (menuItem.getItemId() == 2) {
                        listener.onDeleteClick(item);
                        return true;
                    }
                }
                return false;
            });
            popup.show();
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvSetName;
        TextView tvWordCount;
        ImageView ivMore;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSetName = itemView.findViewById(R.id.tv_set_name);
            tvWordCount = itemView.findViewById(R.id.tv_word_count);
            ivMore = itemView.findViewById(R.id.iv_more);
        }
    }
}
