package com.example.ember.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ember.Models.User;
import com.example.ember.R;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.List;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private Button buttonSignUp;
    private Button buttonExistingUserLogin;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        buttonSignUp = findViewById(R.id.buttonSignUp);
        buttonExistingUserLogin = findViewById(R.id.buttonExistingUserLogin);

        // Animation setup
        TextView letterE = findViewById(R.id.letterE);
        TextView letterM = findViewById(R.id.letterM);
        TextView letterB = findViewById(R.id.letterB);
        TextView letterE2 = findViewById(R.id.letterE2);
        TextView letterR = findViewById(R.id.letterR);

        Animation bounceAnimation = AnimationUtils.loadAnimation(this, R.anim.bounce);

        Handler handler = new Handler();
        handler.postDelayed(() -> letterE.startAnimation(bounceAnimation), 100);
        handler.postDelayed(() -> letterM.startAnimation(bounceAnimation), 300);
        handler.postDelayed(() -> letterB.startAnimation(bounceAnimation), 500);
        handler.postDelayed(() -> letterE2.startAnimation(bounceAnimation), 700);
        handler.postDelayed(() -> letterR.startAnimation(bounceAnimation), 900);

        buttonSignUp.setOnClickListener(v -> signUp()); // שולח את המשתמש למסך ההרשמה

        buttonExistingUserLogin.setOnClickListener(v -> {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user == null) {
                openExistingUserLoginActivity();
            } else {
                checkUserProfile(user);
            }
        });
    }

    private void openExistingUserLoginActivity() {
        Intent intent = new Intent(this, LoginUserActivity.class);
        startActivity(intent);
    }

    private void signUp() {
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.PhoneBuilder().build());

        Intent signUpIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setLogo(R.drawable.icon) // Set your custom logo here
                .build();
        signInLauncher.launch(signUpIntent);
    }

    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            new ActivityResultCallback<FirebaseAuthUIAuthenticationResult>() {
                @Override
                public void onActivityResult(FirebaseAuthUIAuthenticationResult result) {
                    onSignUpResult(result);
                }
            }
    );

    private void onSignUpResult(FirebaseAuthUIAuthenticationResult result) {
        if (result.getResultCode() == RESULT_OK) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                saveInitialUserData(user);
                transactToWelcomeActivity();
            }
        } else {
            // Sign in failed, handle error here
        }
    }

    private void checkUserProfile(FirebaseUser user) {
        // Check if the user has completed their profile
        String userId = user.getUid();
        FirebaseDatabase.getInstance().getReference("Users")
                .child(userId)
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        User userProfile = task.getResult().getValue(User.class);
                        if (userProfile != null && userProfile.getAge() > 0) {
                            transactToMainActivity();
                        } else {
                            transactToWelcomeActivity();
                        }
                    } else {
                        transactToWelcomeActivity();
                    }
                });
    }

    private void transactToMainActivity() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void transactToWelcomeActivity() {
        Intent intent = new Intent(getApplicationContext(), WelcomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void saveInitialUserData(FirebaseUser firebaseUser) {
        String userId = firebaseUser.getUid();
        String email = firebaseUser.getEmail();
        String phone = firebaseUser.getPhoneNumber();

        FirebaseDatabase.getInstance().getReference("Users")
                .child(userId)
                .child("email")
                .setValue(email != null ? email : "");
        FirebaseDatabase.getInstance().getReference("Users")
                .child(userId)
                .child("phone")
                .setValue(phone != null ? phone : "");
    }
}
