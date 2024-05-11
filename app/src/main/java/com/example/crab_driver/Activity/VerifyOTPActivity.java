package com.example.crab_driver.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.crab_driver.Manager.FirestoreConstants;
import com.example.crab_driver.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class VerifyOTPActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    String inputOtp;
    String verificationCode = "";
    private boolean resendEnabled = false; // true after 60sec
    private int resendTime = 60;
    private TextView resendBtn;
    private int selectedEtPosition = 0;
    private EditText otpEt1;
    private EditText otpEt2;
    private EditText otpEt3;
    private EditText otpEt4;
    private EditText otpEt5;
    private EditText otpEt6;
    private ProgressBar progressBar;
    String phoneNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_verify_otp);

        mAuth = FirebaseAuth.getInstance();

        otpEt1 = findViewById(R.id.otp_et1);
        otpEt2 = findViewById(R.id.otp_et2);
        otpEt3 = findViewById(R.id.otp_et3);
        otpEt4 = findViewById(R.id.otp_et4);
        otpEt5 = findViewById(R.id.otp_et5);
        otpEt6 = findViewById(R.id.otp_et6);
        progressBar = findViewById(R.id.progress_bar);

        resendBtn = findViewById(R.id.resend_btn);

        phoneNumber = getIntent().getStringExtra("phone_number");
        TextView phoneNumberTv = findViewById(R.id.phone_number_tv);
        phoneNumberTv.setText(phoneNumber);
        sendOtp(phoneNumber,false);

        // show keyboard at otpEt1
        showKeyboard(otpEt1);

        // start resend count-down
        startCountDownTimer();
        resendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (resendEnabled) {
                    sendOtp(phoneNumber, true);
                    startCountDownTimer();
                }
            }
        });

        Button submitBtn = findViewById(R.id.verify_submit_btn);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputOtp = otpEt1.getText().toString()+otpEt2.getText().toString()+otpEt3.getText().toString()+otpEt4.getText().toString()+otpEt5.getText().toString()+otpEt6.getText().toString();
                if (inputOtp.length()==6 && verificationCode != "") {
                    Log.d("verificationCode", verificationCode);
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCode, inputOtp);
                    signInWithPhoneAuthCredential(credential);
                }
            }
        });

        otpEt1.addTextChangedListener(textWatcher);
        otpEt2.addTextChangedListener(textWatcher);
        otpEt3.addTextChangedListener(textWatcher);
        otpEt4.addTextChangedListener(textWatcher);
        otpEt5.addTextChangedListener(textWatcher);
        otpEt6.addTextChangedListener(textWatcher);
    }
    void sendOtp(String phoneNumber, boolean isResend) {
        progressBar.setVisibility(View.VISIBLE);
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber, // Phone number to verify
                60,          // Timeout duration
                TimeUnit.SECONDS, // Unit of timeout
                this,        // Activity (for callback binding)
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential credential) {
                        progressBar.setVisibility(View.GONE);
                        signInWithPhoneAuthCredential(credential);
                    }
                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(VerifyOTPActivity.this, "Verification failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {
                        progressBar.setVisibility(View.GONE);
                        VerifyOTPActivity.this.verificationCode = verificationId;
                        if (isResend) {
                            startCountDownTimer();
                        }
                    }
                });
    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("signIn", "signInWithCredential:success");
                        saveUserToPreferences(task.getResult().getUser());

                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        CollectionReference driversCollection = db.collection(FirestoreConstants.DRIVER);

                        driversCollection.whereEqualTo("SDT", phoneNumber).get().addOnCompleteListener(driverTask -> {
                            if (driverTask.isSuccessful()) {
                                QuerySnapshot querySnapshot = driverTask.getResult();
                                if (querySnapshot != null && querySnapshot.isEmpty()) {
                                    Intent intent = new Intent(VerifyOTPActivity.this, SignupActivity.class);
                                    intent.putExtra("userID", task.getResult().getUser().getUid());
                                    intent.putExtra("phone_number", phoneNumber);
                                    startActivity(intent);
                                    finish();
                                } else {
//                                    Toast.makeText(VerifyOTPActivity.this, "An account with this phone number already exists!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(VerifyOTPActivity.this, HomeActivity.class);
                                    intent.putExtra("userID", task.getResult().getUser().getUid());
                                    startActivity(intent);
                                    finish();
                                }
                            } else {
                                Toast.makeText(VerifyOTPActivity.this, "Failed to fetch documents: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {

                        Log.w("signIn", "signInWithCredential:failure", task.getException());
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {

                            Toast.makeText(VerifyOTPActivity.this, "Invalid verification code.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void saveUserToPreferences(FirebaseUser user) {
        SharedPreferences sharedPreferences = getSharedPreferences("login_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userID", user.getUid());

        // editor.putString("userName", user.getDisplayName());
        // editor.putString("userEmail", user.getEmail());

        editor.apply();
    }
    private void showKeyboard(EditText otpEt) {
        otpEt.requestFocus();
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(otpEt, InputMethodManager.SHOW_IMPLICIT);
    }
    private void startCountDownTimer() {
        resendEnabled = false;
        resendBtn.setTextColor(Color.parseColor("#99000000"));
        new CountDownTimer(resendTime * 1000, 1000){
            @Override
            public void onTick(long millisUntilFinished) {
                resendBtn.setText(getResources().getString(R.string.otp_resend)+" ("+(millisUntilFinished/1000)+")");
            }
            @Override
            public void onFinish() {
                resendEnabled = true;
                resendBtn.setText(getResources().getString(R.string.otp_resend));
                resendBtn.setTextColor(getResources().getColor(R.color.primary));
            }
        }.start();
    }
    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() > 0) {
                switch (selectedEtPosition) {
                    case 0:
                        selectedEtPosition = 1;
                        showKeyboard(otpEt2);
                        break;
                    case 1:
                        selectedEtPosition = 2;
                        showKeyboard(otpEt3);
                        break;
                    case 2:
                        selectedEtPosition = 3;
                        showKeyboard(otpEt4);
                        break;
                    case 3:
                        selectedEtPosition = 4;
                        showKeyboard(otpEt5);
                        break;
                    case 4:
                        selectedEtPosition = 5;
                        showKeyboard(otpEt6);
                        break;
                }
            }
        }
    };

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DEL) {
            switch (selectedEtPosition) {
                case 5:
                    selectedEtPosition--;
                    showKeyboard(otpEt5);
                    break;
                case 4:
                    selectedEtPosition--;
                    showKeyboard(otpEt4);
                    break;
                case 3:
                    selectedEtPosition--;
                    showKeyboard(otpEt3);
                    break;
                case 2:
                    selectedEtPosition--;
                    showKeyboard(otpEt2);
                    break;
                case 1:
                    selectedEtPosition--;
                    showKeyboard(otpEt1);
                    break;
            }
            return true;
        } else {
            return super.onKeyUp(keyCode, event);
        }
    }
}
