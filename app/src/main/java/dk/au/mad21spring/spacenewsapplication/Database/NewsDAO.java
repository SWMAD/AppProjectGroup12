package dk.au.mad21spring.spacenewsapplication.Database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import dk.au.mad21spring.spacenewsapplication.Article;

@Dao
public interface NewsDAO {

    @Insert
    void addArticle(Article article);

    @Query("DELETE FROM Article WHERE ArticleID LIKE :ArticleID")
    void deleteArticle(String ArticleID);
}
