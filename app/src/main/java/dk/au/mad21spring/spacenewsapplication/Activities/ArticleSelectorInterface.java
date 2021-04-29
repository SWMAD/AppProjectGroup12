package dk.au.mad21spring.spacenewsapplication.Activities;

import java.util.ArrayList;

import dk.au.mad21spring.spacenewsapplication.Database.Article;

public interface ArticleSelectorInterface {
    public void onArticleSelected(int position);
    public ArrayList<Article> getArticleList();
    public Article getCurrentSelection();
    public void viewSaved();
}
