package com.nhom2.learnenglish.feature.stories;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nhom2.learnenglish.R;
import com.nhom2.learnenglish.core.data.local.entity.StoryEntity;

import java.util.ArrayList;
import java.util.List;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.StoryViewHolder> {

    private List<StoryEntity> stories = new ArrayList<>();
    private final OnStoryClickListener listener;

    public interface OnStoryClickListener {
        void onStoryClick(StoryEntity story);
    }

    public StoryAdapter(OnStoryClickListener listener) {
        this.listener = listener;
    }

    public void updateData(List<StoryEntity> newData) {
        this.stories = newData != null ? newData : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public StoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_story_simplified, parent, false);
        return new StoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoryViewHolder holder, int position) {
        holder.bind(stories.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return stories.size();
    }

    static class StoryViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivThumb;
        private final TextView tvLevel;
        private final TextView tvTitle;
        private final TextView tvDuration;

        public StoryViewHolder(@NonNull View itemView) {
            super(itemView);
            ivThumb = itemView.findViewById(R.id.iv_story_thumb);
            tvLevel = itemView.findViewById(R.id.tv_story_level);
            tvTitle = itemView.findViewById(R.id.tv_story_title);
            tvDuration = itemView.findViewById(R.id.tv_story_duration);
        }

        public void bind(StoryEntity story, OnStoryClickListener listener) {
            tvTitle.setText(story.getTitle());
            tvLevel.setText(story.getLevel() != null ? story.getLevel() : "All");
            // Hiển thị Thể loại (Category) ở mục phụ phụ thay cho readTime
            tvDuration.setText(story.getCategory() != null ? story.getCategory() : "Short Story");

            if (story.getImage() != null && !story.getImage().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(story.getImage())
                        .placeholder(android.R.color.darker_gray)
                        .into(ivThumb);
            } else {
                ivThumb.setImageResource(android.R.color.darker_gray);
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onStoryClick(story);
                }
            });
        }
    }
}