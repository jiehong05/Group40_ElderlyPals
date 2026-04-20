package my.edu.utar.group40_elderlypals;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface HealthCardDao {
    @Insert
    void insert(HealthCard healthCard);

    @Query("SELECT * FROM health_card_table LIMIT 1")
    HealthCard getHealthCard();

    @Update
    void update(HealthCard healthCard);

    @Delete
    void delete(HealthCard healthCard);
}
