package my.edu.utar.group40_elderlypals;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class MenuActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // Header actions
        TextView tvBack = findViewById(R.id.tv_back);
        TextView tvLogout = findViewById(R.id.tv_logout);

        tvBack.setOnClickListener(v -> finish()); // Go to previous page

        tvLogout.setOnClickListener(v -> {
            SharedPreferences preferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            preferences.edit().putBoolean("isLoggedIn", false).apply();
            
            Intent intent = new Intent(MenuActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        // Voice Assistant logic
        Button btnVoice = findViewById(R.id.btn_voice_assistant);
        btnVoice.setOnClickListener(v -> {
            Toast.makeText(this, "Voice Assistant Triggered...", Toast.LENGTH_SHORT).show();
            // Teammate will implement the rest here
        });

        // Navigation for the four boxes
        CardView cvMedication = findViewById(R.id.cv_medication);
        CardView cvMood = findViewById(R.id.cv_mood);
        CardView cvHealth = findViewById(R.id.cv_health);
        CardView cvSettings = findViewById(R.id.cv_settings);

        cvMedication.setOnClickListener(v -> {
            startActivity(new Intent(MenuActivity.this, MedicationActivity.class));
        });

        cvMood.setOnClickListener(v -> {
            startActivity(new Intent(MenuActivity.this, MoodActivity.class));
        });

        cvHealth.setOnClickListener(v -> {
            startActivity(new Intent(MenuActivity.this, BadgesActivity.class));
        });

        cvSettings.setOnClickListener(v -> {
            startActivity(new Intent(MenuActivity.this, SettingsActivity.class));
        });

        // SOS Button logic
        Button btnSos = findViewById(R.id.btn_sos);
        btnSos.setOnLongClickListener(v -> {
            Toast.makeText(MenuActivity.this, "SOS Alert Sent!", Toast.LENGTH_SHORT).show();
            return true;
        });
    }
}
