package com.example.ember;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class CityDisplayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_display);

        TextView cityNameTextView = findViewById(R.id.cityNameTextView);
        String cityName = getIntent().getStringExtra("CITY_NAME");
        if (cityName != null) {
            cityNameTextView.setText(cityName);
        }
    }
}
