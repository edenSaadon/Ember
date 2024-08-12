package com.example.ember;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ember.Models.CircleTransform;
import com.example.ember.Models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";
    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView userImage;
    private TextView userName;
    private TextView userDetails;
    private Button editProfileButton;
    private Uri imageUri;
    private BottomNavigationView bottomNavigationView;

    private FirebaseUser currentUser;
    private StorageReference storageRef;
    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        userImage = findViewById(R.id.user_image);
        userName = findViewById(R.id.user_name);
        userDetails = findViewById(R.id.user_details);
        editProfileButton = findViewById(R.id.btn_edit_profile);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        storageRef = FirebaseStorage.getInstance().getReference("profile_images");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            userRef = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());
        }

        loadUserProfile();

        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                startActivity(new Intent(ProfileActivity.this, MainActivity.class));
                return true;
            } else if (itemId == R.id.navigation_profile) {
                return true;
            } else {
                return false;
            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            Picasso.get().load(imageUri).transform(new CircleTransform()).into(userImage);
            uploadFile();
        }
    }

    private void uploadFile() {
        if (imageUri != null && currentUser != null) {
            StorageReference fileReference = storageRef.child(currentUser.getUid() + ".jpg");

            fileReference.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String downloadUrl = uri.toString();
                                    userRef.child("imageUrl").setValue(downloadUrl);
                                    Toast.makeText(ProfileActivity.this, "Upload successful", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ProfileActivity.this, "Upload failed", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadUserProfile() {
        if (currentUser != null) {
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @SuppressLint("StaticFieldLeak")
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        // שימוש ב-GeocodingTask כדי לקבל שם עיר אם לא קיים
                        if (user.getCityName() == null || user.getCityName().isEmpty()) {
                            new com.example.ember.NominatimGeocodingTask() {
                                @Override
                                protected void onPostExecute(String cityName) {
                                    if (cityName != null) {
                                        user.setCityName(cityName);
                                        updateUI(user);
                                    } else {
                                        Log.e(TAG, "Failed to determine city");
                                    }
                                }
                            }.execute(user.getLatitude(), user.getLongitude());
                        } else {
                            updateUI(user);
                        }
                    } else {
                        Log.e(TAG, "User data is null");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e(TAG, "Failed to load user data", databaseError.toException());
                }
            });
        } else {
            Log.e(TAG, "Current user is null");
        }
    }

    private void updateUI(User user) {
        if (!user.getImageUrl().isEmpty()) {
            Picasso.get().load(user.getImageUrl()).transform(new CircleTransform()).into(userImage);
        }
        userName.setText(user.getName());

        StringBuilder details = new StringBuilder();
        details.append("Gender: ").append(user.getGender()).append("\n");
        details.append("Sexual Preference: ").append(user.getSexualPreference()).append("\n");
        details.append("Age: ").append(user.getAge()).append("\n");
        details.append("Hobby: ").append(user.getHobby()).append("\n");
        details.append("Status: ").append(user.getStatus()).append("\n");
        details.append("Looking For: ").append(user.getLookingFor()).append("\n");
        details.append("Birthdate: ").append(user.getBirthdate()).append("\n");
        details.append("Partner Age Range: ").append(user.getPartnerAgeRange()).append("\n");
        details.append("Partner Location Range: ").append(user.getPartnerLocationRange()).append("\n");
        details.append("Partner Gender: ").append(user.getPartnerGender()).append("\n");
        details.append("About Yourself: ").append(user.getAboutYourself()).append("\n");
        details.append("City: ").append(user.getCityName()).append("\n");
        details.append("Latitude: ").append(user.getLatitude()).append("\n");
        details.append("Longitude: ").append(user.getLongitude()).append("\n");

        userDetails.setText(details.toString());
    }
}
