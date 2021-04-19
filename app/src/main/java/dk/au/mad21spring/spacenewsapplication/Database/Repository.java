package dk.au.mad21spring.spacenewsapplication.Database;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import dk.au.mad21spring.spacenewsapplication.Article;

public class Repository {

    public static Repository repository;
    private NewsDatabase db;
    private ExecutorService executor;
    private LiveData<List<Article>> readLaterList;

    public static Repository getInstance(Application app) {
        if (repository == null) {
            repository = new Repository(app);
        }
        return repository;
    }

    private Repository(Application app) {
        db = NewsDatabase.getDatabase(app.getApplicationContext());
        executor = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<Article>> getReadLaterList() {return readLaterList;}

    // #### Asynch methods #####################################################

    // Add new article to database
    public void addArticleAsynch(Article aticle){
        executor.execute(new Runnable() {
            @Override
            public void run() {
                db.newsDAO().addArticle(aticle);
            }
        });
    }

    // Delete article in database
    public void deleteArticleAsynch(Article article){
        executor.execute(new Runnable() {
            @Override
            public void run() {
                db.newsDAO().deleteArticle(article.id);
            }
        });
    }






}

