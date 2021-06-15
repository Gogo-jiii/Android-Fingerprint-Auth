package com.example.fingerprintauth;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.biometric.BiometricManager;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import java.util.concurrent.Executor;

import static android.hardware.biometrics.BiometricManager.Authenticators.BIOMETRIC_STRONG;
import static android.hardware.biometrics.BiometricManager.Authenticators.DEVICE_CREDENTIAL;

import androidx.biometric.BiometricPrompt;

public class MyBiometricManager {

    private static MyBiometricManager instance = null;
    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private androidx.biometric.BiometricPrompt.PromptInfo promptInfo;
    private Context context;
    private FragmentActivity fragmentActivity;
    private Callback callback;
    public static final int REQUEST_CODE = 100;

    private MyBiometricManager() {
    }

    public static MyBiometricManager getInstance(Context context) {
        if (instance == null) {
            instance = new MyBiometricManager();
        }
        instance.init(context);
        return instance;
    }

    private void init(Context context) {
        this.context = context;
        this.fragmentActivity = (FragmentActivity) context;
        this.callback = (Callback) context;
    }

    boolean checkIfBiometricFeatureAvailable() {
        BiometricManager biometricManager = BiometricManager.from(context);

        switch (biometricManager.canAuthenticate()) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                Log.d("MY_APP_TAG", "App can authenticate using biometrics.");
                //Toast.makeText(context, "App can authenticate using biometrics.",Toast.LENGTH_SHORT).show();
                return true;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                Log.e("MY_APP_TAG", "No biometric features available on this device.");
                Toast.makeText(context, "No biometric features available on this device.", Toast.LENGTH_SHORT).show();
                return false;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                Log.e("MY_APP_TAG", "Biometric features are currently unavailable.");
                Toast.makeText(context, "Biometric features are currently unavailable.", Toast.LENGTH_SHORT).show();
                return false;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                // Prompts the user to create credentials that your app accepts.
                final Intent enrollIntent = new Intent(Settings.ACTION_BIOMETRIC_ENROLL);
                enrollIntent.putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                        BIOMETRIC_STRONG | DEVICE_CREDENTIAL);

                fragmentActivity.startActivityForResult(enrollIntent, REQUEST_CODE);
                return false;
        }
        return false;
    }

    void authenticate() {
        setupBiometric();
        biometricPrompt.authenticate(promptInfo);
    }

    private void setupBiometric() {
        executor = ContextCompat.getMainExecutor(context);
        biometricPrompt = new BiometricPrompt(fragmentActivity, executor,
                new BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationError(int errorCode,
                                                      @NonNull CharSequence errString) {
                        super.onAuthenticationError(errorCode, errString);
                        callback.onBiometricAuthenticationResult(Callback.AUTHENTICATION_ERROR, errString);
                    }

                    @Override
                    public void onAuthenticationSucceeded(
                            @NonNull BiometricPrompt.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);
                        callback.onBiometricAuthenticationResult(Callback.AUTHENTICATION_SUCCESSFUL,
                                "");
                    }

                    @Override
                    public void onAuthenticationFailed() {
                        super.onAuthenticationFailed();
                        callback.onBiometricAuthenticationResult(Callback.AUTHENTICATION_FAILED, "");
                    }
                });

        showBiometricPrompt();
    }

    private void showBiometricPrompt() {
        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric login for my app")
                .setSubtitle("Log in using your biometric credential")
                .setNegativeButtonText("Use account password")
                .build();
    }

    interface Callback {
        void onBiometricAuthenticationResult(String result, CharSequence errString);

        String AUTHENTICATION_SUCCESSFUL = "AUTHENTICATION_SUCCESSFUL";
        String AUTHENTICATION_FAILED = "AUTHENTICATION_FAILED";
        String AUTHENTICATION_ERROR = "AUTHENTICATION_ERROR";
    }
}
