package dk.au.mad21spring.spacenewsapplication.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import dk.au.mad21spring.spacenewsapplication.R;

public class ArticleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_article);

        // Ved ikke om vi har brug for denne activity når vi bruger fragments...
    }
}