package dk.au.mad21spring.spacenewsapplication.ViewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import java.util.ArrayList;

import dk.au.mad21spring.spacenewsapplication.Database.Article;
import dk.au.mad21spring.spacenewsapplication.Database.Repository;

public class ListViewModel extends AndroidViewModel {

    private Repository repository;

    public ListViewModel(@NonNull Application application) {
        super(application);
        repository = Repository.getInstance(application);
    }

    public void deleteAllArticles() {
        repository.deleteAllArticles();
    }
    public void updateNewsFeed() {
        repository.sendRequestAllArticles();
    }
    public ArrayList<Article> getArticles(String fragmentType) {
        return repository.getArticles(fragmentType);
    }
}
