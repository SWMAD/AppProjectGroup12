package dk.au.mad21spring.spacenewsapplication.ViewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import java.util.ArrayList;

import dk.au.mad21spring.spacenewsapplication.Database.Article;
import dk.au.mad21spring.spacenewsapplication.Database.Repository;

public class ReadLaterViewModel extends AndroidViewModel {

    private Repository repository;

    public ReadLaterViewModel(@NonNull Application application) {
        super(application);
        repository = Repository.getInstance(application);
    }
}
