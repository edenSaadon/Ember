package com.example.ember;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ember.Models.User;
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
    private Button buttonSignIn;
    private Button buttonExistingUserLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        buttonSignIn = findViewById(R.id.buttonSignIn);
        buttonExistingUserLogin = findViewById(R.id.buttonExistingUserLogin);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user == null) {
                    signIn();
                } else {
                    checkUserProfile(user);
                }
            }
        });

        buttonExistingUserLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open an activity for existing users to login with email/phone
                openExistingUserLoginActivity();
            }
        });
    }

    private void openExistingUserLoginActivity() {
        Intent intent = new Intent(this, LoginUserActivity.class);
        startActivity(intent);
    }

    private void signIn() {
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.PhoneBuilder().build());

        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setLogo(R.drawable.icon) // Set your custom logo here
                .build();
        signInLauncher.launch(signInIntent);
    }

    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            new ActivityResultCallback<FirebaseAuthUIAuthenticationResult>() {
                @Override
                public void onActivityResult(FirebaseAuthUIAuthenticationResult result) {
                    onSignInResult(result);
                }
            }
    );

    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
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
