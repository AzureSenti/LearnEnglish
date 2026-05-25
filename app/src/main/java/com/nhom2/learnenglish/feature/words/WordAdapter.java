package com.nhom2.learnenglish.feature.words;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nhom2.learnenglish.R;
import com.nhom2.learnenglish.core.data.model.WordWithProgress;

import java.util.ArrayList;
import java.util.List;

public class WordAdapter extends RecyclerView.Adapter<WordAdapter.WordViewHolder> {

    private List<WordWithProgress> wordList = new ArrayList<>();

    public WordAdapter() {
    }

    public void updateData(List<WordWithProgress> newList) {
        this.wordList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public WordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_word, parent, false);
        return new WordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WordViewHolder holder, int position) {
        WordWithProgress word = wordList.get(position);
        holder.tvWord.setText(word.getEnglishWord());
        holder.tvPhonetic.setText(word.getVietnameseMeaning());
        
        int level = word.getLevel() != null ? word.getLevel() : 0;
        holder.tvLevel.setText("Lv. " + level);
    }

    @Override
    public int getItemCount() {
        return wordList != null ? wordList.size() : 0;
    }

    static class WordViewHolder extends RecyclerView.ViewHolder {
        TextView tvWord;
        TextView tvPhonetic;
        TextView tvLevel;

        public WordViewHolder(@NonNull View itemView) {
            super(itemView);
            tvWord = itemView.findViewById(R.id.tv_word);
            tvPhonetic = itemView.findViewById(R.id.tv_phonetic);
            tvLevel = itemView.findViewById(R.id.tv_word_level);
        }
    }
}
