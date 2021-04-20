package dk.au.mad21spring.spacenewsapplication.Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

// Code regarding database inspired by code demo from class: "roomdemo 1.1"
@Database(entities = {Article.class}, version = 5)
public abstract class NewsDatabase extends RoomDatabase {
    public abstract NewsDAO newsDAO();
    private static NewsDatabase instance;

    public static NewsDatabase getDatabase(final Context context) {
        if (instance == null) {
            synchronized (NewsDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context.getApplicationContext(),
                            NewsDatabase.class, "news_database")
                            .fallbackToDestructiveMigration()
                            .allowMainThreadQueries()
                            .build();
                }
            }
        }
        return instance;
    }
}
