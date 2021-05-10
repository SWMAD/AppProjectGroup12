package dk.au.mad21spring.spacenewsapplication.ViewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import dk.au.mad21spring.spacenewsapplication.Database.Article;
import dk.au.mad21spring.spacenewsapplication.Database.Repository;

public class DetailsViewModel extends AndroidViewModel {

    private Repository repository;

    public DetailsViewModel(@NonNull Application application) {
        super(application);
        repository = Repository.getInstance(application);
    }

    public boolean isArticleSaved(Article article) {return repository.articleExists(article);}
    public void addArticleToReadLater(Article article) {repository.addArticleAsynch(article);}
    public void deleteArticle(Article article) {repository.deleteArticleAsynch(article);}
    public Article getArticles(String fragmentType, int index) {
        return repository.getArticles(fragmentType, index);
    };
}
