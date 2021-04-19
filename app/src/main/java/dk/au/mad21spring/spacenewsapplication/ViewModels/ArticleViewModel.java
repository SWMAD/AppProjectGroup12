package dk.au.mad21spring.spacenewsapplication.ViewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import dk.au.mad21spring.spacenewsapplication.Database.Repository;

public class ArticleViewModel extends AndroidViewModel {

    private Repository repository;

    public ArticleViewModel(@NonNull Application application) {
        super(application);
        repository = Repository.getInstance(application);
    }

}
