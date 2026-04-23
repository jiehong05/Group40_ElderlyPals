package my.edu.utar.group40_elderlypals;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "medication_table")
public class Medication {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String name;
    public String time;
    public String color;

    public Medication(String name, String time, String color) {
        this.name = name;
        this.time = time;
        this.color = color;
    }
}
