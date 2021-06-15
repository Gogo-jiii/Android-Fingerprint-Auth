package com.example.fingerprintauth;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements MyBiometricManager.Callback {

    Button btnFingerPrintAuth;
    private MyBiometricManager myBiometricManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnFingerPrintAuth = findViewById(R.id.btnFingerPrintAuth);
        myBiometricManager = MyBiometricManager.getInstance(this);

        btnFingerPrintAuth.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                if (myBiometricManager.checkIfBiometricFeatureAvailable()) {
                    myBiometricManager.authenticate();
                }
            }
        });
    }

    @Override public void onBiometricAuthenticationResult(String result, CharSequence errString) {
        switch (result) {
            case AUTHENTICATION_SUCCESSFUL:
                Toast.makeText(MainActivity.this,
                        "Authentication succeeded!", Toast.LENGTH_SHORT).show();
                Log.d("TAG", "AUTHENTICATION_SUCCESSFUL");
                break;

            case AUTHENTICATION_FAILED:
                Toast.makeText(MainActivity.this, "Authentication failed",
                        Toast.LENGTH_SHORT)
                        .show();
                Log.d("TAG", "AUTHENTICATION_FAILED");
                break;

            case AUTHENTICATION_ERROR:
                Toast.makeText(MainActivity.this,
                        "Authentication error: " + errString, Toast.LENGTH_SHORT)
                        .show();
                Log.d("TAG", "AUTHENTICATION_ERROR");
                break;
        }
    }

    @Override protected void onActivityResult(int requestCode, int resultCode,
                                              @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MyBiometricManager.REQUEST_CODE && resultCode == RESULT_OK) {
            //check if biometric is now enrolled or not
        }
    }
}