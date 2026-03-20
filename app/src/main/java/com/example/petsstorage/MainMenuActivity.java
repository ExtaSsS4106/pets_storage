package com.example.petsstorage;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainMenuActivity extends AppCompatActivity {

    private Button btnReceive, btnStorage, btnLogin, btnSettings, btnLogout;
    private ApiClient apiClient;
    private TextView tvUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        btnReceive = findViewById(R.id.btnReceive);
        btnStorage = findViewById(R.id.btnStorage);
        btnLogin = findViewById(R.id.btnLogin);
        btnSettings = findViewById(R.id.btnSettings);
        btnLogout = findViewById(R.id.btnLogout);
        tvUserName = findViewById(R.id.tvUserName);

        apiClient = new ApiClient(this);

        UserData user = StorageManager.loadUserData(this);
        if (user != null && tvUserName != null) {
            tvUserName.setText(user.getFullName());
            tvUserName.setVisibility(View.VISIBLE);
        }

        btnReceive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainMenuActivity.this, ReceiveActivity.class);
                startActivity(intent);
            }
        });

        btnStorage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainMenuActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainMenuActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainMenuActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
    }

    private void logout() {
        UserData userData = StorageManager.loadUserData(this);

        if (userData == null) {
            Toast.makeText(this, "Вы не авторизованы", Toast.LENGTH_SHORT).show();
            return;
        }

        apiClient.logout(new ApiClient.ApiCallback() {
            @Override
            public void onSuccess(String response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainMenuActivity.this,
                                "Выход выполнен", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainMenuActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainMenuActivity.this,
                                "Ошибка выхода: " + error, Toast.LENGTH_SHORT).show();
                        StorageManager.clearUserData(MainMenuActivity.this);
                    }
                });
            }
        });
    }
}