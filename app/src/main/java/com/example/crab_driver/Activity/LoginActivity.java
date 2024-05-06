package com.example.crab_driver.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.crab_driver.Class.FirestoreConstants;

import com.example.crab_driver.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    String driverCollection = FirestoreConstants.DRIVER_COLLECTION;
    String vehicleCollection = FirestoreConstants.VEHICLE_COLLECTION;
    String vehicleTypeCollection = FirestoreConstants.VEHICLE_TYPE_COLLECTION;
    private boolean passwordShowing = false;
    FirebaseAuth mAuth;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progress_bar);

        final EditText usernameEt = findViewById(R.id.username_et);
        final EditText passwordEt = findViewById(R.id.password_et);
        final Button submitBtn = findViewById(R.id.submit_btn);
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

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, VerifyOTPActivity.class);
                String phoneNumber = usernameEt.getText().toString();
                intent.putExtra("phone_number", phoneNumber);
                startActivity(intent);
//                progressBar.setVisibility(View.VISIBLE);
//                String username = usernameEt.getText().toString().trim();
//                String password = passwordEt.getText().toString().trim();
//
//                if (TextUtils.isEmpty(username)) {
//                    Toast.makeText(LoginActivity.this, "Enter username", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                if (TextUtils.isEmpty(password)) {
//                    Toast.makeText(LoginActivity.this, "Enter password", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                mAuth.createUserWithEmailAndPassword(username, password)
//                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
//                            @Override
//                            public void onComplete(@NonNull Task<AuthResult> task) {
//                                progressBar.setVisibility(View.GONE);
//                                if (task.isSuccessful()) {
//                                    FirebaseUser user = mAuth.getCurrentUser();
//                                    //
//                                } else {
//
//                                }
//                            }
//                        });
            }
        });
    }
}
