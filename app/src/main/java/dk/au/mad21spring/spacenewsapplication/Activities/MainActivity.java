package dk.au.mad21spring.spacenewsapplication.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;

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
    private UserMode previousUserMode;

    // tags so we can find our fragments later
    private static final String LIST_FRAG = "list_fragment";
    private static final String SAVED_LIST_FRAG = "saved_list_fragment";
    private static final String DETAILS_FRAG = "details_fragment";

    // tags to savedStateInstance
    private static final String ARTICLE_POSITION = "article_position";
    private static final String USER_MODE = "user_mode";
    private static final String PREVIOUS_USER_MODE = "previous_user_mode";

    // fragments
    private ArticleListFragment articleListFragment;
    private ArticleDetailsFragment articleDetailsFragment;
    private ArticleListFragment articleSavedFragment;

    // containers to put our fragments in
    private LinearLayout listContainer;
    private LinearLayout detailsContainer;

    //bottomNavigationBar
    private BottomNavigationView bottomNav;

    private MainViewModel vm;

    private ArrayList<Article> articles;
    private ArrayList<Article> savedArticles;

    private int selectedArticlePosition;

    ForegroundService foregroundService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multipane_main);

        // get container views
        listContainer = findViewById(R.id.list_container);
        detailsContainer = findViewById(R.id.details_container);

        // bottomNavigationView
        bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        foregroundService = new ForegroundService();
        vm = new ViewModelProvider(this).get(MainViewModel.class);

        //vm.deleteAllArticles();
        articles = new ArrayList<Article>();
        savedArticles = new ArrayList<Article>();

        loadArticles();
        loadSavedArticle();

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
            previousUserMode = UserMode.LIST_VIEW;

            // initialize fragments
            articleListFragment = new ArticleListFragment();
            articleDetailsFragment = new ArticleDetailsFragment();
            articleSavedFragment = new ArticleListFragment();

            // set articles in fragments
            articleListFragment.setArticles(articles);
            if (articles.size() != 0){
                articleDetailsFragment.setArticle(articles.get(selectedArticlePosition));
            }
            articleSavedFragment.setArticles(savedArticles);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.details_container, articleDetailsFragment, DETAILS_FRAG)
                    .add(R.id.list_container, articleSavedFragment, SAVED_LIST_FRAG)
                    .replace(R.id.list_container, articleListFragment, LIST_FRAG)
                    .commit();
        } else {
            // got restarted with persisted state, probably due to orientation change
            selectedArticlePosition = savedInstanceState.getInt(ARTICLE_POSITION);
            userMode = (UserMode) savedInstanceState.getSerializable(USER_MODE);
            previousUserMode = (UserMode) savedInstanceState.getSerializable(PREVIOUS_USER_MODE);

            if (userMode == null) {
                userMode = UserMode.LIST_VIEW;  //default value if none saved
            }
            if (previousUserMode == null) {
                previousUserMode = UserMode.LIST_VIEW;
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

        updateFragmentViewState(userMode);
        startForegroundService();
    }

    // method to Bottom navigation bar
    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()){
                        case R.id.nav_home:
                            updateFragmentViewState(UserMode.LIST_VIEW);
                            break;
                        case R.id.nav_saved:
                            updateFragmentViewState(UserMode.SAVED_VIEW);
                            break;

                    }
                    return true; //wants to select the clicked item
                }
            };

    private void startForegroundService() {
        Intent foregroundServiceIntent = new Intent(this, ForegroundService.class);
        startService(foregroundServiceIntent);
    }

    private void stopForegroundService() {
        Intent foregroundServiceIntent = new Intent(this, ForegroundService.class);
        stopService(foregroundServiceIntent);
    }

    // Fragments...
    // denne metode er ikke helt rigtig endnu
    @Override
    public void onBackPressed() {

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
                if (previousUserMode == UserMode.LIST_VIEW) {
                    updateFragmentViewState(UserMode.LIST_VIEW);
                } else if (previousUserMode == UserMode.SAVED_VIEW) {
                    updateFragmentViewState(UserMode.SAVED_VIEW);
                }
            }
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(ARTICLE_POSITION, selectedArticlePosition);
        outState.putSerializable(USER_MODE, userMode);
        outState.putSerializable(PREVIOUS_USER_MODE, previousUserMode);
        super.onSaveInstanceState(outState);
    }

    private void updateFragmentViewState(UserMode targetMode) {
        previousUserMode = userMode;
        userMode = targetMode;
        switchFragment(targetMode);
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

    private void loadArticles() {
        vm.updateNewsFeed();
        articles = vm.getAllArticlesFromAPI();
    }

    private void loadSavedArticle() {
        savedArticles = vm.getAllSavedArticles();
    }

    @Override
    protected void onDestroy() {
        stopForegroundService();
        super.onDestroy();
    }
}