package com.nhom2.learnenglish.feature.game;

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

import java.util.List;

public class GameOptionAdapter extends RecyclerView.Adapter<GameOptionAdapter.OptionViewHolder> {

    private final List<String> options;
    private final int correctOptionIndex;
    private final OnOptionClickListener listener;

    private int selectedPosition = -1;
    private boolean isAnswerChecked = false;

    public interface OnOptionClickListener {
        void onOptionSelected(int position);
    }

    public GameOptionAdapter(List<String> options, int correctOptionIndex, OnOptionClickListener listener) {
        this.options = options;
        this.correctOptionIndex = correctOptionIndex;
        this.listener = listener;
    }

    @NonNull
    @Override
    public OptionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_game_option, parent, false);
        OptionViewHolder holder = new OptionViewHolder(view);

        // Gắn sự kiện click ĐÚNG 1 LẦN DUY NHẤT ở đây
        holder.itemView.setOnClickListener(v -> {
            // Nếu đã bấm Kiểm tra rồi thì khóa không cho chọn lại
            if (isAnswerChecked) return;

            // Lấy vị trí hiện tại của ô vừa bị click
            int position = holder.getAdapterPosition();

            // Tránh lỗi click vào ô đang bị hiệu ứng xóa/ẩn (NO_POSITION là -1)
            if (position != RecyclerView.NO_POSITION) {
                int previousSelected = selectedPosition;
                selectedPosition = position;

                // Vẽ lại 2 ô để đổi viền xanh
                notifyItemChanged(previousSelected);
                notifyItemChanged(selectedPosition);

                // Báo về Activity
                listener.onOptionSelected(selectedPosition);
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull OptionViewHolder holder, int position) {
        String optionText = options.get(position);
        holder.tvOptionText.setText(optionText);

        // Reset state
        holder.cardOption.setStrokeWidth(2);
        holder.cardOption.setStrokeColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.border_light));
        holder.cardOption.setCardBackgroundColor(Color.WHITE);
        holder.ivStatusIcon.setVisibility(View.GONE);

        if (!isAnswerChecked) {
            if (selectedPosition == position) {
                holder.cardOption.setStrokeColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.blue_primary));
                holder.cardOption.setStrokeWidth(4);
            }

            ;
        } else {
            if (position == correctOptionIndex) {
                // Correct answer UI - using the requested background logic
                holder.cardOption.setBackgroundResource(R.drawable.bg_option_correct);
                holder.cardOption.setStrokeWidth(0); // Shape handles the stroke
                holder.ivStatusIcon.setVisibility(View.VISIBLE);
                holder.ivStatusIcon.setImageResource(android.R.drawable.checkbox_on_background);
                holder.ivStatusIcon.setColorFilter(ContextCompat.getColor(holder.itemView.getContext(), R.color.green_tag_text));
            } else if (position == selectedPosition) {
                // Wrong answer UI
                holder.cardOption.setBackgroundResource(R.drawable.bg_option_error);
                holder.cardOption.setStrokeWidth(0);
                holder.ivStatusIcon.setVisibility(View.VISIBLE);
                holder.ivStatusIcon.setImageResource(android.R.drawable.ic_delete);
                holder.ivStatusIcon.setColorFilter(ContextCompat.getColor(holder.itemView.getContext(), R.color.error_red));
            }
        }
    }

    @Override
    public int getItemCount() {
        return options == null ? 0 : options.size();
    }

    public boolean checkAnswer() {
        isAnswerChecked = true;
        notifyDataSetChanged();
        return selectedPosition == correctOptionIndex;
    }

    public static class OptionViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardOption;
        TextView tvOptionText;
        ImageView ivStatusIcon;

        public OptionViewHolder(@NonNull View itemView) {
            super(itemView);
            cardOption = itemView.findViewById(R.id.cardOption);
            tvOptionText = itemView.findViewById(R.id.tvOptionText);
            ivStatusIcon = itemView.findViewById(R.id.ivStatusIcon);
        }
    }
}
