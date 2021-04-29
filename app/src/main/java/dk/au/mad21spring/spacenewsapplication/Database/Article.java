package dk.au.mad21spring.spacenewsapplication.Database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

@Entity
public class Article {

    @PrimaryKey(autoGenerate = true)
    public int Id;

    public String ArticleID;
    public String Title;
    public String Url;
    public String ImageUrl;
    public String NewsSite;
    public String Summary;
    public String PublishedAt;
    public String UpdatededAt;

    public Article() {

    }

    public Article(String articleID, String title, String url, String imageUrl, String newsSite, String summary, String publishedAt, String updatedAt)
    {
        this.ArticleID = articleID;
        this.Title = title;
        this.Url = url;
        this.ImageUrl = imageUrl;
        this.NewsSite = newsSite;
        this.Summary = summary;
        this.PublishedAt = publishedAt;
        this.UpdatededAt = updatedAt;
    }

    // Get set methods
}
