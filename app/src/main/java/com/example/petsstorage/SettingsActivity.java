package com.example.petsstorage;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    private EditText ipEditText;
    private Button saveButton;
    private TextView btnBack, currentIpText, tvUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ipEditText = findViewById(R.id.ipEditText);
        saveButton = findViewById(R.id.saveButton);
        btnBack = findViewById(R.id.btnBack);
        currentIpText = findViewById(R.id.currentIpText);
        tvUserName = findViewById(R.id.tvUserName);

        UserData user = StorageManager.loadUserData(this);
        if (user != null && tvUserName != null) {
            tvUserName.setText(user.getFullName());
            tvUserName.setVisibility(View.VISIBLE);
        }

        String savedIp = StorageManager.loadServerIp(this);
        ipEditText.setText(savedIp);
        currentIpText.setText("Текущий IP: " + savedIp);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newIp = ipEditText.getText().toString().trim();

                if (newIp.isEmpty()) {
                    Toast.makeText(SettingsActivity.this,
                            "Введите IP адрес", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!newIp.startsWith("http://") && !newIp.startsWith("https://")) {
                    newIp = "http://" + newIp;
                }

                StorageManager.saveServerIp(SettingsActivity.this, newIp);
                currentIpText.setText("Текущий IP: " + newIp);

                Toast.makeText(SettingsActivity.this,
                        "IP сохранен", Toast.LENGTH_SHORT).show();
            }
        });
    }
}