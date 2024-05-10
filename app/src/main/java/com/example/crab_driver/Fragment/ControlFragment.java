package com.example.crab_driver.Fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.InsetDrawable;
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
import android.widget.Toast;

import com.example.crab_driver.Dialog.NoInternetDialog;
import com.example.crab_driver.Dialog.ReceiveOrderDialog;
import com.example.crab_driver.Manager.DocumentManager;
import com.example.crab_driver.Manager.FirestoreConstants;
import com.example.crab_driver.Object.Driver;
import com.example.crab_driver.Object.Order;
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

import java.util.List;

public class ControlFragment extends Fragment implements OnMapReadyCallback {
    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    private Button goOnlineBtn;
    private ImageButton searchForRideBtn;
    private View rootView;
    private Driver driver;
    private ProgressDialog progressDialog;
    private boolean handleNextDocument = true;

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

        driver = new Driver();

        if (!checkInternet()) {
            NoInternetDialog noInternetDialog = new NoInternetDialog(getActivity());
            noInternetDialog.setCancelable(false);
            noInternetDialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
            noInternetDialog.show();
        }

        Bundle bundle = getArguments();
        if (bundle != null) {
            String userId = bundle.getString("userID");

            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage(getString(R.string.fetching));
            progressDialog.setCancelable(false);
            progressDialog.show();

            DocumentManager driverManager = new DocumentManager(FirestoreConstants.DRIVER);
            driverManager.getDocumentData(userId, new DocumentManager.OnDocumentLoadedListener() {
                @Override
                public void onDocumentLoaded(Object documentData) {
                    driver.parseFromObject(documentData, new Driver.OnParseCompleteListener() {
                        @Override
                        public void onParseComplete(Driver driver) {
                            setDriver(driver);
                            progressDialog.dismiss();
                        }

                        @Override
                        public void onParseFailed(Exception e) {
                            Toast.makeText(requireContext(), "ParseFailed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onDocumentNotFound() {
                    Log.d("ProfileFragment", "Document not found");
                }

                @Override
                public void onError(Exception e) {
                    Log.d("ProfileFragment", e.getMessage());
                }
            });
        }

        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            getChildFragmentManager().beginTransaction().replace(R.id.map, mapFragment).commit();
        }

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
                handleNextDocument = true; // Reset the flag before processing documents
                handleDocuments();
            }
        });
        return rootView;
    }

    private void handleDocuments() {
        if (!handleNextDocument) return;

        ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getString(R.string.finding_ride));
        progressDialog.setCancelable(false);
        progressDialog.show();

        DocumentManager ordersManager = new DocumentManager(FirestoreConstants.ORDER);
        String vehicleTypeID = driver.getVehicle().getVehicleType().getID();
        ordersManager.getDocumentsWhereEqualTo(FirestoreConstants.ORDER_DRIVER, "", FirestoreConstants.VEHICLE_TYPE_ID, vehicleTypeID, new DocumentManager.OnDocumentsLoadedListener() {
            @Override
            public void onDocumentsLoaded(List<DocumentSnapshot> documents) {
                Log.d("doc_number", Integer.toString(documents.size()));
                progressDialog.dismiss();

                if (!documents.isEmpty()) {
                    handleNextDocument = false; // Set to false when dialog is shown
                    handleLoadedDocuments(documents);
                } else {
                    // Show no orders found message
                }
            }

            @Override
            public void onDocumentsNotFound() {
                progressDialog.dismiss();
                Log.d("ProfileFragment", "Document not found");
            }

            @Override
            public void onError(Exception e) {
                progressDialog.dismiss();
                Log.d("ProfileFragment", e.getMessage());
            }
        });
    }

    private void handleLoadedDocuments(List<DocumentSnapshot> documents) {
        Order order = new Order();
        handleDocumentAtIndex(documents, order, 0);
    }

    private void handleDocumentAtIndex(List<DocumentSnapshot> documents, Order order, int index) {
        if (index >= documents.size()) {
            // All documents processed
            handleNextDocument = true; // Set to true after processing
            return;
        }

        DocumentSnapshot document = documents.get(index);
        DocumentManager orderManager = new DocumentManager(FirestoreConstants.ORDER);
        orderManager.getDocumentData(document.getId(), new DocumentManager.OnDocumentLoadedListener() {
            @Override
            public void onDocumentLoaded(Object documentData) {
                order.parseFromObject(documentData, new Order.OnParseCompleteListener() {
                    @Override
                    public void onParseComplete(Order parsedOrder) {
                        if (parsedOrder.getDriver() == null) {
                            Log.d("DiaChiDen", parsedOrder.getDestination().getAddress());
                            Log.d("DiaChiDon", parsedOrder.getPickup().getAddress());
                            Log.d("KhachHang", parsedOrder.getCustomer().getName());
                            Log.d("SDT", parsedOrder.getCustomer().getPhoneNumber());

                            ReceiveOrderDialog receiveOrderDialog = new ReceiveOrderDialog(getActivity(), parsedOrder);
                            receiveOrderDialog.setCancelable(false);
                            receiveOrderDialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
                            receiveOrderDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    handleDocumentAtIndex(documents, order, index + 1); // Process next document after dialog is dismissed
                                }
                            });
                            receiveOrderDialog.show();
                        } else {
                            // Order already assigned to another driver
                            handleDocumentAtIndex(documents, order, index + 1); // Process next document
                        }
                    }

                    @Override
                    public void onParseFailed(Exception e) {
                        handleDocumentAtIndex(documents, order, index + 1); // Process next document
                    }
                });
            }

            @Override
            public void onDocumentNotFound() {
                handleDocumentAtIndex(documents, order, index + 1); // Process next document
            }

            @Override
            public void onError(Exception e) {
                handleDocumentAtIndex(documents, order, index + 1); // Process next document
            }
        });
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

    private void setDriver(Driver driver) {
        this.driver = driver;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
    }
}