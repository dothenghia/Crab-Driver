package com.example.crab_driver;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.crab_driver.Activity.LoginActivity;
import com.example.crab_driver.Activity.SignupActivity;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    boolean isFirst = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        // Check if the user is logged in
        if (isLoggedIn()) {
//            startActivity(new Intent(this, DashboardActivity.class));
            // Finish the current activity to prevent users from navigating back to it
            finish();
        }

        SharedPreferences sharedPreferences = getSharedPreferences("language_prefs", MODE_PRIVATE);
        String savedLanguage = sharedPreferences.getString("language", "");
        // Set locale based on the saved language or default to English
        if (!savedLanguage.isEmpty()) {
            setLocale(savedLanguage);
        } else {
            setLocale("en");
        }
        setContentView(R.layout.activity_main);

        Spinner languageSpinner = findViewById(R.id.language_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.language_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languageSpinner.setAdapter(adapter);

        if (!savedLanguage.isEmpty()) {
            switch (savedLanguage) {
                case "en":
                    savedLanguage = "English";
                    break;
                case "vi":
                    savedLanguage = "Tiếng Việt";
                    break;
            }
            int languageIndex = adapter.getPosition(savedLanguage);
            languageSpinner.setSelection(languageIndex);
        } else {
            // Set the default selection to English
            languageSpinner.setSelection(adapter.getPosition("English"));
        }

        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedLanguage = (String) parentView.getItemAtPosition(position);
                if (isFirst) {
                    isFirst = false;
                } else {
                    Toast.makeText(MainActivity.this, "Selected Language: " + selectedLanguage, Toast.LENGTH_SHORT).show();
                    switch (selectedLanguage) {
                        case "English":
                            selectedLanguage = "en";
                            break;
                        case "Tiếng Việt":
                            selectedLanguage = "vi";
                            break;
                    }
                    sharedPreferences.edit().putString("language", selectedLanguage).apply();
                    setLocale(selectedLanguage);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing if nothing is selected
            }
        });

        Button loginBtn = findViewById(R.id.login_btn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        });

        Button signupBtn = findViewById(R.id.signup_btn);
        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, SignupActivity.class));
            }
        });
    }
    // Method to check if the user is logged in
    private boolean isLoggedIn() {
        return false; // Placeholder
    }
    private void setLocale(String language) {
        Locale currentLocale = getResources().getConfiguration().locale;
        Locale newLocale = new Locale(language);

        // Check if the new locale is different from the current locale
        if (!currentLocale.equals(newLocale)) {
            Locale.setDefault(newLocale);
            Configuration config = new Configuration();
            config.locale = newLocale;
            getResources().updateConfiguration(config, getResources().getDisplayMetrics());

            // Restart activity to apply language changes
            recreate();
        }
    }

}