package dk.au.mad21spring.spacenewsapplication.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import dk.au.mad21spring.spacenewsapplication.Database.Article;
import dk.au.mad21spring.spacenewsapplication.Database.Repository;
import dk.au.mad21spring.spacenewsapplication.Fragments.ArticleDetailsFragment;
import dk.au.mad21spring.spacenewsapplication.Fragments.ArticleListFragment;
import dk.au.mad21spring.spacenewsapplication.NewsAdapter;
import dk.au.mad21spring.spacenewsapplication.R;
import dk.au.mad21spring.spacenewsapplication.Services.ForegroundService;
import dk.au.mad21spring.spacenewsapplication.ViewModels.MainViewModel;

public class MainActivity extends AppCompatActivity implements ArticleSelectorInterface {

    // keeping track of phone mode and user mode
    public enum PhoneMode {PORTRAIT, LANDSCAPE}

    public enum UserMode {LIST_VIEW, DETAIL_VIEW, SAVED_VIEW}

    private PhoneMode phoneMode;
    private UserMode userMode;

    // tags so we can find our fragments later
    private static final String LIST_FRAG = "list_fragment";
    private static final String SAVED_LIST_FRAG = "saved_list_fragment";
    private static final String DETAILS_FRAG = "details_fragment";

    // fragments
    private ArticleListFragment articleListFragment;
    private ArticleDetailsFragment articleDetailsFragment;
    private ArticleListFragment articleSavedFragment; // ikke sikker på dette, men tror det er sådan vi kan genbruge vores fragment

    // containers we use to put our fragments in
    private LinearLayout listContainer;
    private LinearLayout detailsContainer;

    private Button btnAdd, btnDelete, btnAPI;
    private MainViewModel vm;
    private RecyclerView recyclerView;
    private NewsAdapter adapter;

    public ArrayList<Article> articles; // hvorfor er denne public?
    private int selectedArticlePosition;
    private ArrayList<Article> savedArticles;
    private Article testArticle = new Article("16v33fgrh3", "My article", "", "", "", "", "", "");
    private Article testArticle2 = new Article("y324724279", "My article2", "", "", "", "", "", "");

    ForegroundService foregroundService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multipane_main);

        // get container views
        listContainer = (LinearLayout) findViewById(R.id.list_container);
        detailsContainer = (LinearLayout) findViewById(R.id.details_container);

        foregroundService = new ForegroundService();
        vm = new ViewModelProvider(this).get(MainViewModel.class);
        //vm.getAllArticlesFromAPI();

        articles = new ArrayList<Article>();
        loadArticle(articles);
        savedArticles = new ArrayList<Article>();
        loadSavedArticle(savedArticles);

        // determine orientation
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            phoneMode = PhoneMode.PORTRAIT;
        } else {
            phoneMode = PhoneMode.LANDSCAPE;
        }

        // our saved state so we are gonna remember which movie we picked
        if (savedInstanceState == null) {

            // no persisted state, start the app in list view mode and selected index = 0
            selectedArticlePosition = 0;
            userMode = UserMode.LIST_VIEW;

            // initialize fragments
            articleListFragment = new ArticleListFragment();
            articleDetailsFragment = new ArticleDetailsFragment();
            articleSavedFragment = new ArticleListFragment();


            articleListFragment.setArticles(articles);
            articleDetailsFragment.setArticle(articles.get(selectedArticlePosition));
            articleSavedFragment.setArticles(savedArticles);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.list_container, articleSavedFragment, SAVED_LIST_FRAG)
                    .replace(R.id.list_container, articleListFragment, LIST_FRAG)
                    .add(R.id.details_container, articleDetailsFragment, DETAILS_FRAG)
                    .commit();
        } else {
            // got restarted with persisted state, probably due to orientation change
            selectedArticlePosition = savedInstanceState.getInt("article_position");
            userMode = (UserMode) savedInstanceState.getSerializable("user_mode");

            if (userMode == null) {
                userMode = UserMode.LIST_VIEW;  //default value if none saved
            }

            // check if FragmentManager already holds instance of Fragments, else create them
            articleListFragment = (ArticleListFragment) getSupportFragmentManager().findFragmentByTag(LIST_FRAG);
            if (articleListFragment == null) {
                articleListFragment = new ArticleListFragment();
            }
            articleDetailsFragment = (ArticleDetailsFragment) getSupportFragmentManager().findFragmentByTag(DETAILS_FRAG);
            if (articleDetailsFragment == null) {
                articleDetailsFragment = new ArticleDetailsFragment();
            }
            articleSavedFragment = (ArticleListFragment) getSupportFragmentManager().findFragmentByTag(SAVED_LIST_FRAG);
            if (articleSavedFragment == null) {
                articleSavedFragment = new ArticleListFragment();
            }
        }

        /*
        btnAdd = findViewById(R.id.button);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addArticleToReadLater();
            }
        });

        btnDelete = findViewById(R.id.button2);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteArticleFromReadLater();
            }
        });

        btnAPI = findViewById(R.id.button3);
        btnAPI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vm.getArticleFromAPI("https://test.spaceflightnewsapi.net/api/v2/articles/6051e86631c42cd69c01e29a");
            }
        });

         */
        articles.add(testArticle);
        articles.add(testArticle2);
        addArticleToReadLater(testArticle);
        addArticleToReadLater(testArticle2);

        updateFragmentViewState(userMode);
        //initializeRecyclerView();
        //adapter.updateNewsAdapter(articles);
        startForegroundService();
    }

    private void startForegroundService() {
        Intent foregroundServiceIntent = new Intent(this, ForegroundService.class);
        startService(foregroundServiceIntent);
    }

    private void stopForegroundService() {
        Intent foregroundServiceIntent = new Intent(this, ForegroundService.class);
        stopService(foregroundServiceIntent);
    }


    /*
    @Override
    public void onArticleClicked(int index) {

    }

     */

    // recyclerview og adapter er flyttet til ArticleListFragment
    /*
    private void initializeRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView);
        adapter = new NewsAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

     */

    public void addArticleToReadLater(Article article) {
        vm.addArticle(article);
    }

    public void deleteArticleFromReadLater(Article article) {
        vm.deleteArticle(article);
    }


    // Fragments...
    // denne metode er ikke helt rigtig endnu
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // hvad skal der ske når man trykker tilbage
        if (phoneMode == phoneMode.LANDSCAPE) {
            if (userMode == UserMode.SAVED_VIEW) {
                updateFragmentViewState(UserMode.LIST_VIEW);
            } else {
                finish();
            }
        } else {
            if (userMode == UserMode.LIST_VIEW) {
                finish();
            } else if (userMode == UserMode.SAVED_VIEW) {
                updateFragmentViewState(UserMode.LIST_VIEW);
            } else if (userMode == UserMode.DETAIL_VIEW) { // hvad hvis brugeren var i saved view lige før de var i detail view, så skal de tilbage dertil...
                updateFragmentViewState(UserMode.LIST_VIEW);
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt("article_position", selectedArticlePosition);
        outState.putSerializable("user_mode", userMode);
        super.onSaveInstanceState(outState);
    }

    // jeg ved ikke om vi skal finde en anden måde at gøre dette på?
    private void updateFragmentViewState(UserMode targetMode) {
        // update view
        if (targetMode == UserMode.DETAIL_VIEW) {
            userMode = UserMode.DETAIL_VIEW;
            switchFragment(targetMode);
        }
        if (targetMode == UserMode.LIST_VIEW) {
            userMode = UserMode.LIST_VIEW;
            switchFragment(targetMode);
        }
        if (targetMode == UserMode.SAVED_VIEW) {
            userMode = UserMode.SAVED_VIEW;
            switchFragment(targetMode);
        } else {
            //ignore
        }

    }

    // denne metode er ikke rigtig endnu
    private boolean switchFragment(UserMode targetMode) { // hvorfor returnerer den en bool?
        if (phoneMode == PhoneMode.PORTRAIT) {
            if (targetMode == UserMode.LIST_VIEW) {
                listContainer.setVisibility(View.VISIBLE);
                detailsContainer.setVisibility(View.GONE);
                changeListContainerFragment(targetMode);
            } else if (targetMode == UserMode.DETAIL_VIEW) {
                listContainer.setVisibility(View.GONE);
                detailsContainer.setVisibility(View.VISIBLE);
                //changeListContainerFragment(targetMode);
            } else if (targetMode == UserMode.SAVED_VIEW) {
                listContainer.setVisibility(View.VISIBLE);
                detailsContainer.setVisibility(View.GONE);
                changeListContainerFragment(targetMode);
            }
        } else {
            // har ikke kigget på dette endnu
            /*
            if(targetMode == UserMode.LIST_VIEW){
                changeListContainerFragment(UserMode.DETAIL_VIEW);
            } else {
                changeListContainerFragment(targetMode);
            }

             */
        }
        return true;
    }

    private void changeListContainerFragment(UserMode targetMode) {
        switch (targetMode) {
            case LIST_VIEW:
                getSupportFragmentManager().beginTransaction()
                        //.setCustomAnimations(R.animator.slide_in, R.animator.slide_out) // skal vi have animationer?
                        .replace(R.id.list_container, articleListFragment, LIST_FRAG)
                        .commit();
                break;

            case SAVED_VIEW:
                getSupportFragmentManager().beginTransaction()
                        //.setCustomAnimations(R.animator.slide_in, R.animator.slide_out)
                        .addToBackStack(null)
                        .replace(R.id.list_container, articleSavedFragment, SAVED_LIST_FRAG)
                        .commit();
                break;
        }
    }

    @Override
    public void onArticleSelected(int position) {
        if (articleDetailsFragment != null) {
            Article selectedArticle = articles.get(position);

            if (selectedArticle != null) {
                selectedArticlePosition = position;
                articleDetailsFragment.setArticle(selectedArticle);
            }
        }
        updateFragmentViewState(UserMode.DETAIL_VIEW);
    }

    @Override
    public ArrayList<Article> getArticleList() {
        return articles; // skal også returnere savedArticles afhængig af konteksten
    }

    @Override
    public Article getCurrentSelection() {
        if (articles != null) {
            return articles.get(selectedArticlePosition);
        } else {
            return null;
        }
    }

    @Override
    public void viewSaved() {
        if (userMode != UserMode.SAVED_VIEW) { // checks if the user mode is not already the saved mode
            updateFragmentViewState(UserMode.SAVED_VIEW);
        } else {
            updateFragmentViewState(UserMode.LIST_VIEW);
        }
    }

    // dette er blot til test
    private void loadArticle(List<Article> articles) {
        for (int i = 0; i < 20; i++) {
            articles.add(new Article("16v33fgrh3", "My article" + i, "www", "www", "news site", "summary ", "01-01-21", "02-01-21"));
        }
    }

    // dette er blot til test
    private void loadSavedArticle(List<Article> articles) {
        for (int i = 0; i < 3; i++) {
            savedArticles.add(new Article("2131dsf", "Saved article" + i, "www", "www", "news site", "summary ", "01-01-21", "02-01-21"));

        }
    }
    @Override
    protected void onDestroy() {
        stopForegroundService();
        super.onDestroy();
    }
}