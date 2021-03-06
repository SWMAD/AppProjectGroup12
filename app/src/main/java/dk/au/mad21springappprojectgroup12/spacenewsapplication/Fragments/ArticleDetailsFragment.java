package dk.au.mad21springappprojectgroup12.spacenewsapplication.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;

import dk.au.mad21springappprojectgroup12.spacenewsapplication.Constants;
import dk.au.mad21springappprojectgroup12.spacenewsapplication.Database.Article;
import dk.au.mad21springappprojectgroup12.spacenewsapplication.R;
import dk.au.mad21springappprojectgroup12.spacenewsapplication.ViewModels.DetailsViewModel;

public class ArticleDetailsFragment extends Fragment {

    // tag for the savedStateInstance
    private static final String CHOSEN_ARTICLE = "chosenArticle";

    // ui elements
    private TextView txtTitleDetail, txtNewsSiteDetail, txtPublishedDetail, txtUpdatedDetail, txtSummaryDetail;
    private Button btnReadArticle, btnSaveForLater;
    private ImageView imViewDetail;

    // view model
    private DetailsViewModel vm;

    // article to be shown
    private Article chosenArticle;

    // required empty public constructor
    public ArticleDetailsFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        chosenArticle = new Article();

        vm = new ViewModelProvider(this).get(DetailsViewModel.class);

        // if the app is restarted for example due to rotation, the previous article should be
        // shown after restart. otherwise the article by default is set to the first article in the
        // list with all the articles
        if (savedInstanceState != null) {
            chosenArticle = savedInstanceState.getParcelable(CHOSEN_ARTICLE);
        } else {
            chosenArticle = vm.getArticle(Constants.LIST_FRAG, 0);
        }

        // inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_article, container, false);

        //get references to UI widgets
        txtTitleDetail = view.findViewById(R.id.txtTitleDetail);
        txtNewsSiteDetail = view.findViewById(R.id.txtNewsSiteDetail);
        txtPublishedDetail = view.findViewById(R.id.txtPublishedDetail);
        txtUpdatedDetail = view.findViewById(R.id.txtUpdatedDetail);
        txtSummaryDetail = view.findViewById(R.id.txtSummaryDetail);
        imViewDetail = view.findViewById(R.id.imViewDetail);
        btnReadArticle = view.findViewById(R.id.btnReadArticle);
        btnSaveForLater = view.findViewById(R.id.btnSaveForLater);

        btnReadArticle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openBrowser(v, chosenArticle.Url);
            }
        });


        btnSaveForLater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (vm.isArticleSaved(chosenArticle) == false){
                    vm.addArticleToReadLater(chosenArticle);

                    Toast.makeText(getContext(), getResources().getText(R.string.Toast_SavedForLater),
                            Toast.LENGTH_SHORT).show();
                    btnSaveForLater.setText(R.string.RemoveFromReadLater);
                }
                else {
                    vm.deleteArticle(chosenArticle);
                    Toast.makeText(getContext(), getResources().getText(R.string.Toast_RemovedFromReadlater),
                            Toast.LENGTH_SHORT).show();
                    btnSaveForLater.setText(R.string.btnSaveForLater);
                }
            }
        });

        setArticle(chosenArticle);

        return view;
    }

    // set ui widgets with the article properties
    public void setArticle(Article article){

        if (vm.isArticleSaved(article) == false){
            btnSaveForLater.setText(R.string.btnSaveForLater);
        }
        else {
            btnSaveForLater.setText(R.string.RemoveFromReadLater);
        }

        if (txtTitleDetail != null && txtNewsSiteDetail != null && txtPublishedDetail != null && txtUpdatedDetail != null && txtSummaryDetail != null && txtPublishedDetail != null && txtUpdatedDetail != null) {
            txtTitleDetail.setText(article.Title);
            txtNewsSiteDetail.setText(article.NewsSite);
            txtPublishedDetail.setText(article.PublishedAt);
            txtUpdatedDetail.setText(article.UpdatededAt);
            txtSummaryDetail.setText(article.Summary);
            Glide.with(imViewDetail.getContext()).load(article.ImageUrl).into(imViewDetail);
        }

        chosenArticle = article;
    }

    // method for opening browser when 'Read article' button is clicked
    // Inspiration from https://www.youtube.com/watch?v=9-3OCc5g5oE&ab_channel=EAngkorTech
    private void openBrowser(View view, String url){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelable(CHOSEN_ARTICLE, chosenArticle);
        super.onSaveInstanceState(outState);
    }
}
