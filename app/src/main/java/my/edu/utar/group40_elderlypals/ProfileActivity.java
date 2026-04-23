package my.edu.utar.group40_elderlypals;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;

public class ProfileActivity extends AppCompatActivity {

    private TextInputEditText etName, etPhone, etAddress;
    private SwitchMaterial switchHideInfo;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        preferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);

        etName = findViewById(R.id.et_name);
        etPhone = findViewById(R.id.et_phone);
        etAddress = findViewById(R.id.et_address);
        switchHideInfo = findViewById(R.id.switch_hide_info);
        Button btnSave = findViewById(R.id.btn_save_profile);

        // Load existing data
        etName.setText(preferences.getString("userName", ""));
        etPhone.setText(preferences.getString("userPhone", ""));
        etAddress.setText(preferences.getString("userAddress", ""));
        switchHideInfo.setChecked(preferences.getBoolean("hideInfo", false));

        findViewById(R.id.tv_back).setOnClickListener(v -> finish());

        btnSave.setOnClickListener(v -> saveProfile());
    }

    private void saveProfile() {
        String name = etName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        boolean hideInfo = switchHideInfo.isChecked();

        if (TextUtils.isEmpty(name)) {
            etName.setError("Name is required");
            return;
        }

        // Basic phone validation (e.g. 012-3456789)
        if (!phone.matches("\\d{3}-\\d{7,8}")) {
            etPhone.setError("Invalid format. Use 012-3456789");
            return;
        }

        if (TextUtils.isEmpty(address)) {
            etAddress.setError("Address is required");
            return;
        }

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("userName", name);
        editor.putString("userPhone", phone);
        editor.putString("userAddress", address);
        editor.putBoolean("hideInfo", hideInfo);
        editor.apply();

        Toast.makeText(this, "Profile saved successfully", Toast.LENGTH_SHORT).show();
        finish();
    }
}
