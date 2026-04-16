package my.edu.utar.group40_elderlypals;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Button btnOpenVoiceAssistant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnOpenVoiceAssistant = findViewById(R.id.btnOpenVoiceAssistant);

        btnOpenVoiceAssistant.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, VoiceAssistantActivity.class);
            startActivity(intent);
        });
    }
}