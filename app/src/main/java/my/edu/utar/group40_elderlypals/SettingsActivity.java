package my.edu.utar.group40_elderlypals;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    private TextView tvUserName, tvInfoDisplay;
    private View layoutUserInfo;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        preferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);

        tvUserName = findViewById(R.id.tv_user_name);
        tvInfoDisplay = findViewById(R.id.tv_info_display);
        layoutUserInfo = findViewById(R.id.layout_user_info);

        findViewById(R.id.tv_back).setOnClickListener(v -> finish());
        
        findViewById(R.id.btn_profile).setOnClickListener(v -> {
            startActivity(new Intent(this, ProfileActivity.class));
        });

        findViewById(R.id.tv_logout).setOnClickListener(v -> {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("isLoggedIn", false);
            editor.apply();

            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        updateUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }

    private void updateUI() {
        String name = preferences.getString("userName", "User");
        String phone = preferences.getString("userPhone", "-");
        String address = preferences.getString("userAddress", "-");
        boolean hideInfo = preferences.getBoolean("hideInfo", false);

        tvUserName.setText("Hi, " + name + ".");

        if (hideInfo) {
            tvInfoDisplay.setText("Phone: ********\nAddress: ********");
        } else {
            tvInfoDisplay.setText("Phone: " + phone + "\nAddress: " + address);
        }
    }
}
