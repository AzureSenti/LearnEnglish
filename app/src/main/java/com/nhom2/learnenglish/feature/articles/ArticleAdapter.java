package com.nhom2.learnenglish.feature.articles;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nhom2.learnenglish.R;
import com.nhom2.learnenglish.core.data.local.entity.ArticleEntity;
import android.widget.ImageView;
import com.bumptech.glide.Glide;

import java.util.List;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder> {


    private List<ArticleEntity> articleData;
    private OnArticleClickListener listener;

    public interface OnArticleClickListener {
        void onArticleClick(ArticleEntity article);
    }

    public ArticleAdapter( OnArticleClickListener listener) {

        this.listener = listener;
    }

    public void updateData(List<ArticleEntity> newData) {
        this.articleData = newData;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public ArticleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_article, parent, false);
        return new ArticleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArticleViewHolder holder, int position) {
        ArticleEntity article = articleData.get(position);


        holder.tvTitle.setText(article.getTitle());
        holder.tvDescription.setText(article.getDescription());
        holder.tvLevel.setText(article.getLevel());
        holder.tvCategory.setText(article.getCategory());
        holder.tvReadTime.setText(article.getReadTime());
        holder.tvStatus.setText("Báo hay");

//        holder.tvStatus.setVisibility(article.isCompleted() ? View.VISIBLE : View.GONE);
        // TẢI ẢNH TỪ URL BẰNG GLIDE
        if (article.getImage() != null && !article.getImage().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(article.getImage())
                    .placeholder(android.R.color.darker_gray) // Ảnh hiển thị tạm trong lúc tải
                    .into(holder.ivImage);
        } else {
            holder.ivImage.setImageResource(android.R.color.darker_gray); // Ảnh mặc định nếu không có link
        }


        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onArticleClick(article);
            }
        });
    }
    @Override
    public int getItemCount() {
        return articleData != null ? articleData.size() : 0;
    }

    static class ArticleViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDescription, tvLevel, tvCategory, tvReadTime, tvStatus;
        ImageView ivImage;

        public ArticleViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.iv_article_image);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvDescription = itemView.findViewById(R.id.tv_description);
            tvLevel = itemView.findViewById(R.id.tv_level_tag);
            tvCategory = itemView.findViewById(R.id.tv_category);
            tvReadTime = itemView.findViewById(R.id.tv_read_time);
            tvStatus = itemView.findViewById(R.id.tv_status_tag);
        }
    }
}