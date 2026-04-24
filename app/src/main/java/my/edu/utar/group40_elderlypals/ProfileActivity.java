package my.edu.utar.group40_elderlypals;

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

    private HealthVaultDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        db = HealthVaultDatabase.getInstance(this);

        etName = findViewById(R.id.et_name);
        etPhone = findViewById(R.id.et_phone);
        etAddress = findViewById(R.id.et_address);
        switchHideInfo = findViewById(R.id.switch_hide_info);
        Button btnSave = findViewById(R.id.btn_save_profile);

        loadProfileFromDb();

        findViewById(R.id.tv_back).setOnClickListener(v -> finish());

        btnSave.setOnClickListener(v -> saveProfile());
    }

    private void loadProfileFromDb() {
        HealthCard profile = db.healthCardDao().getHealthCard();

        if (profile != null) {
            etName.setText(profile.userName);
            etPhone.setText(profile.userPhone);
            etAddress.setText(profile.userAddress);
            switchHideInfo.setChecked(profile.hideInfo);
        }
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

        if (!phone.matches("\\d{3}-\\d{7,8}")) {
            etPhone.setError("Invalid format. Use 012-3456789");
            return;
        }

        if (TextUtils.isEmpty(address)) {
            etAddress.setError("Address is required");
            return;
        }

        HealthCard updatedCard = new HealthCard(name, phone, address, hideInfo);
        db.healthCardDao().insertOrUpdate(updatedCard);

        Toast.makeText(this, "Profile saved in Health Vault!", Toast.LENGTH_SHORT).show();
        finish();
    }
}
