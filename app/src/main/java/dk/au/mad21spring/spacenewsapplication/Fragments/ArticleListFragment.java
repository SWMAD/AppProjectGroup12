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
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import dk.au.mad21spring.spacenewsapplication.Activities.ArticleSelectorInterface;
import dk.au.mad21spring.spacenewsapplication.Database.Article;
import dk.au.mad21spring.spacenewsapplication.NewsAdapter;
import dk.au.mad21spring.spacenewsapplication.R;
import dk.au.mad21spring.spacenewsapplication.ViewModels.ListViewModel;

public class ArticleListFragment extends Fragment implements NewsAdapter.INewsItemClickedListener {

    // constant
    private static final String FRAGMENT_TYPE = "fragmentType";

    // recycler view
    private RecyclerView recyclerView;
    private NewsAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    // view model
    private ListViewModel vm;

    // determines if the fragment is for the view with all articles or only saved articles
    private String fragmentType;

    private ArrayList<Article> articles;
    private ArticleSelectorInterface articleSelector;

    // required empty public constructor
    public ArticleListFragment() {

    }

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

        articles = new ArrayList<>();

        if (savedInstanceState != null) {
            fragmentType = savedInstanceState.getString(FRAGMENT_TYPE);
        }

        vm = new ViewModelProvider(this).get(ListViewModel.class);
        vm.getArticles(fragmentType).observe(getViewLifecycleOwner(), new Observer<List<Article>>() {
            @Override
            public void onChanged(List<Article> list) {
                ArrayList<Article> tempList = new ArrayList<>();
                for (Article item : list){
                    tempList.add(item);
                }
                adapter.updateNewsAdapter(tempList);
                articles = tempList;
            }
        });

        vm.updateNewsFeed();
        adapter = new NewsAdapter(this, getActivity());

        // set grid column span
        layoutManager = new GridLayoutManager(getContext(), 2);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString(FRAGMENT_TYPE, fragmentType);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onAttach(@NonNull Context activity) {
        super.onAttach(activity);

        // making sure that the proper interface is implemented
        try {
            articleSelector = (ArticleSelectorInterface) activity;
        } catch (ClassCastException ex) {
            throw new ClassCastException(activity.toString() + " must implement ArticleSelectorInterface");
        }
    }

    // method for passing the chosen article to the interface
    @Override
    public void onArticleClicked(int index) {
        if (articleSelector != null){
            Article selectedArticle = articles.get(index);
            articleSelector.onArticleSelected(selectedArticle);
        }
    }
}
