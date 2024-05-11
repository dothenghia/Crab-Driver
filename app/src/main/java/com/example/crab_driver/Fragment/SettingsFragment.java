package com.example.crab_driver.Fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.crab_driver.MainActivity;
import com.example.crab_driver.Manager.DocumentManager;
import com.example.crab_driver.Manager.FirestoreConstants;
import com.example.crab_driver.Object.Driver;
import com.example.crab_driver.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Locale;
import java.util.Map;

public class SettingsFragment extends Fragment {
    private View view;
    public SettingsFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_settings, container, false);

        Bundle bundle = getArguments();
        if (bundle != null) {
            String userId = bundle.getString("userID");

            DocumentManager driverManager = new DocumentManager(FirestoreConstants.DRIVER);
            Driver driver = new Driver();
            driverManager.getDocumentData(userId, new DocumentManager.OnDocumentLoadedListener() {
                @Override
                public void onDocumentLoaded(Object documentData) {
                    driver.parseFromObject(documentData, new Driver.OnParseCompleteListener() {
                        @Override
                        public void onParseComplete(Driver driver) {
                            updateFields(driver);
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

                //  NIGHT MODE
            SwitchCompat changeThemeSc = view.findViewById(R.id.change_theme_switch);
            SharedPreferences themePreferences = getActivity().getSharedPreferences("theme_prefs", Context.MODE_PRIVATE);
            int savedTheme = themePreferences.getInt("theme_mode", -1);
            if (savedTheme == -1) {
                savedTheme = AppCompatDelegate.MODE_NIGHT_NO;
            }
            changeThemeSc.setChecked(savedTheme == AppCompatDelegate.MODE_NIGHT_YES);
            changeThemeSc.setOnCheckedChangeListener((buttonView, isChecked) -> {
                // Save the new theme mode in SharedPreferences
                SharedPreferences.Editor editor = themePreferences.edit();
                editor.putInt("theme_mode", isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
                editor.apply();

                // Apply
                AppCompatDelegate.setDefaultNightMode(isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
                getActivity().recreate();
            });

            RelativeLayout logoutOpt = view.findViewById(R.id.logout_opt);
            logoutOpt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showLogoutConfirmationDialog();
                }
            });

            RelativeLayout deleteAccOpt = view.findViewById(R.id.delete_acc_opt);
            deleteAccOpt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDeleteAccConfirmationDialog(userId);
                }
            });

            RelativeLayout changeLanguageOpt = view.findViewById(R.id.change_language_opt);
            changeLanguageOpt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showLanguageChooserDialog();
                }
            });
        }

        return view;
    }

    private void updateFields(Driver driver) {
        TextView driverNameTv = view.findViewById(R.id.driver_name_tv);
        driverNameTv.setText(driver.getName());
        TextView driverPhoneTv = view.findViewById(R.id.driver_phone_tv);
        driverPhoneTv.setText(driver.getPhoneNumber());
    }

    private void showLogoutConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.logout)
                .setMessage(R.string.confirm_logout)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        logout();
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showDeleteAccConfirmationDialog(String userId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.delete_acc)
                .setMessage(R.string.confirm_delete)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        deleteAcc(userId);
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showLanguageChooserDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.choose_language)
                .setItems(R.array.language_options, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String selectedLanguage = getResources().getStringArray(R.array.language_options)[which];
                        switch (selectedLanguage) {
                            case "English":
                                selectedLanguage = "en";
                                break;
                            case "Tiếng Việt":
                                selectedLanguage = "vi";
                                break;
                        }
                        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("language_prefs", Context.MODE_PRIVATE);
                        sharedPreferences.edit().putString("language", selectedLanguage).apply();
                        setLocale(selectedLanguage);
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void logout() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("login_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        Intent intent = new Intent(getContext(), MainActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    private void deleteAcc(String userId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String documentPath = FirestoreConstants.DRIVER + "/" + userId;

        db.document(documentPath)
            .get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    Map<String, Object> data = (Map<String, Object>) documentSnapshot.getData();
                    Object vehicleRefObj = data.get(FirestoreConstants.VEHICLE);
                    if (vehicleRefObj instanceof DocumentReference) {
                        DocumentReference vehicleRef = (DocumentReference) vehicleRefObj;
                        vehicleRef.delete().addOnFailureListener(e -> {
                            // Handle
                        });
                    }
                    db.document(documentPath).delete().addOnSuccessListener(aVoid -> {
                        logout();
                    }).addOnFailureListener(e -> {
                        // Handle deletion failure
                    });
                } else {
                    // Document does not exist
                }
            }).addOnFailureListener(e -> {
                // Handle retrieval failure
            });
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
            getActivity().recreate();
        }
    }
}