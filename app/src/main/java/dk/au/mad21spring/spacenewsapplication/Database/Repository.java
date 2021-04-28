package dk.au.mad21spring.spacenewsapplication.Database;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Repository {

    private static final String TAG = "TAG";
    public static Repository repository;
    private NewsDatabase db;
    private ExecutorService executor;
    private RequestQueue queue;
    private Application app;
    private int index = 0;

    //private LiveData<List<Article>> readLaterList;
    private List<Article> readLaterList;

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
        //readLaterList = db.newsDAO().getAllReadLaterArticles();
        readLaterList = db.newsDAO().getAllReadLaterArticlesNonAsync();
    }

    //public LiveData<List<Article>> getReadLaterList() {return readLaterList;}
    public List<Article> getReadLaterList() {return readLaterList;}

    public Article getReadLaterArticle(){

        int maxIndex = readLaterList.size();
        Article article = readLaterList.get(index);

        if (index == maxIndex-1){
            index = 0;
        }
        else{
            index++;
        }

        return article;
    }

    // ########################## API methods ##########################

    public void sendRequestOneArticle(String url){
        if(queue==null){
            queue = Volley.newRequestQueue(app.getApplicationContext());
        }

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "API accessed successfully! " + response);
                        parseJsonOneArticle(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error occured while trying to access API!", error);
            }
        });

        queue.add(stringRequest);
    }

    public void sendRequestAllArticles(){
        String url = "https://spaceflightnewsapi.net/api/v2/articles";

        if(queue==null){
            queue = Volley.newRequestQueue(app.getApplicationContext());
        }

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "API accessed successfully! " + response);
                        parseJsonAllArticles(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error occured while trying to access API!", error);
            }
        });

        queue.add(stringRequest);
    }

    private void parseJsonOneArticle(String json){
        Gson gson = new GsonBuilder().create();
        ArticleDTO articleFromAPI =  gson.fromJson(json, ArticleDTO.class);

        if(articleFromAPI!=null){
            Log.d(TAG, "parseJson: Title: " + articleFromAPI.getTitle() + ", news site: " + articleFromAPI.getNewsSite());

            addArticleAsynch(new Article("", articleFromAPI.getTitle(), articleFromAPI.getUrl(), articleFromAPI.getImageUrl(), articleFromAPI.getNewsSite(),articleFromAPI.getSummary(), articleFromAPI.getPublishedAt(), articleFromAPI.getUpdatedAt()));
        }
    }

    private void parseJsonAllArticles(String json){
//        Gson gson = new GsonBuilder().create();
//        ArticleDTO articleFromAPI =  gson.fromJson(json, ArticleDTO.class);
//
//        if(articleFromAPI!=null){
//            Log.d(TAG, "parseJson: Title: " + articleFromAPI.getTitle() + ", news site: " + articleFromAPI.getNewsSite());
//
//            addArticleAsynch(new Article("", articleFromAPI.getTitle(), articleFromAPI.getUrl(), articleFromAPI.getImageUrl(), articleFromAPI.getNewsSite(),articleFromAPI.getSummary(), articleFromAPI.getPublishedAt(), articleFromAPI.getUpdatedAt()));
//        }
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

