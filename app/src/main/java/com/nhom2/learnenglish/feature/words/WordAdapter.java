package com.nhom2.learnenglish.feature.words;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nhom2.learnenglish.R;
import com.nhom2.learnenglish.model.Word;

import java.util.ArrayList;
import java.util.List;

public class WordAdapter extends RecyclerView.Adapter<WordAdapter.WordViewHolder> {

    private List<Word> wordList = new ArrayList<>();
    private OnWordActionListener actionListener;

    public interface OnWordActionListener {
        void onWordMenuClick(Word word, View anchor);
    }

    public void setOnWordActionListener(OnWordActionListener listener) {
        this.actionListener = listener;
    }

    public void updateData(List<Word> newList) {
        this.wordList = newList != null ? newList : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public WordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_word_card, parent, false);
        return new WordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WordViewHolder holder, int position) {
        Word word = wordList.get(position);
        holder.tvWord.setText(word.getEnglish());
        holder.tvPhonetic.setText(word.getVietnameseMeaning());
        holder.tvLevel.setText(word.getLevelLabel());

        holder.btnSpeak.setOnClickListener(v ->
                Toast.makeText(v.getContext(), word.getEnglish(), Toast.LENGTH_SHORT).show()
        );

        holder.ivMore.setOnClickListener(v -> {
            if (actionListener != null) {
                actionListener.onWordMenuClick(word, v);
            }
        });
    }

    @Override
    public int getItemCount() {
        return wordList.size();
    }

    static class WordViewHolder extends RecyclerView.ViewHolder {
        TextView tvWord;
        TextView tvPhonetic;
        TextView tvLevel;
        ImageView ivMore;
        ImageButton btnSpeak;

        WordViewHolder(@NonNull View itemView) {
            super(itemView);
            tvWord = itemView.findViewById(R.id.tv_word);
            tvPhonetic = itemView.findViewById(R.id.tv_phonetic);
            tvLevel = itemView.findViewById(R.id.tv_word_level);
            ivMore = itemView.findViewById(R.id.iv_word_more);
            btnSpeak = itemView.findViewById(R.id.btn_speak);
        }
    }
}
