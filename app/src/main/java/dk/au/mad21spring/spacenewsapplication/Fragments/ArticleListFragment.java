package dk.au.mad21spring.spacenewsapplication.Fragments;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import dk.au.mad21spring.spacenewsapplication.Activities.ArticleSelectorInterface;
import dk.au.mad21spring.spacenewsapplication.Database.Article;
import dk.au.mad21spring.spacenewsapplication.NewsAdapter;
import dk.au.mad21spring.spacenewsapplication.R;
import dk.au.mad21spring.spacenewsapplication.ViewModels.ArticleViewModel;
import dk.au.mad21spring.spacenewsapplication.ViewModels.MainViewModel;
import dk.au.mad21spring.spacenewsapplication.ViewModels.ReadLaterViewModel;

public class ArticleListFragment extends Fragment implements NewsAdapter.INewsItemClickedListener {

    private RecyclerView recyclerView;
    private NewsAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private Button btnSaved;
    private ReadLaterViewModel vm;

    private ArrayList<Article> articles; // her skal vi nok have noget live data i stedet

    private ArticleSelectorInterface articleSelector;

    public ArticleListFragment() {
        // required empty public constructor??
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_article_list, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        adapter = new NewsAdapter(this, getActivity(), articles);

        if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            layoutManager = new GridLayoutManager(getContext(), 2);
        } else {
            layoutManager = new GridLayoutManager(getContext(), 1);
        }

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);

        // view model...
    }

    @Override
    public void onAttach(@NonNull Context activity) {
        super.onAttach(activity);

        try {
            articleSelector = (ArticleSelectorInterface) activity;
        } catch (ClassCastException ex) {
            throw new ClassCastException(activity.toString() + " must implement ArticleSelectorInterface");
        }
    }

    public void setArticles(ArrayList<Article> articles){
        this.articles = articles;
    }

    @Override
    public void onArticleClicked(int index) {
        // opdater view model...

        if (articleSelector != null){
            articleSelector.onArticleSelected(index);
        }
    }
}
