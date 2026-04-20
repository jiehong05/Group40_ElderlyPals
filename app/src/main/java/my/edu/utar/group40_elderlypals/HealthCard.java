package my.edu.utar.group40_elderlypals;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "health_card_table")
public class HealthCard {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String emergencyContactName;
    public String emergencyContactPhone;
    public String bloodType;
    public String allergies;

    public HealthCard(String emergencyContactName, String emergencyContactPhone, String bloodType, String allergies) {
        this.emergencyContactName = emergencyContactName;
        this.emergencyContactPhone = emergencyContactPhone;
        this.bloodType = bloodType;
        this.allergies = allergies;
    }
}