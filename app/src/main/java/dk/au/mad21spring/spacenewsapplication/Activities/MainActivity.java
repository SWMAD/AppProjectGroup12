package dk.au.mad21spring.spacenewsapplication.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

import dk.au.mad21spring.spacenewsapplication.Article;
import dk.au.mad21spring.spacenewsapplication.NewsAdapter;
import dk.au.mad21spring.spacenewsapplication.R;
import dk.au.mad21spring.spacenewsapplication.ViewModels.MainViewModel;

public class MainActivity extends AppCompatActivity implements NewsAdapter.INewsItemClickedListener {

    private Button btnAdd, btnDelete, btnAPI;
    private MainViewModel vm;
    private RecyclerView recyclerView;
    private NewsAdapter adapter;

    public ArrayList<Article> articles;
    private Article testArticle = new Article("16v33fgrh3", "My article", "", "", "", "");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        vm = new ViewModelProvider(this).get(MainViewModel.class);
        articles = new ArrayList<Article>();
        articles.add(testArticle);
        articles.add(testArticle);

        btnAdd = findViewById(R.id.button);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addArticleToReadLater();
            }
        });

        btnDelete = findViewById(R.id.button2);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteArticleFromReadLater();
            }
        });

        btnAPI = findViewById(R.id.button3);
        btnAPI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vm.getArticleFromAPI("https://test.spaceflightnewsapi.net/api/v2/articles/6051e86631c42cd69c01e29a");
            }
        });

        initializeRecyclerView();
        adapter.updateNewsAdapter(articles);
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

    public void addArticleToReadLater() {
        vm.addArticle(testArticle);
    }

    public void deleteArticleFromReadLater() {
        vm.deleteArticle(testArticle);
    }


}