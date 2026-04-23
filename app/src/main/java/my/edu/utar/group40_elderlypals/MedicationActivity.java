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
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class MedicationActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MedicationAdapter adapter;
    private List<Medication> medicationList = new ArrayList<>();
    private TextView tvAlertZoneMed;

    private HealthVaultDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medication);

        db = HealthVaultDatabase.getInstance(this);

        tvAlertZoneMed = findViewById(R.id.tv_alert_zone_med);
        recyclerView = findViewById(R.id.rv_medication);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        refreshList();

        adapter = new MedicationAdapter(medicationList, new MedicationAdapter.OnMedicationClickListener() {
            @Override
            public void onEdit(int position) {
                showMedicationDialog(position);
            }

            @Override
            public void onDelete(int position) {
                // CRUD: Delete - 从数据库删除
                db.medicationDao().delete(medicationList.get(position));
                refreshList(); // 刷新列表
            }
        });
        recyclerView.setAdapter(adapter);

        findViewById(R.id.tv_back).setOnClickListener(v -> finish());
        findViewById(R.id.btn_add).setOnClickListener(v -> showMedicationDialog(-1));

        findViewById(R.id.tv_logout).setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
            prefs.edit().putBoolean("isLoggedIn", false).apply();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        findViewById(R.id.btn_mic).setOnClickListener(v -> {
            Intent intent = new Intent(MedicationActivity.this, VoiceAssistantActivity.class);
            startActivity(intent);
        });
    }

    private void refreshList() {
        medicationList.clear();
        // CRUD: Read - 从数据库获取所有药
        medicationList.addAll(db.medicationDao().getAllMedications());
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
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
            if (med.color != null) {
                switch (med.color) {
                    case "Yellow": rgColor.check(R.id.rb_yellow); break;
                    case "Red": rgColor.check(R.id.rb_red); break;
                    case "Green": rgColor.check(R.id.rb_green); break;
                    case "Blue": rgColor.check(R.id.rb_blue); break;
                }
            }
        }

        builder.setPositiveButton(position == -1 ? "Add" : "Update", (dialog, which) -> {
            String name = etName.getText().toString().trim();
            String time = etTime.getText().toString().trim();

            int checkedId = rgColor.getCheckedRadioButtonId();
            String color = "Blue";
            if (checkedId == R.id.rb_yellow) color = "Yellow";
            else if (checkedId == R.id.rb_red) color = "Red";
            else if (checkedId == R.id.rb_green) color = "Green";

            if (!name.isEmpty() && !time.isEmpty()) {
                if (position == -1) {
                    db.medicationDao().insert(new Medication(name, time, color));
                } else {
                    Medication med = medicationList.get(position);
                    med.name = name;
                    med.time = time;
                    med.color = color;
                    db.medicationDao().update(med);
                }
                refreshList();
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
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

            int colorCode;
            switch (med.color != null ? med.color : "Blue") {
                case "Yellow": colorCode = Color.YELLOW; break;
                case "Red": colorCode = Color.RED; break;
                case "Green": colorCode = Color.GREEN; break;
                default: colorCode = Color.BLUE; break;
            }
            holder.viewColor.setBackgroundColor(colorCode);

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