package my.edu.utar.group40_elderlypals;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import my.edu.utar.group40_elderlypals.internal_integration.EmergencyManager;
import my.edu.utar.group40_elderlypals.internal_integration.LocationHelper;
import my.edu.utar.group40_elderlypals.internal_integration.WeatherServiceProvider;

import android.Manifest;
import android.content.pm.PackageManager;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;

public class MenuActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        TextView tvBack = findViewById(R.id.tv_back);
        TextView tvLogout = findViewById(R.id.tv_logout);

        tvBack.setOnClickListener(v -> finish());

        tvLogout.setOnClickListener(v -> {
            SharedPreferences preferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            preferences.edit().putBoolean("isLoggedIn", false).apply();

            Intent intent = new Intent(MenuActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        Button btnVoice = findViewById(R.id.btn_voice_assistant);
        btnVoice.setOnClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this, VoiceAssistantActivity.class);
            startActivity(intent);
        });

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

        // Initialize Member 2's services
        LocationHelper locationHelper = new LocationHelper(this);
        WeatherServiceProvider weatherProvider = new WeatherServiceProvider();

        // 1. Get current location (GPS)
        locationHelper.getCurrentLocation((lat, lon) -> {
            // 1. Get the city name from our helper
            String city = locationHelper.getCityName(lat, lon);

            // 2. Fetch the weather
            weatherProvider.fetchWeatherAdvice(lat, lon, new WeatherServiceProvider.WeatherAdviceListener() {
                @Override
                public void onAdviceReceived(String advice) {
                    runOnUiThread(() -> {
                        // Show BOTH the city and the advice
                        TextView mainText = findViewById(R.id.TVWelcome);
                        mainText.setText("📍 " + city + "\n" + advice);
                    });
                }
                @Override

                public void onError(String message) {

                    Log.e("WeatherAlert", "WEATHER ERROR: " + message);

                }
            });
        });


        // Link the SOS button container
        Button sosButton = findViewById(R.id.btn_sos);
        EmergencyManager emergencyManager = new EmergencyManager();

        sosButton.setOnLongClickListener(v -> {
            // 1. Existing Logic: Get Location and log the message
            locationHelper.getCurrentLocation((lat, lon) -> {
                String fullAlert = emergencyManager.createEmergencyMessage(lat, lon);
                android.util.Log.d("SOS_ALERT", fullAlert);

                // 2. NEW Feature: Trigger the Phone Call
                // Replace '999' with a family member's number for testing if you prefer
                String emergencyNumber = "tel:0195613287";
                android.content.Intent callIntent = new android.content.Intent(android.content.Intent.ACTION_CALL);
                callIntent.setData(android.net.Uri.parse(emergencyNumber));

                try {
                    startActivity(callIntent);
                } catch (SecurityException e) {
                    // This happens if the user didn't grant "Call" permissions in settings
                    android.widget.Toast.makeText(this, "Please enable Call permissions!", android.widget.Toast.LENGTH_SHORT).show();
                }
            });
            return true;
        });
    }
}