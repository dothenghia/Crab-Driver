package com.example.crab_driver.Activity;

import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.crab_driver.R;

public class LoginActivity extends AppCompatActivity {
    private boolean passwordShowing = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

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
    }
}
