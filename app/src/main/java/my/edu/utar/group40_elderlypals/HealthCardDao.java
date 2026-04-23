package my.edu.utar.group40_elderlypals;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface HealthCardDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrUpdate(HealthCard healthCard);

    @Query("SELECT * FROM health_card_table WHERE id = 1")
    HealthCard getHealthCard();

    @Update
    void update(HealthCard healthCard);

    @Delete
    void delete(HealthCard healthCard);
}
