package dk.au.mad21spring.spacenewsapplication.Database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface NewsDAO {

    @Query("SELECT * FROM cityDTO")
    LiveData<List<CityDTO>> getAllCities();

    @Query("SELECT * FROM cityDTO")
    List<CityDTO> getAllCitiesNonAsynch();

    @Query("SELECT * FROM cityDTO WHERE uid LIKE :uid")
    CityDTO findCity(int uid);

    @Query("SELECT * FROM cityDTO WHERE cityName LIKE :name")
    CityDTO findCity(String name);

    @Insert
    void addCity(CityDTO city);

    @Update
    void updateCity(CityDTO city);

    @Query("DELETE FROM cityDTO WHERE uid LIKE :uid")
    void deleteCity(int uid);

    @Query("DELETE FROM CityDTO")
    void deleteAll();
}
