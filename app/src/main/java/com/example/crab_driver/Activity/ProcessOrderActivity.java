package com.example.crab_driver.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.crab_driver.Object.Order;
import com.example.crab_driver.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class ProcessOrderActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_process_order);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            getSupportFragmentManager().beginTransaction().replace(R.id.map, mapFragment).commit();
        }

        Order order = (Order) getIntent().getSerializableExtra("order");

        Button pickUpBtn = findViewById(R.id.pick_up_btn);
        ImageButton shutDownBtn = findViewById(R.id.shutdown_btn);
        TextView customerInfoTv = findViewById(R.id.customer_info_tv);
        TextView locationTv = findViewById(R.id.location_tv);
        TextView addressTv = findViewById(R.id.address_tv);
        TextView feeTv = findViewById(R.id.fee_tv);

        customerInfoTv.setText(order.getCustomer().getName() + " - " + order.getCustomer().getPhoneNumber());
        locationTv.setText("Trường Đại học Khoa học Tự nhiên");
        addressTv.setText(order.getPickup().getAddress());
        feeTv.setText(Float.toString(order.getFee()) + " VND");

        shutDownBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        pickUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String buttonText = pickUpBtn.getText().toString();
                if (buttonText.equals(getString(R.string.pick_up))) {
                    pickUpBtn.setText(R.string.drop_off);
                    pickUpBtn.setBackgroundResource(R.drawable.round_back_primary);
                    pickUpBtn.setTextColor(getResources().getColor(R.color.black));

                    TextView locationTv = findViewById(R.id.location_tv);
                    locationTv.setText("Trường Đại học Khoa học Tự nhiên");
                    TextView addressTv = findViewById(R.id.address_tv);
                    addressTv.setText(order.getDestination().getAddress());
                    TextView taskTv = findViewById(R.id.task_tv);
                    taskTv.setText(R.string.arriving);
                } else if (buttonText.equals(getString(R.string.drop_off))) {
                    // Thong bao thanh cong
                }
            }
        });

        ImageButton chatWithCustomerBtn = findViewById(R.id.chat_btn);
        chatWithCustomerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }
    @Override
    public void onBackPressed() {
    }
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
    }
}
