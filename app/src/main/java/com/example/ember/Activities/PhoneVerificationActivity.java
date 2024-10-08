package com.example.ember.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ember.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

public class PhoneVerificationActivity extends AppCompatActivity {

    private EditText codeInput;
    private Button btnVerify;
    private FirebaseAuth mAuth;
    private String verificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_verification);

        codeInput = findViewById(R.id.input_code);
        btnVerify = findViewById(R.id.btn_verify);
        mAuth = FirebaseAuth.getInstance();

        verificationId = getIntent().getStringExtra("verificationId");

        btnVerify.setOnClickListener(v -> {
            String code = codeInput.getText().toString().trim();
            if (!code.isEmpty() && verificationId != null) {
                verifyCode(code);
            } else {
                Toast.makeText(PhoneVerificationActivity.this, "Enter verification code", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        LoginUserActivity.signInWithPhoneAuthCredential(credential, this);
    }


}
