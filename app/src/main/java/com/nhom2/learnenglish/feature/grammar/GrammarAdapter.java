package com.nhom2.learnenglish.feature.grammar;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.nhom2.learnenglish.R;
import com.nhom2.learnenglish.core.data.local.model.GrammarLessonWithStatus;
import java.util.ArrayList;
import java.util.List;

public class GrammarAdapter extends RecyclerView.Adapter<GrammarAdapter.GrammarViewHolder> {

    private List<GrammarLessonWithStatus> lessonList = new ArrayList<>();
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onLessonClick(long lessonId);
    }

    public GrammarAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void updateData(List<GrammarLessonWithStatus> newList) {
        this.lessonList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public GrammarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_grammar_lesson, parent, false);
        return new GrammarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GrammarViewHolder holder, int position) {
        GrammarLessonWithStatus item = lessonList.get(position);
        holder.bind(item, listener);
    }

    @Override
    public int getItemCount() {
        return lessonList.size();
    }

    static class GrammarViewHolder extends RecyclerView.ViewHolder {
        View layoutContainer;
        TextView tvOrderIndex, tvLessonTitle, tvTheoryLabel, tvQuizLabel;
        ImageView ivLocked, ivTheoryDone, ivQuizDone;

        public GrammarViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutContainer = itemView.findViewById(R.id.layout_container);
            tvOrderIndex = itemView.findViewById(R.id.tv_order_index);
            tvLessonTitle = itemView.findViewById(R.id.tv_lesson_title);
            tvTheoryLabel = itemView.findViewById(R.id.tv_theory_label);
            tvQuizLabel = itemView.findViewById(R.id.tv_quiz_label);
            ivLocked = itemView.findViewById(R.id.iv_locked);
            ivTheoryDone = itemView.findViewById(R.id.iv_theory_done);
            ivQuizDone = itemView.findViewById(R.id.iv_quiz_done);
        }

        public void bind(GrammarLessonWithStatus item, OnItemClickListener listener) {
            tvLessonTitle.setText(item.getLesson().getTitle());
            tvOrderIndex.setText(String.valueOf(item.getLesson().getOrderIndex()));

            // Màu xanh lục cho các trạng thái đã hoàn thành
            int colorDone = Color.parseColor("#4CAF50");
            int colorPending = Color.parseColor("#9E9E9E");

            if (item.isUnlocked()) {
                // UI Mở khóa
                layoutContainer.setAlpha(1.0f);
                itemView.setEnabled(true);
                tvOrderIndex.setVisibility(View.VISIBLE);
                ivLocked.setVisibility(View.GONE);

                // Cập nhật tích xanh
                ivTheoryDone.setColorFilter(item.isTheoryCompleted() ? colorDone : colorPending);
                tvTheoryLabel.setTextColor(item.isTheoryCompleted() ? colorDone : colorPending);

                ivQuizDone.setColorFilter(item.isQuizPassed() ? colorDone : colorPending);
                tvQuizLabel.setTextColor(item.isQuizPassed() ? colorDone : colorPending);

                itemView.setOnClickListener(v -> listener.onLessonClick(item.getLesson().getId()));
            } else {
                // UI Bị khóa
                layoutContainer.setAlpha(0.5f);
                itemView.setEnabled(false); // Chặn click
                tvOrderIndex.setVisibility(View.GONE);
                ivLocked.setVisibility(View.VISIBLE);

                ivTheoryDone.setColorFilter(colorPending);
                tvTheoryLabel.setTextColor(colorPending);
                ivQuizDone.setColorFilter(colorPending);
                tvQuizLabel.setTextColor(colorPending);
            }
        }
    }
}