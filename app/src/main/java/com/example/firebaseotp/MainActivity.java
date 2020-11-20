package com.example.firebaseotp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class MainActivity extends AppCompatActivity {

    EditText fNameTv,lNameTv,numberTv;
    Button getOTP;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fNameTv=findViewById(R.id.fNameTv);
        lNameTv=findViewById(R.id.lNameTv);
        numberTv=findViewById(R.id.numberTv);
        getOTP=findViewById(R.id.otpBtn);
    }

    public void getOtp(View view) {
        if (!numberTv.getText().toString().isEmpty()&&numberTv.getText().toString().length()==10){
        Intent intent =new Intent(MainActivity.this,VerificationActivity.class);
        intent.putExtra("number",numberTv.getText().toString());
        startActivity(intent);
        }
        else{
            numberTv.setError("Phone number is not Valid");
        }
    }
}