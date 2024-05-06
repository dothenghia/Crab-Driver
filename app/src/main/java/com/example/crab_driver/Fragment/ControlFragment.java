package com.example.crab_driver.Fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.crab_driver.Dialog.NoInternetDialog;
import com.example.crab_driver.Dialog.ReceiveOrderDialog;
import com.example.crab_driver.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ControlFragment extends Fragment implements OnMapReadyCallback {
    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    private Button goOnlineBtn;
    private ImageButton searchForRideBtn;
    private View rootView;
    public ControlFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_control, container, false);

        if (!checkInternet()) {
            NoInternetDialog noInternetDialog = new NoInternetDialog(getActivity());
            noInternetDialog.setCancelable(false);
            noInternetDialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
            noInternetDialog.show();
        }

        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            getChildFragmentManager().beginTransaction().replace(R.id.map, mapFragment).commit();
        }
//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);

        goOnlineBtn = rootView.findViewById(R.id.go_online_btn);
        searchForRideBtn = rootView.findViewById(R.id.search_for_ride_btn);

        goOnlineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onGoOnlineClick();
            }
        });
        searchForRideBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProgressDialog progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage(getString(R.string.finding_ride));
                progressDialog.setCancelable(false);
                progressDialog.show();

                // Perform your task here, such as searching for a ride

                // Simulate a delay or perform your search operation
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Dismiss the progress dialog after the task is completed
                        progressDialog.dismiss();

                        // Add your logic after the task is completed
                        // For example, navigate to another screen or perform further actions
                    }
                }, 2000);

                ReceiveOrderDialog receiveOrderDialog = new ReceiveOrderDialog(getActivity());
                receiveOrderDialog.setCancelable(false);
                receiveOrderDialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
                receiveOrderDialog.show();
            }
        });
        return rootView;
    }
    private boolean checkInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    private void onGoOnlineClick() {
        String buttonText = goOnlineBtn.getText().toString();
        TextView tv = rootView.findViewById(R.id.active_status_tv);
        ImageView iv = rootView.findViewById(R.id.active_status_iv);

        if (buttonText.equals(getString(R.string.go_online))) {
            tv.setText(R.string.ur_online);
            iv.setImageResource(R.drawable.icon_dot_green);
            goOnlineBtn.setText(R.string.go_offline);
            goOnlineBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_power_on, 0, 0, 0);
            searchForRideBtn.setVisibility(View.VISIBLE);
        } else if (buttonText.equals(getString(R.string.go_offline))) {
            tv.setText(R.string.ur_offline);
            iv.setImageResource(R.drawable.icon_dot_red);
            goOnlineBtn.setText(R.string.go_online);
            goOnlineBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_power_off, 0, 0, 0);
            searchForRideBtn.setVisibility(View.GONE);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

//                // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}