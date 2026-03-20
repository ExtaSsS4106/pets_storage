package com.example.petsstorage;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText searchEditText;
    private LinearLayout productsContainer;
    private TextView btnBack;
    private ApiClient apiClient;
    private List<ActiveProduct> allProducts;
    private List<ActiveProduct> filteredProducts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchEditText = findViewById(R.id.searchEditText);
        productsContainer = findViewById(R.id.productsContainer);
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
        allProducts = new ArrayList<>();
        filteredProducts = new ArrayList<>();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        loadActiveProducts();

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterProducts(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void loadActiveProducts() {
        apiClient.getActiveProducts(new ApiClient.ApiCallback() {
            @Override
            public void onSuccess(String response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        parseActiveProducts(response);
                    }
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "Ошибка загрузки: " + error, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void parseActiveProducts(String jsonResponse) {
        try {
            JSONObject root = new JSONObject(jsonResponse);
            JSONObject data = root.getJSONObject("data");
            JSONArray groupedData = data.getJSONArray("grouped_data");

            allProducts.clear();

            for (int i = 0; i < groupedData.length(); i++) {
                JSONObject item = groupedData.getJSONObject(i);

                ActiveProduct product = new ActiveProduct(
                        item.getString("name"),
                        item.getString("manufacturer"),
                        item.getString("category"),
                        item.getString("animal_type"),
                        item.getString("expiry_date"),
                        item.getString("delivery_date"),
                        item.getInt("total_quantity")
                );
                allProducts.add(product);
            }

            displayProducts(allProducts);

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Ошибка обработки данных", Toast.LENGTH_SHORT).show();
        }
    }

    private void displayProducts(List<ActiveProduct> products) {
        productsContainer.removeAllViews();

        for (ActiveProduct product : products) {
            LinearLayout productLayout = new LinearLayout(this);
            productLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            productLayout.setOrientation(LinearLayout.HORIZONTAL);
            productLayout.setPadding(12, 12, 12, 12);
            productLayout.setBackgroundResource(R.drawable.product_card_bg);

            TextView tvIcon = new TextView(this);
            tvIcon.setText("📦");
            tvIcon.setTextSize(24);
            tvIcon.setPadding(0, 0, 12, 0);

            LinearLayout rightLayout = new LinearLayout(this);
            rightLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
            rightLayout.setOrientation(LinearLayout.VERTICAL);

            TextView tvName = new TextView(this);
            tvName.setText(product.name + " (" + product.manufacturer + ")");
            tvName.setTextSize(16);
            tvName.setTypeface(null, android.graphics.Typeface.BOLD);
            tvName.setTextColor(0xFF333333);
            rightLayout.addView(tvName);

            LinearLayout rowLayout = new LinearLayout(this);
            rowLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            rowLayout.setOrientation(LinearLayout.HORIZONTAL);
            rowLayout.setPadding(0, 4, 0, 0);

            TextView tvExpiry = new TextView(this);
            tvExpiry.setLayoutParams(new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
            tvExpiry.setText("Годен до: " + product.expiryDate);
            tvExpiry.setTextSize(14);
            tvExpiry.setTextColor(0xFF666666);

            TextView tvQuantity = new TextView(this);
            tvQuantity.setText(product.totalQuantity + " шт");
            tvQuantity.setTextSize(14);
            tvQuantity.setTextColor(0xFF666666);

            rowLayout.addView(tvExpiry);
            rowLayout.addView(tvQuantity);
            rightLayout.addView(rowLayout);

            TextView tvDetails = new TextView(this);
            tvDetails.setText(product.category + " | " + product.animalType +
                    " | Привоз: " + product.deliveryDate);
            tvDetails.setTextSize(14);
            tvDetails.setTextColor(0xFF666666);
            rightLayout.addView(tvDetails);

            productLayout.addView(tvIcon);
            productLayout.addView(rightLayout);
            productsContainer.addView(productLayout);

            View divider = new View(this);
            divider.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 1));
            divider.setBackgroundColor(0xFFEEEEEE);
            productsContainer.addView(divider);
        }
    }

    private void filterProducts(String query) {
        filteredProducts.clear();
        if (query.isEmpty()) {
            displayProducts(allProducts);
            return;
        }

        for (ActiveProduct product : allProducts) {
            if (product.name.toLowerCase().contains(query.toLowerCase())) {
                filteredProducts.add(product);
            }
        }
        displayProducts(filteredProducts);
    }

    private class ActiveProduct {
        String name, manufacturer, category, animalType, expiryDate, deliveryDate;
        int totalQuantity;

        ActiveProduct(String name, String manufacturer, String category, String animalType,
                      String expiryDate, String deliveryDate, int totalQuantity) {
            this.name = name;
            this.manufacturer = manufacturer;
            this.category = category;
            this.animalType = animalType;
            this.expiryDate = expiryDate;
            this.deliveryDate = deliveryDate;
            this.totalQuantity = totalQuantity;
        }
    }
}