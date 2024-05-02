package com.example.crab_driver.Activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.crab_driver.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignupActivity extends AppCompatActivity {
    private static final int REQUEST_IMAGE_PICK = 1;
    CircleImageView avatarImageView;
    private boolean passwordShowing = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);

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

            // Password
        final EditText passwordEt = findViewById(R.id.password_et);
        final ImageView visibilityIv = findViewById(R.id.visibility_iv);

        // Show/Hide password
        visibilityIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (passwordShowing) {
                    passwordShowing = false;
                    passwordEt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    visibilityIv.setImageResource(R.drawable.icon_visibility_off);
                } else {
                    passwordShowing = true;
                    passwordEt.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    visibilityIv.setImageResource(R.drawable.icon_visibility);
                }

                passwordEt.setSelection(passwordEt.length());
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
                String selectedLanguage = (String) parentView.getItemAtPosition(position);
                Toast.makeText(SignupActivity.this, "Selected Driver Type: " + selectedLanguage, Toast.LENGTH_SHORT).show();
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
                Intent intent = new Intent(SignupActivity.this, VerifyOTPActivity.class);
                EditText et = findViewById(R.id.phone_number_et);
                String phoneNumber = String.valueOf(et.getText());
                intent.putExtra("phone_number", phoneNumber);
                startActivity(intent);
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
}
