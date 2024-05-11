package com.example.crab_driver.Activity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.crab_driver.Manager.FirestoreConstants;
import com.example.crab_driver.R;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.hbb20.CountryCodePicker;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignupActivity extends AppCompatActivity {
    private static final int REQUEST_IMAGE_PICK = 1;
    CircleImageView avatarImageView;
    EditText fullnameEt;
    String phoneNumber;
    EditText vehicleNameEt;
    EditText vehicleNumberEt;
    EditText emailEt;
    RadioGroup genderRadioGroup;
    String userId;
    String selectedDriverType = "1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);

        phoneNumber = getIntent().getStringExtra("phone_number");
//        Log.d("SDT", phoneNumber);
        userId = getIntent().getStringExtra("userID");
        clearUserFromPreferences(userId);
        EditText phoneNumberEt = findViewById(R.id.phone_number_et);
        phoneNumberEt.setText(phoneNumber);

        fullnameEt = findViewById(R.id.fullname_et);
        vehicleNameEt = findViewById(R.id.vehicle_name_et);
        vehicleNumberEt = findViewById(R.id.vehicle_license_plate_et);
        emailEt = findViewById(R.id.email_et);
        genderRadioGroup = findViewById(R.id.gender_radio_group);

        // Back
        Button backBtn = findViewById(R.id.back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

            //  Avatar
        avatarImageView = findViewById(R.id.avatar_iv);
        Button pickAvatarBtn = findViewById(R.id.pick_avatar_btn);
        pickAvatarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open image picker
                Intent pickImageIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickImageIntent, REQUEST_IMAGE_PICK);
            }
        });

            // Driver Type
        Spinner driverTypeSpinner = findViewById(R.id.driver_type_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.driver_type_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        driverTypeSpinner.setAdapter(adapter);
        driverTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selectedDriverType = Integer.toString(position + 1);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing if nothing is selected
            }
        });

            // Date picker
        Button driverLicenseDatePicker = findViewById(R.id.driver_license_date_picker);
        Button vehicleRegistrationDatePicker = findViewById(R.id.vehicle_registration_date_picker);
        driverLicenseDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView driverLicenseDate = findViewById(R.id.driver_license);
                openDatePicker(driverLicenseDate);
            }
        });
        vehicleRegistrationDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView vehicleRegistration = findViewById(R.id.vehicle_registration);
                openDatePicker(vehicleRegistration);
            }
        });

            // Submit
        Button submitBtn = findViewById(R.id.submit_btn);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("selectedDriverType", selectedDriverType);
                createAccount(phoneNumber, selectedDriverType);
            }
        });
    }
    private void createAccount(String phoneNumber, String driverType) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference driversCollection = db.collection(FirestoreConstants.DRIVER);

        driversCollection.whereEqualTo("SDT", phoneNumber).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                if (querySnapshot != null && querySnapshot.isEmpty()) {
                    // Query the vehicleType collection to find the document matching the selected driver type
                    db.collection(FirestoreConstants.VEHICLE_TYPE).whereEqualTo(FirestoreConstants.VEHICLE_TYPE_ID, driverType).get().addOnSuccessListener(driverTypeSnapshot -> {
                        if (!driverTypeSnapshot.isEmpty()) {
                            // Get the first document found (assuming there is only one matching document)
                            DocumentSnapshot vehicleTypeDocument = driverTypeSnapshot.getDocuments().get(0);
                            DocumentReference vehicleTypeRef = vehicleTypeDocument.getReference();

                            Map<String, Object> vehicleData = new HashMap<>();
                            vehicleData.put(FirestoreConstants.VEHICLE_NAME, vehicleNameEt.getText().toString());
                            vehicleData.put(FirestoreConstants.VEHICLE_NUMBER, vehicleNumberEt.getText().toString());
                            vehicleData.put(FirestoreConstants.VEHICLE_TYPE, vehicleTypeRef);

                            db.collection(FirestoreConstants.VEHICLE).add(vehicleData).addOnSuccessListener(vehicleDocumentReference -> {
                                Toast.makeText(SignupActivity.this, "Vehicle added successfully!", Toast.LENGTH_SHORT).show();

                                Map<String, Object> driverData = new HashMap<>();
                                driverData.put(FirestoreConstants.DRIVER_NAME, fullnameEt.getText().toString());
                                driverData.put(FirestoreConstants.DRIVER_PHONE_NUMBER, phoneNumber);
                                driverData.put(FirestoreConstants.DRIVER_EMAIL, emailEt.getText().toString());
                                driverData.put(FirestoreConstants.DRIVER_SCORE, 5);

                                int selectedRadioButtonId = genderRadioGroup.getCheckedRadioButtonId();
                                if (selectedRadioButtonId != -1) {
                                    RadioButton selectedRadioButton = findViewById(selectedRadioButtonId);

                                    String selectedOption = selectedRadioButton.getText().toString();
                                    driverData.put(FirestoreConstants.DRIVER_GENDER, selectedOption);
                                }

                                driverData.put(FirestoreConstants.VEHICLE, vehicleDocumentReference);

                                db.collection(FirestoreConstants.DRIVER).document(userId).set(driverData).addOnSuccessListener(driverDocumentReference -> {
                                    saveUserToPreferences(userId);
                                    Intent intent = new Intent(SignupActivity.this, HomeActivity.class);
                                    intent.putExtra("userID", userId);
                                    startActivity(intent);
                                    finish();
                                }).addOnFailureListener(e -> {
                                    Toast.makeText(SignupActivity.this, "Failed to add TaiXe: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                            }).addOnFailureListener(e -> {
                                // Error occurred while adding Vehicle document
                                Toast.makeText(SignupActivity.this, "Failed to add vehicle: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                        } else {
                            // No matching document found in the vehicleType collection
                            Toast.makeText(SignupActivity.this, "No matching vehicle type found", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(e -> {
                        // Error occurred while querying the vehicleType collection
                        Toast.makeText(SignupActivity.this, "Failed to query vehicle types: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                } else {
                    Toast.makeText(SignupActivity.this, "An account with this phone number already exists!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(SignupActivity.this, "Failed to fetch documents: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            avatarImageView.setImageURI(imageUri);
        }
    }
    private void openDatePicker(TextView textView) {
        DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                textView.setText(String.valueOf(year) + "." + String.valueOf(month) + "." + String.valueOf(day));
            }
        }, 2024, 5, 1);
        dialog.show();
    }

    private void saveUserToPreferences(String userId) {
        SharedPreferences sharedPreferences = getSharedPreferences("login_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userID", userId);
        editor.apply();
    }

    private void clearUserFromPreferences(String userId) {
        SharedPreferences sharedPreferences = getSharedPreferences("login_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}
