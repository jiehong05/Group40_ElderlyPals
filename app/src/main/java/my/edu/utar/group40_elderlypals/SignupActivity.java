package my.edu.utar.group40_elderlypals;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SignupActivity extends AppCompatActivity {

    private View v1, v2, v3, v4;
    private TextView tvStrengthText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        EditText etUsername = findViewById(R.id.et_signup_username);
        EditText etEmail = findViewById(R.id.et_signup_email);
        EditText etPassword = findViewById(R.id.et_signup_password);
        EditText etConfirmPassword = findViewById(R.id.et_signup_confirm_password);
        CheckBox cbTerms = findViewById(R.id.cb_terms);
        Button btnCreate = findViewById(R.id.btn_create);
        TextView tvLoginHere = findViewById(R.id.tv_login_here);

        // Initial state: grey
        btnCreate.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#9E9E9E")));

        TextWatcher signupWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String user = etUsername.getText().toString().trim();
                String email = etEmail.getText().toString().trim();
                String pass = etPassword.getText().toString().trim();
                String confirm = etConfirmPassword.getText().toString().trim();

                if (!user.isEmpty() && !email.isEmpty() && !pass.isEmpty() && !confirm.isEmpty()) {
                    btnCreate.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#2196F3")));
                } else {
                    btnCreate.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#9E9E9E")));
                }

                if (s.hashCode() == etPassword.getText().hashCode()) {
                    updatePasswordStrength(pass);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        etUsername.addTextChangedListener(signupWatcher);
        etEmail.addTextChangedListener(signupWatcher);
        etPassword.addTextChangedListener(signupWatcher);
        etConfirmPassword.addTextChangedListener(signupWatcher);

        btnCreate.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();

            if (TextUtils.isEmpty(username)) {
                etUsername.setError("Username is required");
                return;
            }

            if (TextUtils.isEmpty(email)) {
                etEmail.setError("Email is required");
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etEmail.setError("Please enter a valid email address");
                return;
            }

            if (calculateStrengthScore(password) < 4) {
                etPassword.setError("Password must be strong (Length 8+, Upper & Lower, Number, Symbol)");
                return;
            }

            if (!password.equals(confirmPassword)) {
                etConfirmPassword.setError("Passwords do not match");
                return;
            }

            if (!cbTerms.isChecked()) {
                Toast.makeText(this, "Please agree to the terms", Toast.LENGTH_SHORT).show();
                return;
            }

            // Save user credentials
            getSharedPreferences("UserPrefs", MODE_PRIVATE)
                    .edit()
                    .putString("registeredUsername", username)
                    .putString("registeredPassword", password)
                    .putString("userName", username)
                    .apply();

            Toast.makeText(this, "Account for " + username + " created!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        tvLoginHere.setOnClickListener(v -> {
            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private int calculateStrengthScore(String password) {
        int score = 0;
        if (password.length() >= 8) score++;
        if (password.matches("(?=.*[a-z])(?=.*[A-Z]).*")) score++;
        if (password.matches("(?=.*[0-9]).*")) score++;
        if (password.matches("(?=.*[~!@#$%^&*()_+\\-=\\[\\]{};':\",./<>?]).*")) score++;
        return score;
    }

    private void updatePasswordStrength(String password) {
        int score = calculateStrengthScore(password);
        
        v1.setBackgroundColor(Color.parseColor("#E0E0E0"));
        v2.setBackgroundColor(Color.parseColor("#E0E0E0"));
        v3.setBackgroundColor(Color.parseColor("#E0E0E0"));
        v4.setBackgroundColor(Color.parseColor("#E0E0E0"));

        if (score >= 1) v1.setBackgroundColor(Color.RED);
        if (score >= 2) v2.setBackgroundColor(Color.parseColor("#FFA500")); // Orange
        if (score >= 3) v3.setBackgroundColor(Color.YELLOW);
        if (score >= 4) v4.setBackgroundColor(Color.GREEN);

        switch (score) {
            case 0: tvStrengthText.setText("Strength: Too weak"); break;
            case 1: tvStrengthText.setText("Strength: Weak (Add Uppercase/Length)"); break;
            case 2: tvStrengthText.setText("Strength: Fair (Add Number)"); break;
            case 3: tvStrengthText.setText("Strength: Good (Add Symbol)"); break;
            case 4: tvStrengthText.setText("Strength: Strong"); break;
        }
    }
}
