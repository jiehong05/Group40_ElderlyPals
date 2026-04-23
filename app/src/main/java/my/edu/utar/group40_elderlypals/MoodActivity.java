package my.edu.utar.group40_elderlypals;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MoodActivity extends AppCompatActivity {

    private MoodChartView moodChartView;
    private List<Integer> moodValues = new ArrayList<>();
    private List<String> timestamps = new ArrayList<>();

    private HealthVaultDatabase db;
    private List<MoodLog> currentMoodLogs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood);

        db = HealthVaultDatabase.getInstance(this);
        moodChartView = findViewById(R.id.mood_chart_view);

        loadMoodDataFromDb();

        moodChartView.setOnPointSelectedListener(index -> {
            new AlertDialog.Builder(this)
                    .setTitle("Delete Record")
                    .setMessage("Do you want to delete this mood entry from " + timestamps.get(index) + "?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        if (index < currentMoodLogs.size()) {
                            db.moodLogDao().delete(currentMoodLogs.get(index));
                            loadMoodDataFromDb();
                            Toast.makeText(this, "Entry deleted", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        findViewById(R.id.tv_back).setOnClickListener(v -> finish());

        findViewById(R.id.tv_logout).setOnClickListener(v -> {
            SharedPreferences preferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
            preferences.edit().putBoolean("isLoggedIn", false).apply();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        setupMoodCardHover(findViewById(R.id.cv_happy), 4, "Happy");
        setupMoodCardHover(findViewById(R.id.cv_neutral), 3, "Neutral");
        setupMoodCardHover(findViewById(R.id.cv_sad), 2, "Sad");
        setupMoodCardHover(findViewById(R.id.cv_tired), 1, "Tired");
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadMoodDataFromDb();
    }

    private void loadMoodDataFromDb() {
        currentMoodLogs = db.moodLogDao().getAllMoodLogs();

        moodValues.clear();
        timestamps.clear();

        for (MoodLog log : currentMoodLogs) {
            moodValues.add(log.moodValue);
            timestamps.add(log.timestamp);
        }

        moodChartView.setData(new ArrayList<>(moodValues), new ArrayList<>(timestamps));
    }

    private void recordMood(int value, String label) {
        String timeStamp = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());

        MoodLog newLog = new MoodLog(value, timeStamp);
        db.moodLogDao().insert(newLog);

        List<MoodLog> allLogs = db.moodLogDao().getAllMoodLogs();
        if (allLogs.size() > 7) {
            db.moodLogDao().delete(allLogs.get(0));
        }

        loadMoodDataFromDb();
        Toast.makeText(this, "Recorded: " + label + " at " + timeStamp, Toast.LENGTH_SHORT).show();
    }

    private void setupMoodCardHover(View view, int value, String label) {
        view.setOnClickListener(v -> recordMood(value, label));
        final View emojiView;
        if (view.getId() == R.id.cv_happy) emojiView = findViewById(R.id.tv_happy_emoji);
        else if (view.getId() == R.id.cv_neutral) emojiView = findViewById(R.id.tv_neutral_emoji);
        else if (view.getId() == R.id.cv_sad) emojiView = findViewById(R.id.tv_sad_emoji);
        else emojiView = findViewById(R.id.tv_tired_emoji);

        view.setOnHoverListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_HOVER_ENTER:
                case MotionEvent.ACTION_HOVER_MOVE:
                    if (emojiView != null && emojiView.getScaleX() == 1.0f) {
                        emojiView.animate().scaleX(1.5f).scaleY(1.5f).setDuration(200).start();
                        emojiView.playSoundEffect(SoundEffectConstants.CLICK);
                        emojiView.postDelayed(() -> emojiView.playSoundEffect(SoundEffectConstants.CLICK), 50);
                    }
                    return true;
                case MotionEvent.ACTION_HOVER_EXIT:
                    if (emojiView != null) {
                        emojiView.postDelayed(() -> emojiView.animate().scaleX(1.0f).scaleY(1.0f).setDuration(200).start(), 500);
                    }
                    return true;
            }
            return false;
        });
    }
}