package dk.au.mad21spring.spacenewsapplication.Database;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

@Entity
public class Article implements Parcelable {

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

    //source of inspiration for parcelable: https://www.youtube.com/watch?v=WBbsvqSu0is&ab_channel=CodinginFlow
    protected Article(Parcel in) {
        Id = in.readInt();
        ArticleID = in.readString();
        Title = in.readString();
        Url = in.readString();
        ImageUrl = in.readString();
        NewsSite = in.readString();
        Summary = in.readString();
        PublishedAt = in.readString();
        UpdatededAt = in.readString();
    }

    public static final Creator<Article> CREATOR = new Creator<Article>() {
        @Override
        public Article createFromParcel(Parcel in) {
            return new Article(in);
        }

        @Override
        public Article[] newArray(int size) {
            return new Article[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(Id);
        dest.writeString(ArticleID);
        dest.writeString(Title);
        dest.writeString(Url);
        dest.writeString(ImageUrl);
        dest.writeString(NewsSite);
        dest.writeString(Summary);
        dest.writeString(PublishedAt);
        dest.writeString(UpdatededAt);
    }
}
