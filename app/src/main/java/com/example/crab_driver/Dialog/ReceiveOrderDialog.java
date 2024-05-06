package com.example.crab_driver.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.crab_driver.Activity.ProcessOrderActivity;
import com.example.crab_driver.Activity.SignupActivity;
import com.example.crab_driver.Activity.VerifyOTPActivity;
import com.example.crab_driver.R;

public class ReceiveOrderDialog extends Dialog {
    TextView acceptTimeTv;
    public ReceiveOrderDialog(@NonNull Context context) {
        super(context);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_receive_order);

        acceptTimeTv = findViewById(R.id.accept_time_tv);
        Button declineBtn = findViewById(R.id.decline_btn);
        Button acceptBtn = findViewById(R.id.accept_btn);
        declineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = getContext();
                Intent intent = new Intent(context, ProcessOrderActivity.class);
                context.startActivity(intent);

                dismiss();
            }
        });

        int waitingTime = 15;
        startCountDownTimer(waitingTime);
    }

    private void startCountDownTimer(int waitingTime) {
        new CountDownTimer(waitingTime * 1000, 1000){
            @Override
            public void onTick(long millisUntilFinished) {
                acceptTimeTv.setText(getContext().getString(R.string.auto_decline)+" "+(millisUntilFinished/1000));
            }
            @Override
            public void onFinish() {
                dismiss();
            }
        }.start();
    }
}
