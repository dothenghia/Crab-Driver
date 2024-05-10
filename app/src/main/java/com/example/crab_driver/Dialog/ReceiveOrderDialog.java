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
import com.example.crab_driver.Object.Order;
import com.example.crab_driver.R;

public class ReceiveOrderDialog extends Dialog {
    private Order order;
    TextView feeTv;
//    TextView vehicleTypeTv;
    TextView destinationAddressTv;
    TextView pickupAddressTv;
    TextView acceptTimeTv;
    private boolean orderAccepted = false;
    public ReceiveOrderDialog(@NonNull Context context, Order order) {
        super(context);
        this.order = order;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_receive_order);

        feeTv = findViewById(R.id.fee_tv);
//        vehicleTypeTv = findViewById(R.id.vehicle_type_tv);
        destinationAddressTv = findViewById(R.id.destination_address_tv);
        pickupAddressTv = findViewById(R.id.pickup_address_tv);

        acceptTimeTv = findViewById(R.id.accept_time_tv);
        Button declineBtn = findViewById(R.id.decline_btn);
        Button acceptBtn = findViewById(R.id.accept_btn);

        if (getContext() != null) {
            feeTv.setText(Float.toString(order.getFee()) + " VND");
//        vehicleTypeTv.setText(order.getVehicleType().getName());
            destinationAddressTv.setText(order.getDestination().getAddress());
            pickupAddressTv.setText(order.getPickup().getAddress());

            declineBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                }
            });
            acceptBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    orderAccepted = true; // Set the flag to indicate order acceptance
                    dismiss();

                    Context context = getContext();
                    Intent intent = new Intent(context, ProcessOrderActivity.class);
                    // Pass the Order object to ProcessOrderActivity
                    intent.putExtra("order", order);
                    context.startActivity(intent);
                }
            });
        }

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

    public boolean isOrderAccepted() {
        return orderAccepted;
    }
}
