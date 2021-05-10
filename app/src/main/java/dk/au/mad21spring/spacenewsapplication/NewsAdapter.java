package dk.au.mad21spring.spacenewsapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import dk.au.mad21spring.spacenewsapplication.Database.Article;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

    public interface INewsItemClickedListener{
        void onArticleClicked(int index);
    }

    private INewsItemClickedListener listener;
    private List<Article> articleList;
    Context context;

    public NewsAdapter(INewsItemClickedListener listener, Context context) {
        this.listener = listener;
        this.context = context;
        this.articleList = new ArrayList<>();
        this.articleList = articleList;
    }

    public void updateNewsAdapter(List<Article> lists) {
        articleList = lists;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_item, parent, false);   // Henvsining til recycler item
        NewsViewHolder vh = new NewsViewHolder(v,listener);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        holder.txtTitle.setText(articleList.get(position).Title);
        holder.txtPublished.setText(articleList.get(position).PublishedAt);
        Glide.with(holder.imgArticle.getContext()).load(articleList.get(position).ImageUrl).into(holder.imgArticle); // skal vi have et default billede?
    }

    @Override
    public int getItemCount() {
        if (articleList != null ) {
            return articleList.size();
        } else {
            return 0;
        }
    }

    public class NewsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

//        //Widgets
        ImageView imgArticle;
        TextView txtTitle, txtPublished;

        INewsItemClickedListener listener;

        public NewsViewHolder(@NonNull View itemView, INewsItemClickedListener newsItemClickedListener) {
            super(itemView);

            //references from layout file
            imgArticle = itemView.findViewById(R.id.imgArticle);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtPublished = itemView.findViewById(R.id.txtPublished);
            listener = newsItemClickedListener; //save listener interface

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onArticleClicked(getAdapterPosition());
        }
    }
}
