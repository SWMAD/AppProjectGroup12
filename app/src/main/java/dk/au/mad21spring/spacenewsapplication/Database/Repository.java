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

    private static final String TAG = "TAG";
    public static Repository repository;
    private NewsDatabase db;
    private ExecutorService executor;
    private RequestQueue queue;
    private Application app;

    //private LiveData<List<Article>> readLaterList;

    public static Repository getInstance(Application app) {
        if (repository == null) {
            repository = new Repository(app);
        }
        return repository;
    }

    private Repository(Application app) {
        db = NewsDatabase.getDatabase(app.getApplicationContext());
        executor = Executors.newSingleThreadExecutor();
        this.app = app;
    }

    //public LiveData<List<Article>> getReadLaterList() {return readLaterList;}

    // ########################## API methods ##########################

    public void sendRequest(String url){
        if(queue==null){
            queue = Volley.newRequestQueue(app.getApplicationContext());
        }

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "API accessed successfully! " + response);
                        parseJson(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error occured while trying to access API!", error);
            }
        });

        queue.add(stringRequest);
    }

    private void parseJson(String json){
        Gson gson = new GsonBuilder().create();
        Article articleFromAPI =  gson.fromJson(json, Article.class);

        if(articleFromAPI!=null){
            Log.d(TAG, "parseJson: Article ID: " + articleFromAPI.ArticleID + ", title: " + articleFromAPI.Title);
            // Do something.... Add to database?
        }
    }

    // ########################## Asynch methods ##########################

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
                db.newsDAO().deleteArticle(article.ArticleID);
            }
        });
    }






}

