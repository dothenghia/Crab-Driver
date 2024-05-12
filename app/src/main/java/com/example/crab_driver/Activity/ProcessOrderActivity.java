package com.example.crab_driver.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.crab_driver.Manager.FirestoreConstants;
import com.example.crab_driver.Object.Order;
import com.example.crab_driver.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ProcessOrderActivity extends AppCompatActivity implements OnMapReadyCallback {
    private String userId;
    private Order order;
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

        order = (Order) getIntent().getSerializableExtra("order");
        userId = getIntent().getStringExtra("userID");
        updateOrderConfirm();

        Button pickUpBtn = findViewById(R.id.pick_up_btn);
        ImageButton shutDownBtn = findViewById(R.id.shutdown_btn);
        TextView customerInfoTv = findViewById(R.id.customer_info_tv);
        TextView locationTv = findViewById(R.id.location_tv);
        TextView addressTv = findViewById(R.id.address_tv);
        TextView feeTv = findViewById(R.id.fee_tv);

        customerInfoTv.setText(order.getCustomer().getName() + " - " + order.getCustomer().getPhoneNumber());

        String pickupAddress = order.getPickup().getAddress();
        String[] pickupParts = pickupAddress.split(", ", 2);
        locationTv.setText(pickupParts[0]);
        addressTv.setText(pickupParts[1]);

        feeTv.setText(Float.toString(order.getFee()) + " VND");

        shutDownBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (shutDownBtn.getBackground().getConstantState().equals(getResources().getDrawable(R.drawable.round_back_primary).getConstantState())) {
                    clearOrderConfirm();
                    finish();
                }
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
                    shutDownBtn.setBackgroundResource(R.drawable.round_back_white_oval);

                    TextView locationTv = findViewById(R.id.location_tv);
                    TextView addressTv = findViewById(R.id.address_tv);
                    String destinationAddress = order.getDestination().getAddress();
                    String[] destinationParts = destinationAddress.split(", ", 2);
                    locationTv.setText(destinationParts[0]);
                    addressTv.setText(destinationParts[1]);

                    TextView taskTv = findViewById(R.id.task_tv);
                    taskTv.setText(R.string.arriving);
                } else if (buttonText.equals(getString(R.string.drop_off))) {
                    updateOrderFinish();
                    finish();
                }
            }
        });

        LinearLayout chatWithCustomerOpt = findViewById(R.id.chat_opt);
        chatWithCustomerOpt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                startActivity(intent);
            }
        });

        LinearLayout callCustomerOpt = findViewById(R.id.call_opt);
        callCustomerOpt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNumber = order.getCustomer().getPhoneNumber();
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + phoneNumber));
                startActivity(intent);
            }
        });

    }
    private void updateOrderConfirm() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference orderRef = db.collection(FirestoreConstants.ORDER).document(order.getID());

        // Create a map to update the fields
        Map<String, Object> updates = new HashMap<>();
        updates.put(FirestoreConstants.ORDER_START, FieldValue.serverTimestamp()); // Update timestamp field
        updates.put(FirestoreConstants.ORDER_DRIVER, userId); // Update userId field

        // Update the document
        orderRef.update(updates).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("TAG", "DocumentSnapshot successfully updated!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("TAG", "Error updating document: " + e.getMessage());
            }
        });
    }

    private void clearOrderConfirm() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference orderRef = db.collection(FirestoreConstants.ORDER).document(order.getID());

        // Create a map to update the fields
        Map<String, Object> updates = new HashMap<>();
        updates.put(FirestoreConstants.ORDER_START, ""); // Update timestamp field
        updates.put(FirestoreConstants.ORDER_DRIVER, ""); // Update userId field

        // Update the document
        orderRef.update(updates).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("TAG", "DocumentSnapshot successfully updated!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("TAG", "Error updating document: " + e.getMessage());
            }
        });
    }

    private void updateOrderFinish() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference orderRef = db.collection(FirestoreConstants.ORDER).document(order.getID());

        // Create a map to update the fields
        Map<String, Object> updates = new HashMap<>();
        updates.put(FirestoreConstants.ORDER_END, FieldValue.serverTimestamp()); // Update timestamp field

        // Update the document
        orderRef.update(updates).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("TAG", "DocumentSnapshot successfully updated!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("TAG", "Error updating document: " + e.getMessage());
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
