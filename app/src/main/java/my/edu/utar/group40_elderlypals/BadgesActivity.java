package my.edu.utar.group40_elderlypals;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class BadgesActivity extends AppCompatActivity {

    private SharedPreferences preferences;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private SimpleDateFormat displaySdf = new SimpleDateFormat("MMM d, yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_badges);

        preferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        TextView tvBack = findViewById(R.id.tv_back);
        TextView tvLogout = findViewById(R.id.tv_logout);

        tvBack.setOnClickListener(v -> finish());

        tvLogout.setOnClickListener(v -> {
            preferences.edit().putBoolean("isLoggedIn", false).apply();
            Intent intent = new Intent(BadgesActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        loadBadges();
    }

    private void loadBadges() {
        String recordsJson = preferences.getString("medication_records_list", "[]");
        List<String> recordsList = new ArrayList<>();
        
        try {
            JSONArray array = new JSONArray(recordsJson);
            for (int i = 0; i < array.length(); i++) {
                recordsList.add(array.getString(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        
        // Sort dates in descending order (newest first)
        Collections.sort(recordsList, (o1, o2) -> o2.compareTo(o1));

        int totalTrophies = recordsList.size();
        TextView tvTotalTrophies = findViewById(R.id.tv_total_trophies);
        tvTotalTrophies.setText(String.format(Locale.getDefault(), "Total Trophies: %d", totalTrophies));

        // Update Main Achievement (Biggest trophy)
        TextView tvMainDate = findViewById(R.id.tv_main_date);
        if (totalTrophies > 0) {
            tvMainDate.setText(formatDate(recordsList.get(0)));
        } else {
            tvMainDate.setText("none");
        }

        // Update Milestone dates (from left to right)
        int[] milestoneIds = {
                R.id.tv_date_2, R.id.tv_date_3, R.id.tv_date_4, R.id.tv_date_5,
                R.id.tv_date_6, R.id.tv_date_7, R.id.tv_date_8, R.id.tv_date_9,
                R.id.tv_date_10
        };

        for (int i = 0; i < milestoneIds.length; i++) {
            TextView tvDate = findViewById(milestoneIds[i]);
            if (i + 1 < totalTrophies) {
                tvDate.setText(formatDate(recordsList.get(i + 1)));
            } else {
                tvDate.setText("none");
            }
        }
    }

    private String formatDate(String dateStr) {
        try {
            Date date = sdf.parse(dateStr);
            if (date != null) {
                return displaySdf.format(date);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateStr;
    }
}
