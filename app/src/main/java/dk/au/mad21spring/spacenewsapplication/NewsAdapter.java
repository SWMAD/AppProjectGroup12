package dk.au.mad21spring.spacenewsapplication;

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

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

    public interface INewsItemClickedListener{
        void onArticleClicked(int index);
    }

    private INewsItemClickedListener listener;
    private List<Article> articleList;

    public NewsAdapter(INewsItemClickedListener listener) {
        articleList = new ArrayList<>();
        this.listener = listener;
    }

    public void updateNewsAdapter(List<Article> lists) {
        articleList = lists;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);   // Henvsining til recycler item
        NewsViewHolder vh = new NewsViewHolder(v,listener);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
//        glide library - for every city there is an city, which is gonna return a URL
//        Glide.with(holder.imgIcon.getContext()).load(cityList.get(position).getImageResourceId()).into(holder.imgIcon);
//        holder.txtCity.setText(cityList.get(position).Name);
    }

    @Override
    public int getItemCount() {
        return articleList.size();
    }

    public class NewsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

//        //Widgets
//        ImageView imgIcon;
//        TextView txtCity;

        INewsItemClickedListener listener;

        public NewsViewHolder(@NonNull View itemView, INewsItemClickedListener newsItemClickedListener) {
            super(itemView);

            //references from layout file
//            imgIcon = itemView.findViewById(R.id.imgFlag);
//            txtCity = itemView.findViewById(R.id.txtCity);
            listener = newsItemClickedListener; //save listener interface

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onArticleClicked(getAdapterPosition());
        }
    }
}
