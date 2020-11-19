package com.example.firebaseotp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;


import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.tuyenmonkey.mkloader.MKLoader;

import java.util.concurrent.TimeUnit;


public class VerificationActivity extends AppCompatActivity {

    EditText otp;
    MKLoader loader;
    Button submit;
    TextView resend;
    String number,id;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        otp=findViewById(R.id.confirmEditText);
        loader=findViewById(R.id.loader);
        submit=findViewById(R.id.confirmBtn);
        resend=findViewById(R.id.resendTv);

        mAuth=FirebaseAuth.getInstance();

        number=getIntent().getStringExtra("number");

        sendVerificationCode();
    }

    private void sendVerificationCode() {

        new CountDownTimer(30000,1000) {
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
        }.start();

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(number)       // Phone number to verify
                        .setTimeout(30L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(VerificationActivity.this)                 // Activity (for callback binding)
                        .setCallbacks( new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                            @Override
                            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                super.onCodeSent(id, forceResendingToken);
                                VerificationActivity.this.id=s;
                                

                            }

                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                signInWithPhoneAuthCredential(phoneAuthCredential);
                                if (otp!=null){
                                    loader.setVisibility(View.VISIBLE);
                                }
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                Toast.makeText(VerificationActivity.this,"Failed",Toast.LENGTH_SHORT).show();

                            }


                        })          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        loader.setVisibility(View.INVISIBLE);
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
    public void confirmOTP(View view) {
        if (TextUtils.isEmpty(otp.getText().toString())){
            Toast.makeText(VerificationActivity.this,"Enter OTP",Toast.LENGTH_SHORT).show();
        }
        else if(otp.getText().toString().replace(" ","").length()!=6){
            Toast.makeText(VerificationActivity.this,"OTP doesn't Match!!! Please Try again",Toast.LENGTH_SHORT).show();
        }
        else {
            loader.setVisibility(View.VISIBLE);
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(id, otp.getText().toString().replace(" ",""));
            signInWithPhoneAuthCredential(credential);
        }
    }

    public void resend(View view) {
        sendVerificationCode();
    }
}