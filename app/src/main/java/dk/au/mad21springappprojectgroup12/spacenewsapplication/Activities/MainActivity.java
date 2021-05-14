package dk.au.mad21springappprojectgroup12.spacenewsapplication.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Objects;

import dk.au.mad21springappprojectgroup12.spacenewsapplication.Constants;
import dk.au.mad21springappprojectgroup12.spacenewsapplication.Database.Article;
import dk.au.mad21springappprojectgroup12.spacenewsapplication.Fragments.ArticleDetailsFragment;
import dk.au.mad21springappprojectgroup12.spacenewsapplication.Fragments.ArticleListFragment;
import dk.au.mad21springappprojectgroup12.spacenewsapplication.R;
import dk.au.mad21springappprojectgroup12.spacenewsapplication.Services.ForegroundService;

// Code regarding fragments inspired by code demo from lecture 7
public class MainActivity extends AppCompatActivity implements ArticleSelectorInterface {

    // keeping track of phone mode, user mode and selected article
    private enum PhoneMode {PORTRAIT, LANDSCAPE}
    private enum UserMode {LIST_VIEW, DETAIL_VIEW, SAVED_VIEW}
    private PhoneMode phoneMode;
    private UserMode userMode;
    private UserMode previousUserMode;

    // tags for the savedStateInstance
    private static final String USER_MODE = "user_mode";
    private static final String PREVIOUS_USER_MODE = "previous_user_mode";

    // fragments
    private ArticleListFragment articleListFragment;
    private ArticleDetailsFragment articleDetailsFragment;
    private ArticleListFragment articleSavedFragment;

    // containers to put our fragments in
    private LinearLayout listContainer;
    private LinearLayout detailsContainer;

    // bottom navigation bar
    private BottomNavigationView bottomNav;

    // foreground service
    ForegroundService foregroundService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // hide title bar. Inspired by https://www.javatpoint.com/android-hide-title-bar-example
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_multipane_main);

        // get container views
        listContainer = findViewById(R.id.list_container);
        detailsContainer = findViewById(R.id.details_container);

        // bottomNavigationView
        bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        foregroundService = new ForegroundService();

        // determine orientation
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            phoneMode = PhoneMode.PORTRAIT;
        } else {
            phoneMode = PhoneMode.LANDSCAPE;
        }

        if (savedInstanceState == null) {

            // no persisted state, start the app in list view mode
            userMode = UserMode.LIST_VIEW;
            previousUserMode = UserMode.LIST_VIEW;

            // initialize fragments
            articleListFragment = new ArticleListFragment(Constants.LIST_FRAG);
            articleDetailsFragment = new ArticleDetailsFragment();
            articleSavedFragment = new ArticleListFragment(Constants.SAVED_LIST_FRAG);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.details_container, articleDetailsFragment, Constants.DETAILS_FRAG)
                    .add(R.id.list_container, articleSavedFragment, Constants.SAVED_LIST_FRAG)
                    .replace(R.id.list_container, articleListFragment, Constants.LIST_FRAG)
                    .commit();
        } else {
            // if app is e.g. rotated, restart with persisted state
            userMode = (UserMode) savedInstanceState.getSerializable(USER_MODE);
            previousUserMode = (UserMode) savedInstanceState.getSerializable(PREVIOUS_USER_MODE);

            if (userMode == null) {
                userMode = UserMode.LIST_VIEW;  //default value if none saved
            }
            if (previousUserMode == null) {
                previousUserMode = UserMode.LIST_VIEW; //default value if none saved
            }

            // check if FragmentManager already holds instance of Fragments, else create them
            articleListFragment = (ArticleListFragment) getSupportFragmentManager().findFragmentByTag(Constants.LIST_FRAG);
            if (articleListFragment == null) {
                articleListFragment = new ArticleListFragment(Constants.LIST_FRAG);
            }
            articleDetailsFragment = (ArticleDetailsFragment) getSupportFragmentManager().findFragmentByTag(Constants.DETAILS_FRAG);
            if (articleDetailsFragment == null) {
                articleDetailsFragment = new ArticleDetailsFragment();
            }
            articleSavedFragment = (ArticleListFragment) getSupportFragmentManager().findFragmentByTag(Constants.SAVED_LIST_FRAG);
            if (articleSavedFragment == null) {
                articleSavedFragment = new ArticleListFragment(Constants.SAVED_LIST_FRAG);
            }
        }

        updateFragmentViewState(userMode);
        startForegroundService();
    }

    // method to Bottom navigation bar
    // inspired by: https://www.section.io/engineering-education/bottom-navigation-bar-in-android/
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
                    return true;
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

    // when the user presses the back button, the app view should either be updated or the app
    // should be shut down
    @Override
    public void onBackPressed() {
        if (phoneMode == phoneMode.PORTRAIT) {
            if (userMode == UserMode.LIST_VIEW) {
                finish();
            } else if (userMode == UserMode.SAVED_VIEW) {
                updateFragmentViewState(UserMode.LIST_VIEW);
            } else if (userMode == UserMode.DETAIL_VIEW) {
                if (previousUserMode == UserMode.LIST_VIEW) {
                    updateFragmentViewState(UserMode.LIST_VIEW);
                } else if (previousUserMode == UserMode.SAVED_VIEW) {
                    updateFragmentViewState(UserMode.SAVED_VIEW);
                }
            }
        } else {
            if (userMode == UserMode.SAVED_VIEW) {
                updateFragmentViewState(UserMode.LIST_VIEW);
            } else {
                finish();
            }
        }
    }

    // update the user mode
    private void updateFragmentViewState(UserMode targetMode) {
        previousUserMode = userMode;
        userMode = targetMode;
        switchFragment(targetMode);
    }

    // switch which fragment is visible
    private void switchFragment(UserMode targetMode) {
        if (phoneMode == PhoneMode.PORTRAIT) {
            if (targetMode == UserMode.LIST_VIEW || targetMode == UserMode.SAVED_VIEW) {
                listContainer.setVisibility(View.VISIBLE);
                detailsContainer.setVisibility(View.GONE);
                changeListContainerFragment(targetMode);
            } else if (targetMode == UserMode.DETAIL_VIEW) {
                listContainer.setVisibility(View.GONE);
                detailsContainer.setVisibility(View.VISIBLE);
            }
        } else {
            if(targetMode == UserMode.LIST_VIEW || targetMode == UserMode.SAVED_VIEW){
                changeListContainerFragment(targetMode);
            }
        }
    }

    // replace container fragment with new fragment
    private void changeListContainerFragment(UserMode targetMode) {
        switch (targetMode) {
            case LIST_VIEW:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.list_container, articleListFragment, Constants.LIST_FRAG)
                        .commit();
                break;

            case SAVED_VIEW:
                getSupportFragmentManager().beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.list_container, articleSavedFragment, Constants.SAVED_LIST_FRAG)
                        .commit();
                break;
        }
    }

    // when an article is selected in the list view, set the new article in the detailes view
    // and update the fragment view state
    @Override
    public void onArticleSelected(Article article) {
        if (article != null) {
            articleDetailsFragment.setArticle(article);
        }
        updateFragmentViewState(UserMode.DETAIL_VIEW);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(USER_MODE, userMode);
        outState.putSerializable(PREVIOUS_USER_MODE, previousUserMode);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        stopForegroundService();
        super.onDestroy();
    }
}