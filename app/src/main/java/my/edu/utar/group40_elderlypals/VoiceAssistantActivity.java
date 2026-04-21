package my.edu.utar.group40_elderlypals;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Locale;

public class VoiceAssistantActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private Button btnSpeak;
    private TextView tvRecognizedText;
    private TextView tvSystemResponse;
    private TextToSpeech textToSpeech;

    private ActivityResultLauncher<Intent> speechLauncher;
    private ActivityResultLauncher<String> permissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_assistant);

        btnSpeak = findViewById(R.id.btnSpeak);
        tvRecognizedText = findViewById(R.id.tvRecognizedText);
        tvSystemResponse = findViewById(R.id.tvSystemResponse);

        textToSpeech = new TextToSpeech(this, this);

        setupSpeechLauncher();
        setupPermissionLauncher();

        btnSpeak.setOnClickListener(v -> checkAudioPermission());
    }

    private void setupSpeechLauncher() {
        speechLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {

                        ArrayList<String> resultList =
                                result.getData().getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                        String spokenText = "";

                        if (resultList != null && !resultList.isEmpty()) {
                            spokenText = resultList.get(0);
                        }

                        tvRecognizedText.setText(spokenText);

                        VoiceAction action = CommandParser.parseCommand(spokenText);
                        tvSystemResponse.setText(action.getMessage());

                        speakOut(action.getMessage());
                        handleAction(action);
                    }
                }
        );
    }

    private void setupPermissionLauncher() {
        permissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        startVoiceInput();
                    } else {
                        Toast.makeText(this, "Microphone permission denied", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void checkAudioPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED) {
            startVoiceInput();
        } else {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO);
        }
    }

    private void startVoiceInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Please speak now");

        try {
            speechLauncher.launch(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Speech recognition is not available", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleAction(VoiceAction action) {
        String type = action.getType();

        if (type.equals("MOOD_HAPPY")) {
            saveMoodPlaceholder("Happy");
        } else if (type.equals("MOOD_SAD")) {
            saveMoodPlaceholder("Sad");
        } else if (type.equals("MOOD_TIRED")) {
            saveMoodPlaceholder("Tired");
        } else if (type.equals("MOOD_NEUTRAL")) {
            saveMoodPlaceholder("Neutral");
        } else if (type.equals("MEDICATION_YELLOW")) {
            saveMedicationPlaceholder("Yellow Pill");
        } else if (type.equals("MEDICATION_RED")) {
            saveMedicationPlaceholder("Red Pill");
        } else if (type.equals("MEDICATION_WHITE")) {
            saveMedicationPlaceholder("White Pill");
        } else if (type.equals("MEDICATION_GREEN")) {
            saveMedicationPlaceholder("Green Pill");
        } else if (type.equals("MEDICATION_TAKEN")) {
            saveMedicationPlaceholder("Unknown Pill");
        } else if (type.equals("EMERGENCY")) {
            triggerEmergencyPlaceholder();
        }
    }

    private void saveMoodPlaceholder(String mood) {
        Toast.makeText(this, "Mood saved: " + mood, Toast.LENGTH_SHORT).show();
    }

    private void saveMedicationPlaceholder(String medicineName) {
        Toast.makeText(this, "Medication saved: " + medicineName, Toast.LENGTH_SHORT).show();
    }

    private void triggerEmergencyPlaceholder() {
        Toast.makeText(this, "Emergency action triggered", Toast.LENGTH_SHORT).show();
    }

    private void speakOut(String message) {
        if (textToSpeech != null) {
            textToSpeech.speak(message, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = textToSpeech.setLanguage(Locale.US);

            if (result == TextToSpeech.LANG_MISSING_DATA ||
                    result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(this, "TTS language not supported", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "TTS initialization failed", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }
}