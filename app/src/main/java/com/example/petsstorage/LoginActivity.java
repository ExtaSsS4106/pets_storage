package com.example.petsstorage;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private Button loginButton;
    private TextView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this,
                            "Заполните все поля", Toast.LENGTH_SHORT).show();
                    return;
                }
                performLogin(email, password);
            }
        });
    }

    private void performLogin(String email, String password) {
        loginButton.setEnabled(false);
        loginButton.setText("ВХОД...");

        try {
            JSONObject loginData = new JSONObject();
            loginData.put("email", email);
            loginData.put("password", password);

            ApiClient apiClient = new ApiClient(this);
            apiClient.login(loginData, new ApiClient.ApiCallback() {
                @Override
                public void onSuccess(String response) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject jsonResponse = new JSONObject(response);
                                String token = jsonResponse.getString("token");
                                JSONObject user = jsonResponse.getJSONObject("user");

                                String fullName = user.getString("full_name");
                                String userEmail = user.getString("email");
                                String position = user.getString("position");

                                StorageManager.saveUserData(
                                        LoginActivity.this,
                                        token,
                                        fullName,
                                        userEmail,
                                        position
                                );

                                Toast.makeText(LoginActivity.this,
                                        "Добро пожаловать, " + fullName + "!",
                                        Toast.LENGTH_SHORT).show();

                                // Переходим на главный экран
                                Intent intent = new Intent(LoginActivity.this, MainMenuActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();

                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(LoginActivity.this,
                                        "Ошибка обработки ответа", Toast.LENGTH_SHORT).show();
                                resetLoginButton();
                            }
                        }
                    });
                }

                @Override
                public void onError(String error) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LoginActivity.this,
                                    "Ошибка входа: " + error, Toast.LENGTH_SHORT).show();
                            resetLoginButton();
                        }
                    });
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Ошибка формирования запроса", Toast.LENGTH_SHORT).show();
            resetLoginButton();
        }
    }

    private void resetLoginButton() {
        loginButton.setEnabled(true);
        loginButton.setText("ВОЙТИ");
    }
}