package com.example.petsstorage;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class ReceiveActivity extends AppCompatActivity {

    private LinearLayout productsContainer;
    private Button btnSubmit;
    private TextView btnBack;
    private ApiClient apiClient;
    private List<DeliveryItem> deliveryItems;
    private List<Boolean> checkedStatus;
    private List<Integer> allSelectedIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive);

        productsContainer = findViewById(R.id.productsContainer);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnBack = findViewById(R.id.btnBack);

        UserData user = StorageManager.loadUserData(this);
        if (user != null) {
            TextView tvUserName = findViewById(R.id.tvUserName);
            if (tvUserName != null) {
                tvUserName.setText(user.getFullName());
                tvUserName.setVisibility(View.VISIBLE);
            }
        }

        apiClient = new ApiClient(this);
        deliveryItems = new ArrayList<>();
        checkedStatus = new ArrayList<>();
        allSelectedIds = new ArrayList<>();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        loadDeliveriesFromApi();

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitSelectedItems();
            }
        });
    }

    private void loadDeliveriesFromApi() {
        apiClient.getDeliveries(new ApiClient.ApiCallback() {
            @Override
            public void onSuccess(String response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        parseDeliveries(response);
                    }
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ReceiveActivity.this,
                                "Ошибка загрузки: " + error,
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void parseDeliveries(String jsonResponse) {
        try {
            JSONObject root = new JSONObject(jsonResponse);
            JSONObject data = root.getJSONObject("data");
            JSONArray groupedData = data.getJSONArray("grouped_data");

            deliveryItems.clear();
            checkedStatus.clear();

            for (int i = 0; i < groupedData.length(); i++) {
                JSONObject item = groupedData.getJSONObject(i);

                String name = item.getString("name");
                String manufacturer = item.getString("manufacturer");
                String category = item.getString("category");
                String animalType = item.getString("animal_type");
                String expiryDate = item.getString("expiry_date");
                String deliveryDate = item.getString("delivery_date");
                int totalQuantity = item.getInt("total_quantity");

                JSONArray ids = item.getJSONArray("product_ids");
                List<Integer> productIds = new ArrayList<>();
                for (int j = 0; j < ids.length(); j++) {
                    productIds.add(ids.getInt(j));
                }

                DeliveryItem deliveryItem = new DeliveryItem(
                        name, manufacturer, category, animalType,
                        expiryDate, deliveryDate, totalQuantity, productIds
                );
                deliveryItems.add(deliveryItem);
                checkedStatus.add(false);
            }

            displayDeliveries();

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Ошибка обработки данных", Toast.LENGTH_SHORT).show();
        }
    }

    private void displayDeliveries() {
        productsContainer.removeAllViews();

        for (int i = 0; i < deliveryItems.size(); i++) {
            final int position = i;
            DeliveryItem item = deliveryItems.get(i);

            LinearLayout productLayout = new LinearLayout(this);
            productLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            productLayout.setOrientation(LinearLayout.HORIZONTAL);
            productLayout.setPadding(16, 12, 16, 12);
            productLayout.setBackgroundResource(R.drawable.product_card_bg);
            productLayout.setClickable(true);
            productLayout.setGravity(android.view.Gravity.CENTER_VERTICAL);

            final TextView checkBox = new TextView(this);
            checkBox.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            checkBox.setText(checkedStatus.get(i) ? "✅" : "⬜");
            checkBox.setTextSize(28);
            checkBox.setPadding(0, 0, 16, 0);

            LinearLayout rightLayout = new LinearLayout(this);
            rightLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
            rightLayout.setOrientation(LinearLayout.VERTICAL);

            TextView nameText = new TextView(this);
            nameText.setText(item.name);
            nameText.setTextSize(16);
            nameText.setTypeface(null, android.graphics.Typeface.BOLD);
            nameText.setTextColor(0xFF333333);
            rightLayout.addView(nameText);

            TextView detailsText = new TextView(this);
            detailsText.setText(item.category + " | " + item.animalType +
                    " | Годен: " + item.expiryDate);
            detailsText.setTextSize(14);
            detailsText.setTextColor(0xFF666666);
            detailsText.setPadding(0, 4, 0, 0);
            rightLayout.addView(detailsText);


            TextView quantityText = new TextView(this);
            quantityText.setText("Ожидается: " + item.totalQuantity + " шт");
            quantityText.setTextSize(14);
            quantityText.setTextColor(0xFF666666);
            quantityText.setPadding(0, 4, 0, 0);
            rightLayout.addView(quantityText);

            productLayout.addView(checkBox);
            productLayout.addView(rightLayout);
            productsContainer.addView(productLayout);

            View divider = new View(this);
            divider.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 1));
            divider.setBackgroundColor(0xFFEEEEEE);
            productsContainer.addView(divider);

            final int pos = i;
            productLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkedStatus.set(pos, !checkedStatus.get(pos));
                    checkBox.setText(checkedStatus.get(pos) ? "✅" : "⬜");
                }
            });
        }
    }

    private void submitSelectedItems() {
        try {
            JSONArray itemsToSubmit = new JSONArray();
            allSelectedIds.clear();

            for (int i = 0; i < deliveryItems.size(); i++) {
                if (checkedStatus.get(i)) {
                    DeliveryItem item = deliveryItems.get(i);
                    for (Integer id : item.productIds) {
                        JSONObject product = new JSONObject();
                        product.put("id", id);
                        product.put("status", "exists");
                        itemsToSubmit.put(product);
                        allSelectedIds.add(id);
                    }
                }
            }

            if (itemsToSubmit.length() == 0) {
                Toast.makeText(this, "Выберите товары для приема", Toast.LENGTH_SHORT).show();
                return;
            }

            apiClient.changeStatus(itemsToSubmit, new ApiClient.ApiCallback() {
                @Override
                public void onSuccess(String response) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ReceiveActivity.this,
                                    "Принято товаров: " + allSelectedIds.size(),
                                    Toast.LENGTH_SHORT).show();
                            loadDeliveriesFromApi();
                        }
                    });
                }

                @Override
                public void onError(String error) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ReceiveActivity.this,
                                    "Ошибка: " + error,
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Ошибка формирования запроса", Toast.LENGTH_SHORT).show();
        }
    }

    private class DeliveryItem {
        String name, manufacturer, category, animalType, expiryDate, deliveryDate;
        int totalQuantity;
        List<Integer> productIds;

        DeliveryItem(String name, String manufacturer, String category, String animalType,
                     String expiryDate, String deliveryDate, int totalQuantity, List<Integer> productIds) {
            this.name = name;
            this.manufacturer = manufacturer;
            this.category = category;
            this.animalType = animalType;
            this.expiryDate = expiryDate;
            this.deliveryDate = deliveryDate;
            this.totalQuantity = totalQuantity;
            this.productIds = productIds;
        }
    }
}