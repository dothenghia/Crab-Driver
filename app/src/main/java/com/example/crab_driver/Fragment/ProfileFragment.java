package com.example.crab_driver.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.crab_driver.Manager.DocumentManager;
import com.example.crab_driver.Manager.FirestoreConstants;
import com.example.crab_driver.Object.Driver;
import com.example.crab_driver.R;

public class ProfileFragment extends Fragment {
    Driver driver;
    TextView scoreTv;
    TextView fullnameTv;
    TextView emailTv;
    TextView phoneNumberTv;
    TextView genderTv;
    TextView driverTypeTv;
    TextView vehicleNameTv;
    TextView vehicleNumberTv;
    private ProgressBar progressBar;

    public ProfileFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        progressBar = view.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);

        scoreTv = view.findViewById(R.id.score_tv);
        fullnameTv = view.findViewById(R.id.fullname_tv);
        emailTv = view.findViewById(R.id.email_tv);
        phoneNumberTv = view.findViewById(R.id.phone_number_tv);
        genderTv = view.findViewById(R.id.gender_tv);
        driverTypeTv = view.findViewById(R.id.driver_type_tv);
        vehicleNameTv = view.findViewById(R.id.vehicle_name_tv);
        vehicleNumberTv = view.findViewById(R.id.vehicle_license_plate_tv);

        driver = new Driver();

        Bundle bundle = getArguments();
        if (bundle != null) {
            String userId = bundle.getString("userID");
            Log.d("userId", userId);
            DocumentManager driverManager = new DocumentManager(FirestoreConstants.DRIVER);
            driverManager.getDocumentData(userId, new DocumentManager.OnDocumentLoadedListener() {
                @Override
                public void onDocumentLoaded(Object documentData) {
                    driver.parseFromObject(documentData, new Driver.OnParseCompleteListener() {
                        @Override
                        public void onParseComplete(Driver driver) {
                            updateFields(driver);
                            progressBar.setVisibility(View.GONE);
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

        return view;
    }
    void updateFields(Driver driver) {
        scoreTv.setText(Float.toString(driver.getScore()));
        fullnameTv.setText(driver.getName());
        emailTv.setText(driver.getEmail());
        phoneNumberTv.setText(driver.getPhoneNumber());

        switch (driver.getGender()) {
            case "Nữ":
                genderTv.setText(getResources().getString(R.string.female));
                break;
            case "Female":
                genderTv.setText(getResources().getString(R.string.female));
                break;
            case "Nam":
                genderTv.setText(getResources().getString(R.string.male));
                break;
            case "Male":
                genderTv.setText(getResources().getString(R.string.male));
                break;
        }

        genderTv.setText(driver.getGender());
        driverTypeTv.setText(driver.getVehicle().getVehicleType().getName());
        vehicleNameTv.setText(driver.getVehicle().getName());
        vehicleNumberTv.setText(driver.getVehicle().getNumber());
    }
}