package com.example.crab_driver.Activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.crab_driver.R;

import org.w3c.dom.Text;

public class VerifyOTPActivity extends AppCompatActivity {
    private boolean resendEnabled = false; // true after 60sec
    private int resendTime = 60;
    private TextView resendBtn;
    private int selectedEtPosition = 0;
    private EditText otpEt1;
    private EditText otpEt2;
    private EditText otpEt3;
    private EditText otpEt4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_verify_otp);

        otpEt1 = findViewById(R.id.otp_et1);
        otpEt2 = findViewById(R.id.otp_et2);
        otpEt3 = findViewById(R.id.otp_et3);
        otpEt4 = findViewById(R.id.otp_et4);

        resendBtn = findViewById(R.id.resend_btn);

        final String phoneNumber = getIntent().getStringExtra("phone_number");
        TextView phoneNumberTv = findViewById(R.id.phone_number_tv);
        phoneNumberTv.setText(phoneNumber);

        // show keyboard at otpEt1
        showKeyboard(otpEt1);

        // start resend count-down
        startCountDownTimer();
        resendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (resendEnabled) {
                    // handle resend code
                    // start new
                    startCountDownTimer();
                }
            }
        });

        Button submitBtn = findViewById(R.id.verify_submit_btn);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String generateOtp = otpEt1.getText().toString()+otpEt2.getText().toString()+otpEt3.getText().toString()+otpEt4.getText().toString();
                if (generateOtp.length()==4) {
                    // handle verification
                }
            }
        });

        otpEt1.addTextChangedListener(textWatcher);
        otpEt2.addTextChangedListener(textWatcher);
        otpEt3.addTextChangedListener(textWatcher);
        otpEt4.addTextChangedListener(textWatcher);
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
                }
            }
        }
    };

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DEL) {
            switch (selectedEtPosition) {
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
