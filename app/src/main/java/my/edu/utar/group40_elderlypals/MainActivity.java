//package my.edu.utar.group40_elderlypals;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.widget.Button;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import my.edu.utar.group40_elderlypals.internal_integration.EmergencyManager;
//import my.edu.utar.group40_elderlypals.internal_integration.LocationHelper;
//import my.edu.utar.group40_elderlypals.internal_integration.WeatherServiceProvider;
//
//public class MainActivity extends AppCompatActivity {
//
//    private Button btnOpenVoiceAssistant;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        btnOpenVoiceAssistant = findViewById(R.id.btn_voice_assistant);
//
//        btnOpenVoiceAssistant.setOnClickListener(v -> {
//            Intent intent = new Intent(MainActivity.this, VoiceAssistantActivity.class);
//            startActivity(intent);
//        });
//
//        // Initialize Member 2's services
//        LocationHelper locationHelper = new LocationHelper(this);
//        WeatherServiceProvider weatherProvider = new WeatherServiceProvider();
//
//        // 1. Get current location (GPS)
//        locationHelper.getCurrentLocation((lat, lon) -> {
//            // 2. Once location is found, fetch the weather for that spot
//            weatherProvider.fetchWeatherAdvice(lat, lon, new WeatherServiceProvider.WeatherAdviceListener() {
//                @Override
//                public void onAdviceReceived(String advice) {
//                    // This logs it for you (Member 2)
//                    android.util.Log.d("WeatherAlert", "MEMBER 2 ADVICE: " + advice);
//
//                    // This shows it to the User!
//                    runOnUiThread(() -> {
//                        TextView mainText = findViewById(R.id.tvWelcome);
//                        mainText.setText(advice);
//                    });
//                }
//
//                @Override
//                public void onError(String message) {
//                    android.util.Log.e("WeatherAlert", "MEMBER 2 ERROR: " + message);
//                }
//            });
//        });
//
//        // Link the SOS button container
//        LinearLayout sosButton = findViewById(R.id.sosButtonContainer);
//        EmergencyManager emergencyManager = new EmergencyManager();
//
//        sosButton.setOnClickListener(v -> {
//            // 1. Get current location when button is pressed
//            locationHelper.getCurrentLocation((lat, lon) -> {
//
//                // 2. Generate the message
//                String fullAlert = emergencyManager.createEmergencyMessage(lat, lon);
//
//                // 3. For now, show it in a Toast and Log so you can see it working
//                android.widget.Toast.makeText(this, "🚨 SOS SENT: " + fullAlert, android.widget.Toast.LENGTH_LONG).show();
//                android.util.Log.d("SOS_ALERT", fullAlert);
//            });
//        });
//    }
//}