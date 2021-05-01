package dk.au.mad21spring.spacenewsapplication.ViewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import java.util.ArrayList;

import dk.au.mad21spring.spacenewsapplication.Database.Article;
import dk.au.mad21spring.spacenewsapplication.Database.Repository;

public class MainViewModel extends AndroidViewModel {

    private Repository repository;

    public MainViewModel(@NonNull Application application) {
        super(application);
        repository = Repository.getInstance(application);
    }

    public void deleteAllArticles() {repository.deleteAllArticles();}

    //public ArrayList<Article> getAllArticlesFromAPI() {return repository.getApiArticlesList();}
    //public void getOneArticleFromAPI(String url) {repository.sendRequestOneArticle(url);}
}
