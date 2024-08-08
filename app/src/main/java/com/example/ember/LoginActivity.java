package com.example.ember;

import android.content.Intent;
import android.os.Bundle;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            signIn();
        } else {
            checkUserProfile(user);
        }
    }

    private void signIn() {
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.PhoneBuilder().build());

        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setLogo(R.drawable.icon)
                .build();
        signInLauncher.launch(signInIntent);
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
        }
    }
    private void checkUserProfile(FirebaseUser user) {
        // בדיקה האם המשתמש כבר מילא את השאלון
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



