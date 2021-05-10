package dk.au.mad21spring.spacenewsapplication.Database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface NewsDAO {

    @Insert
    void addArticle(Article article);

    @Query("DELETE FROM Article WHERE ArticleID LIKE :ArticleID")
    void deleteArticle(String ArticleID);

    @Query("SELECT * FROM Article")
    LiveData<List<Article>> getAllReadLaterArticles();

    @Query("DELETE FROM Article")
    void deleteAll();

    @Query("SELECT * FROM Article WHERE ArticleID LIKE :ArticleID")
    Article isArticleSaved(String ArticleID);
}
