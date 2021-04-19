package dk.au.mad21spring.spacenewsapplication.Database;

import android.content.Context;

import androidx.room.Room;
import androidx.room.RoomDatabase;

// Code regarding database inspired by code demo from class: "roomdemo 1.1"
@NewsDatabase(entities = {CityDTO.class}, version = 4)
public abstract class NewsDatabase extends RoomDatabase {
    public abstract CityDAO cityDAO();
    private static CityDatabase instance;

    public static CityDatabase getDatabase(final Context context) {
        if (instance == null) {
            synchronized (CityDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context.getApplicationContext(),
                            CityDatabase.class, "city_database")
                            .fallbackToDestructiveMigration()
                            .allowMainThreadQueries()
                            .build();

                }
            }
        }
        return instance;
    }
}
