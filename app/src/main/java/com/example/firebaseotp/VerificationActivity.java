package com.example.firebaseotp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;


import android.content.Intent;
import android.os.Bundle;


import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;


import java.util.concurrent.TimeUnit;


public class VerificationActivity extends AppCompatActivity {

    FirebaseAuth mAuth;

    Button verify_btn;
    ProgressBar progressBar;
    EditText otp;

    String otpSentBySystem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        verify_btn = findViewById(R.id.verify_btn);
        otp= findViewById(R.id.otp);
        progressBar = findViewById(R.id.progress_bar);
        mAuth=FirebaseAuth.getInstance();


        String phoneNo = getIntent().getStringExtra("number");

        sendVerificationCodeToUser(phoneNo);

    }

    private void sendVerificationCodeToUser(String phoneNo) {
      /*  new CountDownTimer(30000,1000) {
            @Override
            public void onTick(long l) {
                resend.setText(""+l/1000);
                resend.setEnabled(false);
            }

            @Override
            public void onFinish() {
                resend.setText("Resend");
                resend.setEnabled(true);
            }
        }.start();*/
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber("+91"+phoneNo)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            otpSentBySystem=s;
            Log.d("OTP", "onCodeSent:" + s);

        }

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

            String code=phoneAuthCredential.getSmsCode();
            if (otp!=null){
                progressBar.setVisibility(View.VISIBLE);
                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(VerificationActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    };


    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(VerificationActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            startActivity(new Intent(VerificationActivity.this,HomeActivity.class));
                            finish();
                            // ...
                        } else {
                            Toast.makeText(VerificationActivity.this,"Verification Failed",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void verifyCode(String codeByUser) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(otpSentBySystem, codeByUser);
        signInWithPhoneAuthCredential(credential);
    }


    public void verifyBtn(View view) {
        String code = otp.getText().toString();

        if (code.isEmpty() || code.length() < 6) {
            otp.setError("Wrong OTP...");
            otp.requestFocus();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        verifyCode(code);
    }
}