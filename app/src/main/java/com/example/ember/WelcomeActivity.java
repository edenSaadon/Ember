package com.example.ember;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.ember.Models.User;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class WelcomeActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private EditText nameInput, ageInput, hobbyInput, aboutYourselfInput, birthdateInput;
    private Spinner genderSpinner, sexualPreferenceSpinner, statusSpinner, lookingForSpinner;
    private SeekBar partnerLocationSeekBar;
    private TextView partnerLocationTextView;
    private Button saveButton;

    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private double latitude;
    private double longitude;
    private String cityName; // משתנה לשם העיר

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        nameInput = findViewById(R.id.input_name);
        ageInput = findViewById(R.id.input_age);
        hobbyInput = findViewById(R.id.input_hobby);
        aboutYourselfInput = findViewById(R.id.input_about_yourself);
        birthdateInput = findViewById(R.id.input_birthdate);
        genderSpinner = findViewById(R.id.spinner_gender);
        sexualPreferenceSpinner = findViewById(R.id.spinner_sexual_preference);
        statusSpinner = findViewById(R.id.spinner_status);
        lookingForSpinner = findViewById(R.id.spinner_looking_for);
        partnerLocationSeekBar = findViewById(R.id.seekBar_partner_location_range);
        partnerLocationTextView = findViewById(R.id.txt_partner_location_range_value);
        saveButton = findViewById(R.id.btn_save);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        setupSpinners();
        checkLocationPermission();

        birthdateInput.setOnClickListener(v -> showDatePickerDialog());

        saveButton.setOnClickListener(view -> saveUserData());

        partnerLocationSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                partnerLocationTextView.setText(progress + " km");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Do something when the user starts to move the SeekBar
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Do something when the user stops moving the SeekBar
            }
        });

        createLocationRequest();
    }

    private void setupSpinners() {
        ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(this,
                R.array.gender_options, android.R.layout.simple_spinner_item);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(genderAdapter);

        ArrayAdapter<CharSequence> sexualPreferenceAdapter = ArrayAdapter.createFromResource(this,
                R.array.sexual_preference_options, android.R.layout.simple_spinner_item);
        sexualPreferenceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sexualPreferenceSpinner.setAdapter(sexualPreferenceAdapter);

        ArrayAdapter<CharSequence> statusAdapter = ArrayAdapter.createFromResource(this,
                R.array.status_options, android.R.layout.simple_spinner_item);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusSpinner.setAdapter(statusAdapter);

        ArrayAdapter<CharSequence> lookingForAdapter = ArrayAdapter.createFromResource(this,
                R.array.looking_for_options, android.R.layout.simple_spinner_item);
        lookingForAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        lookingForSpinner.setAdapter(lookingForAdapter);
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                WelcomeActivity.this,
                (view, year1, month1, dayOfMonth) -> birthdateInput.setText(dayOfMonth + "/" + (month1 + 1) + "/" + year1),
                year, month, day);
        datePickerDialog.show();
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            getLocation();
        }
    }

    private void createLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    cityName = getCityNameFromLocation(latitude, longitude); // קבלת שם העיר
                    Toast.makeText(WelcomeActivity.this, "מיקום נוכחי: " + cityName, Toast.LENGTH_SHORT).show();
                }
            }
        }, null);
    }

    private String getCityNameFromLocation(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (!addresses.isEmpty()) {
                return addresses.get(0).getLocality();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Unknown";
    }

    private void saveUserData() {
        String name = nameInput.getText().toString();
        int age = Integer.parseInt(ageInput.getText().toString());
        String hobby = hobbyInput.getText().toString();
        String aboutYourself = aboutYourselfInput.getText().toString();
        String birthdate = birthdateInput.getText().toString();
        String selectedGender = genderSpinner.getSelectedItem().toString();
        String selectedSexualPreference = sexualPreferenceSpinner.getSelectedItem().toString();
        String selectedStatus = statusSpinner.getSelectedItem().toString();
        String selectedLookingFor = lookingForSpinner.getSelectedItem().toString();
        int partnerAgeRange = 10; // Example value
        int partnerLocationRange = partnerLocationSeekBar.getProgress(); // Updated to use SeekBar value
        String selectedPartnerGender = "Any"; // Example value

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = currentUser.getUid();
        String email = currentUser.getEmail();
        String phone = currentUser.getPhoneNumber();
        String imageUrl = ""; // ניתן להוסיף URL של תמונת פרופיל כאן אם יש

        User user = new User(userId, name, email, phone, selectedSexualPreference, selectedGender, age, hobby, selectedStatus, selectedLookingFor, birthdate, String.valueOf(partnerAgeRange), String.valueOf(partnerLocationRange), selectedPartnerGender, aboutYourself, latitude, longitude, partnerLocationRange, imageUrl, cityName);

        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Users");
        usersRef.child(userId).setValue(user);

        Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            } else {
                Toast.makeText(this, "Location permission is required to use this app.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
