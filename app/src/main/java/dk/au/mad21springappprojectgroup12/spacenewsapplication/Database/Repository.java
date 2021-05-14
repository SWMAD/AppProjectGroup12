package dk.au.mad21springappprojectgroup12.spacenewsapplication.Database;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import dk.au.mad21springappprojectgroup12.spacenewsapplication.Constants;

public class Repository {

    // Code regarding database inspired by code demo from class: "roomdemo 1.1"
    // Code regarding api inspired by code demo from class: "RickandMortyGallery 1.8"

    private static final String TAG = "TAG";
    public static Repository repository;
    private NewsDatabase db;
    private ExecutorService executor;
    private RequestQueue queue;
    private Application app;
    private int index = 0;

    private LiveData<List<Article>> readLaterList;
    private MutableLiveData<List<Article>> apiArticlesList;

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
        readLaterList = db.newsDAO().getAllReadLaterArticles();
        apiArticlesList = new MutableLiveData<List<Article>>() {};

        if (apiArticlesList == null){
            workaround();
            sendRequestAllArticles();
        }
    }

    public LiveData<List<Article>> getReadLaterList() {return readLaterList;}
    public LiveData<List<Article>> getApiArticlesList() {return apiArticlesList;}

    private void workaround(){
        ArrayList<Article> tempList = new ArrayList<>();
        tempList.add(new Article("608cee3997ebc0001c7d2d26", "Chinese Long March 6 rocket delivers nine small satellites to space", "https://spaceflightnow.com/2021/04/30/chinese-long-march-6-rocket-delivers-nine-small-satellites-to-space/", "https://mk0spaceflightnoa02a.kinstacdn.com/wp-content/uploads/2021/05/lm6_cluster.jpg", "Spaceflight Now", "Nine small Chinese satellites, including a technology experiment to test out ways to capture space debris, rode a Long March 6 rocket into orbit April 27 on a rideshare mission managed by China Great Wall Industry Corp., the government-owned enterprise charged with selling Chinese launch services on the commercial market.", "2021-04-30T05:59:21.000Z", "2021-05-01T05:59:21.540Z"));
        apiArticlesList.setValue(tempList);
    }

    // return either all articles or saved articles depending on which fragment needs it
    public LiveData<List<Article>> getArticles(String fragmentType) {
        if (fragmentType.equals(Constants.LIST_FRAG)) {
            return getApiArticlesList();
        } else {
            return getReadLaterList();
        }
    }

    // return article based on which list it should be taken from and the index
    public Article getArticle(String fragmentType, int index) {

        workaround();
        sendRequestAllArticles();

        if (fragmentType.equals(Constants.LIST_FRAG)) {
            return apiArticlesList.getValue().get(index);
        } else {
            return readLaterList.getValue().get(index);
        }
    }

    public Article getReadLaterArticle(){

        if (readLaterList.getValue() != null){
            int maxIndex = readLaterList.getValue().size();

            if(maxIndex != 0){
                Article article = readLaterList.getValue().get(index);

                if (index == maxIndex-1){
                    index = 0;
                }
                else{
                    index++;
                }

                return article;
            }
            else{
                return null;
            }
        }
        else {
            return null;
        }
    }

    // ########################## API methods ##########################

    public void sendRequestAllArticles(){
        String url = "https://spaceflightnewsapi.net/api/v2/articles?_limit=100";

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

    private void parseJsonAllArticles(String json){

        ArrayList<Article> tempList = new ArrayList<Article>();

        if (apiArticlesList != null) {
            apiArticlesList.getValue().clear();
        }

        try {
            JSONArray jsonArray = new JSONArray(json);
            for(int i = 0 ; i < jsonArray.length() ; i++){
                String articleID = jsonArray.getJSONObject(i).getString("id");
                String title = jsonArray.getJSONObject(i).getString("title");
                String url = jsonArray.getJSONObject(i).getString("url");
                String imageUrl = jsonArray.getJSONObject(i).getString("imageUrl");
                String newsSite = jsonArray.getJSONObject(i).getString("newsSite");
                String summary = jsonArray.getJSONObject(i).getString("summary");
                String publishedAt = jsonArray.getJSONObject(i).getString("publishedAt");
                String updatededAt = jsonArray.getJSONObject(i).getString("updatedAt");

                Article articleFromAPI =  new Article(articleID, title, url, imageUrl, newsSite, summary, publishedAt, updatededAt);
                Log.d(TAG, "parseJson: Title: " + articleFromAPI.Title + ", news site: " + articleFromAPI.NewsSite);
                tempList.add(articleFromAPI);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        apiArticlesList.setValue(tempList);
        tempList.clear();
    }

    // ########################## Asynch methods ##########################

    // Add new article to database
    public void addArticleAsynch(Article article){
        executor.execute(new Runnable() {
            @Override
            public void run() {
                db.newsDAO().addArticle(article);
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

    // Delete all cities in database
    public void deleteAllArticles(){
        executor.execute(new Runnable() {
            @Override
            public void run() {
                db.newsDAO().deleteAll();
            }
        });
    }

    // Check if article is already saved for 'read later'
    public boolean articleExists(Article article){

        if (db.newsDAO().isArticleSaved(article.ArticleID) == null){
            return false;
        }
        else {
            return true;
        }
    }
}

