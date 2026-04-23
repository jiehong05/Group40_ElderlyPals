package my.edu.utar.group40_elderlypals;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "health_card_table")
public class HealthCard {

    @PrimaryKey
    public int id = 1;

    public String userName;
    public String userPhone;
    public String userAddress;
    public boolean hideInfo;

    public HealthCard(String userName, String userPhone, String userAddress, boolean hideInfo) {
        this.userName = userName;
        this.userPhone = userPhone;
        this.userAddress = userAddress;
        this.hideInfo = hideInfo;
    }
}