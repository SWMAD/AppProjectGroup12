package dk.au.mad21spring.spacenewsapplication.ViewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import dk.au.mad21spring.spacenewsapplication.Database.Article;
import dk.au.mad21spring.spacenewsapplication.Database.Repository;

public class MainViewModel extends AndroidViewModel {

    private Repository repository;

    public MainViewModel(@NonNull Application application) {
        super(application);
        repository = Repository.getInstance(application);
    }

    public void addArticle(Article article) {repository.addArticleAsynch(article);}
    public void deleteArticle(Article article) {repository.deleteArticleAsynch(article);}
    public void getArticleFromAPI(String url) {repository.sendRequest(url);}
}
