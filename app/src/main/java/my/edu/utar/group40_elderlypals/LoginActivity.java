package my.edu.utar.group40_elderlypals;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditText etUsername = findViewById(R.id.et_login_username);
        EditText etPassword = findViewById(R.id.et_login_password);
        Button btnLogin = findViewById(R.id.btn_login);
        TextView tvRegisterHere = findViewById(R.id.tv_register_here);

        // Initial state: grey
        btnLogin.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#9E9E9E")));

        TextWatcher loginWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String user = etUsername.getText().toString().trim();
                String pass = etPassword.getText().toString().trim();

                if (!user.isEmpty() && !pass.isEmpty()) {
                    btnLogin.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#2196F3")));
                } else {
                    btnLogin.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#9E9E9E")));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        etUsername.addTextChangedListener(loginWatcher);
        etPassword.addTextChangedListener(loginWatcher);

        btnLogin.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            String registeredUser = prefs.getString("registeredUsername", "");
            String registeredPass = prefs.getString("registeredPassword", "");

            if ((username.equals("admin") && password.equals("Admin@12345")) ||
                (username.equals(registeredUser) && password.equals(registeredPass) && !registeredUser.isEmpty())) {
                
                prefs.edit()
                        .putBoolean("isLoggedIn", true)
                        .apply();

                Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
                startActivity(intent);
                finish();
            } else {
                if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
                    Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Incorrect username or password", Toast.LENGTH_SHORT).show();
                }
            }
        });

        tvRegisterHere.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(intent);
        });
    }
}
