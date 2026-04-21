package my.edu.utar.group40_elderlypals;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Vibrator;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity implements VoiceAssistantListener {

    private FloatingActionButton fabVoice;
    private Button btnNext;
    private Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check login status
        SharedPreferences preferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        boolean isLoggedIn = preferences.getBoolean("isLoggedIn", false);
        if (isLoggedIn) {
            Intent intent = new Intent(MainActivity.this, MenuActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        fabVoice = findViewById(R.id.fab_voice_assistant);
        btnNext = findViewById(R.id.btn_next_page);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        fabVoice.setOnClickListener(v -> {
            provideHapticFeedback();
            onStateChanged("LISTENING");
        });

        btnNext.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }

    private void provideHapticFeedback() {
        if (vibrator != null && vibrator.hasVibrator()) {
            vibrator.vibrate(50); // Short buzz for elderly feedback
        }
    }

    @Override
    public void onCommandDetected(String commandType, String payload) {
        // Handle database updates (Member 1) or Emergency calls (Member 2)
        Toast.makeText(this, "Command: " + commandType + " (" + payload + ")", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onStateChanged(String state) {
        switch (state) {
            case "LISTENING":
                fabVoice.setImageResource(android.R.drawable.ic_btn_speak_now); // Pulse animation placeholder
                break;
            case "PROCESSING":
                // Show loading spinner
                break;
            case "IDLE":
                fabVoice.setImageResource(android.R.drawable.ic_btn_speak_now);
                break;
        }
    }

    @Override
    public void onError(String errorMessage) {
        Toast.makeText(this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
    }
}
