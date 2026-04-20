package my.edu.utar.group40_elderlypals;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "medication_table")
public class Medication {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String medName;
    public String time;
    public boolean isTaken;

    public Medication(String medName, String time, boolean isTaken) {
        this.medName = medName;
        this.time = time;
        this.isTaken = isTaken;
    }
}
