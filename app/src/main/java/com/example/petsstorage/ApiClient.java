package com.example.petsstorage;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ApiClient {
    private static final String TAG = "ApiClient";
    private RequestQueue requestQueue;
    private Context context;

    public ApiClient(Context context) {
        this.context = context;
        this.requestQueue = Volley.newRequestQueue(context);
    }

    private String getBaseUrl() {
        return StorageManager.loadServerIp(context);
    }

    private String getToken() {
        UserData user = StorageManager.loadUserData(context);
        return user != null ? user.getToken() : null;
    }

    public void login(JSONObject loginData, final ApiCallback callback) {
        String url = getBaseUrl() + "/login";
        Log.d(TAG, "Вход: " + url);

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                loginData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "Успешный вход");
                        callback.onSuccess(response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errorMsg = "Ошибка входа: " + error.toString();
                        Log.e(TAG, errorMsg);
                        callback.onError(errorMsg);
                    }
                }
        );

        requestQueue.add(request);
    }

    public void signUp(JSONObject signUpData, final ApiCallback callback) {
        String url = getBaseUrl() + "/sign_up";
        Log.d(TAG, "Регистрация: " + url);

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                signUpData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "Успешная регистрация");
                        callback.onSuccess(response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errorMsg = "Ошибка регистрации: " + error.toString();
                        Log.e(TAG, errorMsg);
                        callback.onError(errorMsg);
                    }
                }
        );

        requestQueue.add(request);
    }

    public void getDeliveries(final ApiCallback callback) {
        String url = getBaseUrl() + "/select_delivery_with_ids";
        Log.d(TAG, "Запрос поставок: " + url);

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "Успешный ответ");
                        callback.onSuccess(response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errorMsg = "Ошибка загрузки поставок: " + error.toString();
                        Log.e(TAG, errorMsg);
                        callback.onError(errorMsg);
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                String token = getToken();
                if (token != null) {
                    headers.put("Authorization", "Token " + token);
                }
                return headers;
            }
        };

        requestQueue.add(request);
    }

    public void getActiveProducts(final ApiCallback callback) {
        String url = getBaseUrl() + "/select_active_with_ids";
        Log.d(TAG, "Запрос активных товаров: " + url);

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "Успешный ответ");
                        callback.onSuccess(response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errorMsg = "Ошибка загрузки товаров: " + error.toString();
                        Log.e(TAG, errorMsg);
                        callback.onError(errorMsg);
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                String token = getToken();
                if (token != null) {
                    headers.put("Authorization", "Token " + token);
                }
                return headers;
            }
        };

        requestQueue.add(request);
    }

    public void changeStatus(JSONArray items, final ApiCallback callback) {
        String url = getBaseUrl() + "/change_status";
        Log.d(TAG, "Отправка статусов: " + url);

        try {
            JSONObject body = new JSONObject();
            body.put("products", items);

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    body,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d(TAG, "Успешный ответ");
                            callback.onSuccess(response.toString());
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            String errorMsg = "Ошибка изменения статуса: " + error.toString();
                            Log.e(TAG, errorMsg);
                            callback.onError(errorMsg);
                        }
                    }
            ) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    String token = getToken();
                    if (token != null) {
                        headers.put("Authorization", "Token " + token);
                    }
                    return headers;
                }
            };

            requestQueue.add(request);

        } catch (JSONException e) {
            Log.e(TAG, "Ошибка создания JSON: " + e.getMessage());
            callback.onError("Ошибка формирования запроса");
        }
    }

    public void logout(final ApiCallback callback) {
        String url = getBaseUrl() + "/logout";
        Log.d(TAG, "Выход: " + url);

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "Успешный выход");
                        StorageManager.clearUserData(context);
                        callback.onSuccess("Выход выполнен");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errorMsg = "Ошибка выхода: " + error.toString();
                        Log.e(TAG, errorMsg);
                        callback.onError(errorMsg);
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                String token = getToken();
                if (token != null) {
                    headers.put("Authorization", "Token " + token);
                }
                return headers;
            }
        };

        requestQueue.add(request);
    }

    public interface ApiCallback {
        void onSuccess(String response);
        void onError(String error);
    }
}