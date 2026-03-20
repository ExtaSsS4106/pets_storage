package com.example.petsstorage;

import android.content.Context;
import android.util.Log;
import org.json.JSONObject;
import org.json.JSONException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class StorageManager {
    private static final String USER_FILE = "user_data.json";
    private static final String SETTINGS_FILE = "settings.json";


    public static void saveUserData(Context context, String token, String fullName,
                                    String email, String position) {
        try {
            JSONObject json = new JSONObject();
            json.put("token", token);
            json.put("full_name", fullName);
            json.put("email", email);
            json.put("position", position);

            FileOutputStream fos = context.openFileOutput(USER_FILE, Context.MODE_PRIVATE);
            fos.write(json.toString().getBytes());
            fos.close();
            Log.d("Storage", "Данные пользователя сохранены");
        } catch (JSONException | IOException e) {
            Log.e("Storage", "Ошибка сохранения", e);
        }
    }

    public static UserData loadUserData(Context context) {
        try {
            FileInputStream fis = context.openFileInput(USER_FILE);
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            fis.close();

            String jsonStr = new String(buffer);
            JSONObject json = new JSONObject(jsonStr);

            return new UserData(
                    json.getString("token"),
                    json.getString("full_name"),
                    json.getString("email"),
                    json.getString("position")
            );
        } catch (Exception e) {
            return null;
        }
    }

    public static void clearUserData(Context context) {
        context.deleteFile(USER_FILE);
        Log.d("Storage", "Данные пользователя удалены");
    }

    public static void saveServerIp(Context context, String ip) {
        try {
            JSONObject json = new JSONObject();
            json.put("server_ip", ip);

            FileOutputStream fos = context.openFileOutput(SETTINGS_FILE, Context.MODE_PRIVATE);
            fos.write(json.toString().getBytes());
            fos.close();
            Log.d("Storage", "IP сохранен: " + ip);
        } catch (JSONException | IOException e) {
            Log.e("Storage", "Ошибка сохранения IP", e);
        }
    }

    public static String loadServerIp(Context context) {
        try {
            FileInputStream fis = context.openFileInput(SETTINGS_FILE);
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            fis.close();

            String jsonStr = new String(buffer);
            JSONObject json = new JSONObject(jsonStr);

            return json.getString("server_ip");
        } catch (Exception e) {

            return "http://10.0.2.2:8000";
        }
    }
}