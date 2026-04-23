package my.edu.utar.group40_elderlypals;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface MoodLogDao {
    @Insert
    void insert(MoodLog moodLog);

    @Query("SELECT * FROM mood_table ORDER BY id ASC")
    List<MoodLog> getAllMoodLogs();

    @Update
    void update(MoodLog moodLog);

    @Delete
    void delete(MoodLog moodLog);
}
