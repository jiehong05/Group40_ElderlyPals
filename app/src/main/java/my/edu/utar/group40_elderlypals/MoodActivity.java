package my.edu.utar.group40_elderlypals;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MoodActivity extends AppCompatActivity {

    private MoodChartView moodChartView;
    private SharedPreferences preferences;
    private List<Integer> moodValues = new ArrayList<>();
    private List<String> timestamps = new ArrayList<>();

    // Change this if you want another number
    private static final String SUPPORT_PHONE = "01118770588";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood);

        preferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        moodChartView = findViewById(R.id.mood_chart_view);

        loadMoodData();

        moodChartView.setOnPointSelectedListener(index -> {
            new AlertDialog.Builder(this)
                    .setTitle("Delete Record")
                    .setMessage("Do you want to delete this mood entry from " + timestamps.get(index) + "?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        moodValues.remove(index);
                        timestamps.remove(index);
                        saveMoodData();
                        moodChartView.setData(new ArrayList<>(moodValues), new ArrayList<>(timestamps));
                        Toast.makeText(this, "Entry deleted", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        findViewById(R.id.tv_back).setOnClickListener(v -> finish());

        findViewById(R.id.tv_logout).setOnClickListener(v -> {
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

        setupCommunicationButtons();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadMoodData();
    }

    private void setupCommunicationButtons() {
        MaterialButton btnCall = findViewById(R.id.btn_call);
        MaterialButton btnMessage = findViewById(R.id.btn_message);

        btnCall.setOnClickListener(v -> {
            try {
                Intent dialIntent = new Intent(Intent.ACTION_DIAL);
                dialIntent.setData(Uri.parse("tel:" + SUPPORT_PHONE));
                startActivity(dialIntent);
            } catch (Exception e) {
                Toast.makeText(this, "Unable to open dialer", Toast.LENGTH_SHORT).show();
            }
        });

        btnMessage.setOnClickListener(v -> {
            try {
                String message = "Hello, I need support from Elderly-Pals.";
                Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
                smsIntent.setData(Uri.parse("smsto:" + SUPPORT_PHONE));
                smsIntent.putExtra("sms_body", message);
                startActivity(smsIntent);
            } catch (Exception e) {
                Toast.makeText(this, "Unable to open message app", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupMoodCardHover(View view, int value, String label) {
        view.setOnClickListener(v -> recordMood(value, label));

        final View emojiView;
        if (view.getId() == R.id.cv_happy) {
            emojiView = findViewById(R.id.tv_happy_emoji);
        } else if (view.getId() == R.id.cv_neutral) {
            emojiView = findViewById(R.id.tv_neutral_emoji);
        } else if (view.getId() == R.id.cv_sad) {
            emojiView = findViewById(R.id.tv_sad_emoji);
        } else {
            emojiView = findViewById(R.id.tv_tired_emoji);
        }

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
                        emojiView.postDelayed(() ->
                                emojiView.animate().scaleX(1.0f).scaleY(1.0f).setDuration(200).start(), 500);
                    }
                    return true;
            }
            return false;
        });
    }

    private void recordMood(int value, String label) {
        String timeStamp = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());

        moodValues.add(value);
        timestamps.add(timeStamp);

        if (moodValues.size() > 7) {
            moodValues.remove(0);
            timestamps.remove(0);
        }

        saveMoodData();
        moodChartView.setData(new ArrayList<>(moodValues), new ArrayList<>(timestamps));

        Toast.makeText(this, "Recorded: " + label + " at " + timeStamp, Toast.LENGTH_SHORT).show();
    }

    private void saveMoodData() {
        StringBuilder valStr = new StringBuilder();
        StringBuilder timeStr = new StringBuilder();

        for (int i = 0; i < moodValues.size(); i++) {
            valStr.append(moodValues.get(i));
            timeStr.append(timestamps.get(i));

            if (i < moodValues.size() - 1) {
                valStr.append(",");
                timeStr.append(",");
            }
        }

        preferences.edit()
                .putString("moodValues", valStr.toString())
                .putString("moodTimes", timeStr.toString())
                .apply();
    }

    private void loadMoodData() {
        String savedVals = preferences.getString("moodValues", "");
        String savedTimes = preferences.getString("moodTimes", "");

        moodValues.clear();
        timestamps.clear();

        if (!savedVals.isEmpty() && !savedTimes.isEmpty()) {
            String[] valArr = savedVals.split(",");
            String[] timeArr = savedTimes.split(",");

            int count = Math.min(valArr.length, timeArr.length);

            for (int i = 0; i < count; i++) {
                try {
                    moodValues.add(Integer.parseInt(valArr[i]));
                    timestamps.add(timeArr[i]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        moodChartView.setData(new ArrayList<>(moodValues), new ArrayList<>(timestamps));
        moodChartView.setOnPointSelectedListener(index -> {
            if (index >= 0 && index < moodValues.size()) {
                moodValues.remove(index);
                timestamps.remove(index);
                saveMoodData();
                loadMoodData();
            }
        });
    }
}