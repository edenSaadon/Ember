package com.example.ember;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
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

import java.util.Calendar;

public class WelcomeActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private EditText nameInput, ageInput, hobbyInput, aboutYourselfInput, birthdateInput, partnerAgeRangeInput;
    private Spinner genderSpinner, sexualPreferenceSpinner, statusSpinner, lookingForSpinner, partnerGenderSpinner;
    private SeekBar partnerLocationSeekBar;
    private TextView partnerLocationTextView;
    private Button saveButton;

    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private double latitude;
    private double longitude;
    private String cityName;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            // Redirect to login activity if the user is not logged in
            startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
            finish();
            return;
        }

        initializeViews();
        setupSpinners();
        setupListeners();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        createLocationRequest();
        createLocationCallback();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            getLastKnownLocation();
        }
    }

    private void initializeViews() {
        nameInput = findViewById(R.id.input_name);
        ageInput = findViewById(R.id.input_age);
        hobbyInput = findViewById(R.id.input_hobby);
        aboutYourselfInput = findViewById(R.id.input_about_yourself);
        birthdateInput = findViewById(R.id.input_birthdate);
        partnerAgeRangeInput = findViewById(R.id.input_partner_age_range);
        genderSpinner = findViewById(R.id.spinner_gender);
        sexualPreferenceSpinner = findViewById(R.id.spinner_sexual_preference);
        statusSpinner = findViewById(R.id.spinner_status);
        lookingForSpinner = findViewById(R.id.spinner_looking_for);
        partnerGenderSpinner = findViewById(R.id.spinner_partner_gender);
        partnerLocationSeekBar = findViewById(R.id.seekBar_partner_location_range);
        partnerLocationTextView = findViewById(R.id.txt_partner_location_range_value);
        saveButton = findViewById(R.id.btn_save);
    }

    private void setupSpinners() {
        setupSpinner(genderSpinner, R.array.gender_options);
        setupSpinner(sexualPreferenceSpinner, R.array.sexual_preference_options);
        setupSpinner(statusSpinner, R.array.status_options);
        setupSpinner(lookingForSpinner, R.array.looking_for_options);
        setupSpinner(partnerGenderSpinner, R.array.partner_gender_options);
    }

    private void setupSpinner(Spinner spinner, int arrayResourceId) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                arrayResourceId, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void setupListeners() {
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

    private void createLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000); // 10 seconds
        locationRequest.setFastestInterval(5000); // 5 seconds
    }

    private void createLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null) {
                    for (Location location : locationResult.getLocations()) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        Log.d("WelcomeActivity", "Updated Location: " + latitude + ", " + longitude);
                    }
                }
            }
        };
    }

    @SuppressLint("MissingPermission")
    private void getLastKnownLocation() {
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        Log.d("WelcomeActivity", "Latitude: " + latitude + ", Longitude: " + longitude);
                    } else {
                        Log.e("WelcomeActivity", "Failed to get location. Requesting updates...");
                        startLocationUpdates();
                    }
                });
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
        }
    }

    private void saveUserData() {
        if (currentUser == null) {
            Log.e("WelcomeActivity", "No current user logged in");
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String name = nameInput.getText().toString();
        int age = Integer.parseInt(ageInput.getText().toString());
        String hobby = hobbyInput.getText().toString();
        String aboutYourself = aboutYourselfInput.getText().toString();
        String birthdate = birthdateInput.getText().toString();
        String selectedGender = genderSpinner.getSelectedItem().toString();
        String selectedSexualPreference = sexualPreferenceSpinner.getSelectedItem().toString();
        String selectedStatus = statusSpinner.getSelectedItem().toString();
        String selectedLookingFor = lookingForSpinner.getSelectedItem().toString();
        String partnerAgeRange = partnerAgeRangeInput.getText().toString();
        int partnerLocationRange = partnerLocationSeekBar.getProgress();
        String selectedPartnerGender = partnerGenderSpinner.getSelectedItem().toString();

        String userId = currentUser.getUid();
        String email = currentUser.getEmail();
        String phone = currentUser.getPhoneNumber();
        String imageUrl = ""; // ניתן להוסיף URL של תמונת פרופיל כאן אם יש

        User user = new User(userId, name, email, phone, selectedSexualPreference, selectedGender, age, hobby, selectedStatus, selectedLookingFor, birthdate, partnerAgeRange, String.valueOf(partnerLocationRange), selectedPartnerGender, aboutYourself, latitude, longitude, partnerLocationRange, imageUrl, cityName, 2);

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
                getLastKnownLocation();
            } else {
                Toast.makeText(this, "Location permission is required to use this app.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }
}
