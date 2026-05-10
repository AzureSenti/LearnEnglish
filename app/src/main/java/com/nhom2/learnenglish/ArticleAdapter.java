package com.nhom2.learnenglish;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder> {

    private List<Article> articleList;
    private OnArticleClickListener listener;

    public interface OnArticleClickListener {
        void onArticleClick(Article article);
    }

    public ArticleAdapter(List<Article> articleList, OnArticleClickListener listener) {
        this.articleList = articleList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ArticleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_article, parent, false);
        return new ArticleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArticleViewHolder holder, int position) {
        Article article = articleList.get(position);
        holder.tvTitle.setText(article.getTitle());
        holder.tvDescription.setText(article.getDescription());
        holder.tvLevel.setText(article.getLevel());
        holder.tvCategory.setText(article.getCategory());
        holder.tvReadTime.setText(article.getReadTime());
        
        holder.tvStatus.setVisibility(article.isCompleted() ? View.VISIBLE : View.GONE);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onArticleClick(article);
            }
        });
    }

    @Override
    public int getItemCount() {
        return articleList.size();
    }

    static class ArticleViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDescription, tvLevel, tvCategory, tvReadTime, tvStatus;

        public ArticleViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvDescription = itemView.findViewById(R.id.tv_description);
            tvLevel = itemView.findViewById(R.id.tv_level_tag);
            tvCategory = itemView.findViewById(R.id.tv_category);
            tvReadTime = itemView.findViewById(R.id.tv_read_time);
            tvStatus = itemView.findViewById(R.id.tv_status_tag);
        }
    }
}