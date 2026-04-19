package my.edu.utar.group40_elderlypals;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.textfield.TextInputEditText;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class MedicationActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MedicationAdapter adapter;
    private List<Medication> medicationList = new ArrayList<>();
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medication);

        preferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        recyclerView = findViewById(R.id.rv_medication);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadMedications();

        adapter = new MedicationAdapter(medicationList, new MedicationAdapter.OnMedicationClickListener() {
            @Override
            public void onEdit(int position) {
                showMedicationDialog(position);
            }

            @Override
            public void onDelete(int position) {
                medicationList.remove(position);
                saveMedications();
                adapter.notifyItemRemoved(position);
            }
        });
        recyclerView.setAdapter(adapter);

        findViewById(R.id.tv_back).setOnClickListener(v -> finish());
        findViewById(R.id.btn_add).setOnClickListener(v -> showMedicationDialog(-1));

        findViewById(R.id.tv_logout).setOnClickListener(v -> {
            preferences.edit().putBoolean("isLoggedIn", false).apply();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        findViewById(R.id.btn_mic).setOnClickListener(v -> 
            Toast.makeText(this, "Voice Assistant Coming Soon", Toast.LENGTH_SHORT).show()
        );
    }

    private void showMedicationDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_medication, null);
        builder.setView(dialogView);

        TextInputEditText etName = dialogView.findViewById(R.id.et_med_name);
        TextInputEditText etTime = dialogView.findViewById(R.id.et_med_time);
        RadioGroup rgColor = dialogView.findViewById(R.id.rg_color);

        if (position != -1) {
            Medication med = medicationList.get(position);
            etName.setText(med.name);
            etTime.setText(med.time);
            switch (med.color) {
                case "Yellow": rgColor.check(R.id.rb_yellow); break;
                case "Red": rgColor.check(R.id.rb_red); break;
                case "Green": rgColor.check(R.id.rb_green); break;
                case "Blue": rgColor.check(R.id.rb_blue); break;
            }
        }

        builder.setPositiveButton(position == -1 ? "Add" : "Update", (dialog, which) -> {
            String name = etName.getText().toString();
            String time = etTime.getText().toString();
            int checkedId = rgColor.getCheckedRadioButtonId();
            String color = "Blue";
            if (checkedId == R.id.rb_yellow) color = "Yellow";
            else if (checkedId == R.id.rb_red) color = "Red";
            else if (checkedId == R.id.rb_green) color = "Green";

            if (!name.isEmpty() && !time.isEmpty()) {
                if (position == -1) {
                    medicationList.add(new Medication(name, time, color));
                    adapter.notifyItemInserted(medicationList.size() - 1);
                } else {
                    Medication med = medicationList.get(position);
                    med.name = name;
                    med.time = time;
                    med.color = color;
                    adapter.notifyItemChanged(position);
                }
                saveMedications();
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void saveMedications() {
        JSONArray jsonArray = new JSONArray();
        try {
            for (Medication med : medicationList) {
                JSONObject obj = new JSONObject();
                obj.put("name", med.name);
                obj.put("time", med.time);
                obj.put("color", med.color);
                jsonArray.put(obj);
            }
            preferences.edit().putString("medications", jsonArray.toString()).apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void loadMedications() {
        String json = preferences.getString("medications", "[]");
        try {
            JSONArray jsonArray = new JSONArray(json);
            medicationList.clear();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                medicationList.add(new Medication(
                        obj.getString("name"),
                        obj.getString("time"),
                        obj.getString("color")
                ));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    static class Medication {
        String name, time, color;
        Medication(String name, String time, String color) {
            this.name = name;
            this.time = time;
            this.color = color;
        }
    }

    static class MedicationAdapter extends RecyclerView.Adapter<MedicationAdapter.ViewHolder> {
        private List<Medication> list;
        private OnMedicationClickListener listener;

        interface OnMedicationClickListener {
            void onEdit(int position);
            void onDelete(int position);
        }

        MedicationAdapter(List<Medication> list, OnMedicationClickListener listener) {
            this.list = list;
            this.listener = listener;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_medication, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Medication med = list.get(position);
            holder.tvName.setText(med.name);
            holder.tvTime.setText(med.time);
            
            int color;
            switch (med.color) {
                case "Yellow": color = Color.YELLOW; break;
                case "Red": color = Color.RED; break;
                case "Green": color = Color.GREEN; break;
                default: color = Color.BLUE; break;
            }
            holder.viewColor.setBackgroundColor(color);

            // Hover-like effect for icons/buttons
            holder.viewColor.setOnHoverListener((v, event) -> {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_HOVER_ENTER:
                        v.animate().scaleX(1.5f).scaleY(1.5f).setDuration(200).start();
                        // Play sound twice to make it "louder"
                        v.playSoundEffect(SoundEffectConstants.CLICK);
                        v.postDelayed(() -> v.playSoundEffect(SoundEffectConstants.CLICK), 50);
                        break;
                    case MotionEvent.ACTION_HOVER_EXIT:
                        v.postDelayed(() -> v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(200).start(), 500);
                        break;
                }
                return false;
            });

            holder.btnEdit.setOnClickListener(v -> listener.onEdit(position));
            holder.btnDelete.setOnClickListener(v -> listener.onDelete(position));
        }

        @Override
        public int getItemCount() { return list.size(); }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvName, tvTime;
            View viewColor;
            ImageButton btnEdit, btnDelete;

            ViewHolder(View itemView) {
                super(itemView);
                tvName = itemView.findViewById(R.id.tv_med_name);
                tvTime = itemView.findViewById(R.id.tv_med_time);
                viewColor = itemView.findViewById(R.id.view_color_dot);
                btnEdit = itemView.findViewById(R.id.btn_edit);
                btnDelete = itemView.findViewById(R.id.btn_delete);
            }
        }
    }
}
