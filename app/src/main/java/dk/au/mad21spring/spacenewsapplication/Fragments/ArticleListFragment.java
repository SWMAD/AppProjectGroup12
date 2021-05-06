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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import dk.au.mad21spring.spacenewsapplication.Activities.ArticleSelectorInterface;
import dk.au.mad21spring.spacenewsapplication.Database.Article;
import dk.au.mad21spring.spacenewsapplication.NewsAdapter;
import dk.au.mad21spring.spacenewsapplication.R;
import dk.au.mad21spring.spacenewsapplication.ViewModels.ListViewModel;

public class ArticleListFragment extends Fragment implements NewsAdapter.INewsItemClickedListener {

    private RecyclerView recyclerView;
    private NewsAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private Button btnSaved;
    private ListViewModel vm;
    private String fragmentType;

    private ArrayList<Article> articles; // her skal vi nok have noget live data i stedet

    private ArticleSelectorInterface articleSelector;

    public ArticleListFragment(String fragmentType) {
        this.fragmentType = fragmentType;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_article_list, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        vm = new ViewModelProvider(this).get(ListViewModel.class);
        vm.updateNewsFeed(); // vi forstår ikke hvorfor både denne metode og getArticles skal kaldes
        articles = vm.getArticles(fragmentType);

        adapter = new NewsAdapter(this, getActivity(), articles);

        if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            layoutManager = new GridLayoutManager(getContext(), 2);
        } else {
            layoutManager = new GridLayoutManager(getContext(), 1);
        }

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
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

    @Override
    public void onArticleClicked(int index) {
        if (articleSelector != null){
            Article selectedArticle = articles.get(index);
            articleSelector.onArticleSelected(selectedArticle);
        }
    }
}
