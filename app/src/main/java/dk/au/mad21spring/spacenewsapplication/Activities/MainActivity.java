package dk.au.mad21spring.spacenewsapplication.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

import dk.au.mad21spring.spacenewsapplication.Database.Article;
import dk.au.mad21spring.spacenewsapplication.Database.Repository;
import dk.au.mad21spring.spacenewsapplication.NewsAdapter;
import dk.au.mad21spring.spacenewsapplication.R;
import dk.au.mad21spring.spacenewsapplication.Services.ForegroundService;
import dk.au.mad21spring.spacenewsapplication.ViewModels.MainViewModel;

public class MainActivity extends AppCompatActivity implements NewsAdapter.INewsItemClickedListener {

    private MainViewModel vm;
    private RecyclerView recyclerView;
    private NewsAdapter adapter;

    public ArrayList<Article> articles;
    private Article testArticle = new Article("16v33fgrh3", "My article", "", "", "", "", "", "");
    private Article testArticle2 = new Article("y324724279", "My article2", "", "", "", "", "", "");

    ForegroundService foregroundService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        foregroundService = new ForegroundService();
        vm = new ViewModelProvider(this).get(MainViewModel.class);
        //vm.getAllArticlesFromAPI();

        articles = new ArrayList<Article>();
        articles.add(testArticle);
        articles.add(testArticle2);
        addArticleToReadLater(testArticle);
        addArticleToReadLater(testArticle2);

        initializeRecyclerView();
        adapter.updateNewsAdapter(articles);
        startForegroundService();
    }

    private void startForegroundService() {
        Intent foregroundServiceIntent = new Intent(this, ForegroundService.class);
        startService(foregroundServiceIntent);
    }

    private void stopForegroundService() {
        Intent foregroundServiceIntent = new Intent(this, ForegroundService.class);
        stopService(foregroundServiceIntent);
    }

    @Override
    public void onArticleClicked(int index) {

    }

    private void initializeRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView);
        adapter = new NewsAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    public void addArticleToReadLater(Article article) {
        vm.addArticle(article);
    }
    public void deleteArticleFromReadLater(Article article) {
        vm.deleteArticle(article);
    }

    @Override
    protected void onDestroy() {
        stopForegroundService();
        super.onDestroy();
    }
}