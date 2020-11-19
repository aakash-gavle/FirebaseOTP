package com.example.firebaseotp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hbb20.CountryCodePicker;

public class MainActivity extends AppCompatActivity {

    CountryCodePicker countryCodePicker;
    EditText number;
    Button getOtp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        countryCodePicker=findViewById(R.id.ccp);
        number=findViewById(R.id.editText);
        getOtp=findViewById(R.id.getOtpBtn);
        countryCodePicker.registerCarrierNumberEditText(number);
    }

    public void getOtp(View view) {
        if (!number.getText().toString().isEmpty()&&number.getText().toString().length()==10){
        Intent intent =new Intent(MainActivity.this,VerificationActivity.class);
        intent.putExtra("number",countryCodePicker.getFullNumberWithPlus());
        startActivity(intent);
        }
        else{
            number.setError("Phone number is not Valid");
        }
    }
}