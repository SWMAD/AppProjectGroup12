package dk.au.mad21spring.spacenewsapplication;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Article {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String ArticleID;
    public String Title;
    public String Url;
    public String ImageUrl;
    public String NewsSite;
    public String Summary;

    public Article() {

    }

    public Article(String articleID, String title, String url, String imageUrl, String newsSite, String summary)
    {
        this.ArticleID = articleID;
        this.Title = title;
        this.Url = url;
        this.ImageUrl = imageUrl;
        this.NewsSite = newsSite;
        this.Summary = summary;
    }
}
