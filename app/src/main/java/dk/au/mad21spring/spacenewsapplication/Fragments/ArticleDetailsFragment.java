package dk.au.mad21spring.spacenewsapplication.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import dk.au.mad21spring.spacenewsapplication.Activities.ArticleSelectorInterface;
import dk.au.mad21spring.spacenewsapplication.Database.Article;
import dk.au.mad21spring.spacenewsapplication.R;

public class ArticleDetailsFragment extends Fragment {

    private TextView txtTitleDetail, txtNewsSiteDetail, txtPublishedDetail, txtUpdatedDetail, txtSummaryDetail;
    private Button btnReadArticle, btnSaveForLater;
    private ImageView imViewDetail;

    private ArticleSelectorInterface articleSelector;

    public ArticleDetailsFragment() {
        // required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

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
                // Handle button click
            }
        });
        btnSaveForLater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle button click
            }
        });

        updateArticle();

        return view;
    }

    @Override
    public void onAttach(@NonNull Context activity) {
        super.onAttach(activity);

        try {
            articleSelector = (ArticleSelectorInterface) activity;
        } catch (ClassCastException ex) {
            throw new ClassCastException(activity.toString() + " must implement ArticleSelectorInterface");
        }
    }

    private void updateArticle() {
        if (articleSelector != null) {
            setArticle(articleSelector.getCurrentSelection());
        }
    }

    public void setArticle(Article article){
        if (txtTitleDetail != null && txtNewsSiteDetail != null && txtPublishedDetail != null && txtUpdatedDetail != null && txtSummaryDetail != null && txtPublishedDetail != null && txtUpdatedDetail != null) {
            txtTitleDetail.setText(article.Title);
            txtNewsSiteDetail.setText(article.NewsSite);
            txtPublishedDetail.setText(article.PublishedAt);
            txtUpdatedDetail.setText(article.UpdatededAt);
            txtSummaryDetail.setText(article.Summary);
            // opdater billede
        }
    }
}
