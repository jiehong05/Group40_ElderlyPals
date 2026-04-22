package my.edu.utar.group40_elderlypals;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.SharedPreferences;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US");
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Please speak now");

        try {
            speechLauncher.launch(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Speech recognition is not available", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleAction(VoiceAction action) {
        String commandType = action.getCommandType();
        String payload = action.getPayload();

        if (commandType.equals("MOOD")) {
            saveMoodToSharedPreferences(payload);
            Toast.makeText(this, "Mood saved: " + payload, Toast.LENGTH_SHORT).show();

        } else if (commandType.equals("MEDICATION")) {
            saveMedicationToSharedPreferences(payload);
            Toast.makeText(this, "Medication saved: " + payload, Toast.LENGTH_SHORT).show();

        } else if (commandType.equals("EMERGENCY")) {
            Toast.makeText(this, "Emergency action triggered", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveMoodToSharedPreferences(String moodLabel) {
        SharedPreferences preferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);

        int moodValue = 3;
        if (moodLabel.equals("happy")) {
            moodValue = 4;
        } else if (moodLabel.equals("neutral")) {
            moodValue = 3;
        } else if (moodLabel.equals("sad")) {
            moodValue = 2;
        } else if (moodLabel.equals("tired")) {
            moodValue = 1;
        }

        String savedVals = preferences.getString("moodValues", "");
        String savedTimes = preferences.getString("moodTimes", "");

        String timeStamp = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());

        String newVals;
        String newTimes;

        if (savedVals.isEmpty()) {
            newVals = String.valueOf(moodValue);
            newTimes = timeStamp;
        } else {
            newVals = savedVals + "," + moodValue;
            newTimes = savedTimes + "," + timeStamp;
        }

        preferences.edit()
                .putString("moodValues", newVals)
                .putString("moodTimes", newTimes)
                .apply();
    }

    private void saveMedicationToSharedPreferences(String medicationColor) {
        SharedPreferences preferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);

        String displayColor;
        if (medicationColor.equals("yellow")) {
            displayColor = "Yellow";
        } else if (medicationColor.equals("red")) {
            displayColor = "Red";
        } else if (medicationColor.equals("white")) {
            displayColor = "White";
        } else if (medicationColor.equals("green")) {
            displayColor = "Green";
        } else if (medicationColor.equals("blue")) {
            displayColor = "Blue";
        } else {
            displayColor = "Unknown";
        }

        String timeStamp = new java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
                .format(new java.util.Date());

        String oldHistory = preferences.getString("medicationHistory", "");
        String[] lines = oldHistory.isEmpty() ? new String[0] : oldHistory.split("\n");

        StringBuilder updatedHistory = new StringBuilder();
        boolean found = false;

        for (String line : lines) {
            if (line.startsWith(displayColor + " pill at ")) {
                // Replace old record of same color with newest time
                if (updatedHistory.length() > 0) {
                    updatedHistory.append("\n");
                }
                updatedHistory.append(displayColor).append(" pill at ").append(timeStamp);
                found = true;
            } else if (!line.trim().isEmpty()) {
                if (updatedHistory.length() > 0) {
                    updatedHistory.append("\n");
                }
                updatedHistory.append(line);
            }
        }

        if (!found) {
            if (updatedHistory.length() > 0) {
                updatedHistory.append("\n");
            }
            updatedHistory.append(displayColor).append(" pill at ").append(timeStamp);
        }

        preferences.edit()
                .putString("medicationHistory", updatedHistory.toString())
                .putString("lastMedicationColor", displayColor)
                .putString("lastMedicationTime", timeStamp)
                .apply();
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