package my.edu.utar.group40_elderlypals;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "mood_log_table")
public class MoodLog {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String date;
    public String moodType;
    public String notes;

    public MoodLog(String date, String moodType, String notes) {
        this.date = date;
        this.moodType = moodType;
        this.notes = notes;
    }
}
