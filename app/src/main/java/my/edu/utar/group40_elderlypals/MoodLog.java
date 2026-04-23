package my.edu.utar.group40_elderlypals;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "mood_table")
public class MoodLog {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public int moodValue;
    public String timestamp;

    public MoodLog(int moodValue, String timestamp) {
        this.moodValue = moodValue;
        this.timestamp = timestamp;
    }
}