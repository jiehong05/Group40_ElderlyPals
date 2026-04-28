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
import android.app.AlertDialog;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import my.edu.utar.group40_elderlypals.internal_integration.EmergencyManager;
import my.edu.utar.group40_elderlypals.internal_integration.LocationHelper;

public class VoiceAssistantActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private Button btnSpeak;

    private ActivityResultLauncher<String> callPermissionLauncher;
    private ActivityResultLauncher<String> locationPermissionLauncher;
    private ActivityResultLauncher<Intent> speechLauncher;
    private ActivityResultLauncher<String> permissionLauncher;

    private LocationHelper locationHelper;
    private EmergencyManager emergencyManager;
    private TextView tvRecognizedText;
    private TextView tvSystemResponse;
    private TextToSpeech textToSpeech;

    // Use the same database as MoodActivity and MedicationActivity
    private HealthVaultDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_assistant);

        btnSpeak = findViewById(R.id.btnSpeak);
        tvRecognizedText = findViewById(R.id.tvRecognizedText);
        tvSystemResponse = findViewById(R.id.tvSystemResponse);

        textToSpeech = new TextToSpeech(this, this);
        locationHelper = new LocationHelper(this);
        emergencyManager = new EmergencyManager();
        db = HealthVaultDatabase.getInstance(this);

        setupLocationPermissionLauncher();
        setupCallPermissionLauncher();
        setupSpeechLauncher();
        setupPermissionLauncher();

        btnSpeak.setOnClickListener(v -> checkAudioPermission());
    }

    private void setupCallPermissionLauncher() {
        callPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        triggerEmergencyWithLocation();
                    } else {
                        Toast.makeText(this, "Call permission denied", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void setupLocationPermissionLauncher() {
        locationPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        triggerEmergencyWithLocation();
                    } else {
                        Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void checkLocationPermissionAndTriggerEmergency() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
            return;
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            callPermissionLauncher.launch(Manifest.permission.CALL_PHONE);
            return;
        }

        triggerEmergencyWithLocation();
    }

    private void triggerEmergencyWithLocation() {
        locationHelper.getCurrentLocation((lat, lon) -> {
            String emergencyMessage = emergencyManager.createEmergencyMessage(lat, lon);

            runOnUiThread(() -> {
                tvSystemResponse.setText(emergencyMessage);
                speakOut("Emergency alert prepared.");

                new AlertDialog.Builder(VoiceAssistantActivity.this)
                        .setTitle("Emergency Alert")
                        .setMessage(emergencyMessage)
                        .setPositiveButton("Call Now", (dialog, which) -> {
                            String emergencyNumber = "tel:01118770588";
                            Intent callIntent = new Intent(Intent.ACTION_CALL);
                            callIntent.setData(android.net.Uri.parse(emergencyNumber));

                            try {
                                startActivity(callIntent);
                            } catch (SecurityException e) {
                                Toast.makeText(this, "Please enable Call permissions!", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            });
        });
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

        if ("MOOD".equals(commandType)) {
            saveMoodToDatabase(payload);
            Toast.makeText(this, "Mood saved: " + payload, Toast.LENGTH_SHORT).show();

        } else if ("MEDICATION".equals(commandType)) {
            saveMedicationToDatabase(payload);
            Toast.makeText(this, "Medication saved: " + payload, Toast.LENGTH_SHORT).show();

        } else if ("EMERGENCY".equals(commandType)) {
            checkLocationPermissionAndTriggerEmergency();
        }
    }

    private void saveMoodToDatabase(String moodLabel) {
        int moodValue;

        switch (moodLabel) {
            case "happy":
                moodValue = 4;
                break;
            case "neutral":
                moodValue = 3;
                break;
            case "sad":
                moodValue = 2;
                break;
            case "tired":
                moodValue = 1;
                break;
            default:
                moodValue = 3;
                break;
        }

        String timeStamp = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());

        MoodLog newLog = new MoodLog(moodValue, timeStamp);
        db.moodLogDao().insert(newLog);

        // Keep at most 7 recent records, same idea as MoodActivity
        List<MoodLog> allLogs = db.moodLogDao().getAllMoodLogs();
        if (allLogs.size() > 7) {
            db.moodLogDao().delete(allLogs.get(0));
        }
    }

    private void saveMedicationToDatabase(String medicationColor) {
        String displayColor;

        switch (medicationColor) {
            case "yellow":
                displayColor = "Yellow";
                break;
            case "red":
                displayColor = "Red";
                break;
            case "white":
                displayColor = "White";
                break;
            case "green":
                displayColor = "Green";
                break;
            case "blue":
                displayColor = "Blue";
                break;
            default:
                displayColor = "Blue";
                break;
        }

        String timeStamp = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());

        // Since voice command currently provides color, use color pill as the simple name
        Medication medication = new Medication(displayColor + " pill", timeStamp, displayColor);
        db.medicationDao().insert(medication);
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