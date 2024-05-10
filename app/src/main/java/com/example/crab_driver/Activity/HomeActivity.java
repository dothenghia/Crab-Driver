package com.example.crab_driver.Activity;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.crab_driver.Fragment.ControlFragment;
import com.example.crab_driver.Fragment.ProfileFragment;
import com.example.crab_driver.Fragment.RevenueFragment;
import com.example.crab_driver.Fragment.SettingsFragment;
import com.example.crab_driver.R;

public class HomeActivity extends AppCompatActivity {
    private int selectedTab = 1;
    ControlFragment controlFragment;
    ProfileFragment profileFragment;
    RevenueFragment revenueFragment;
    SettingsFragment settingsFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        final String userId = getIntent().getStringExtra("userID");
        controlFragment = new ControlFragment();
        profileFragment = new ProfileFragment();
        revenueFragment = new RevenueFragment();
        settingsFragment = new SettingsFragment();

        Bundle bundle = new Bundle();
        bundle.putString("userID", userId);

        controlFragment.setArguments(bundle);
        profileFragment.setArguments(bundle);
        revenueFragment.setArguments(bundle);
        settingsFragment.setArguments(bundle);

        final LinearLayout controlLayout = findViewById(R.id.control_layout);
        final LinearLayout profileLayout = findViewById(R.id.profile_layout);
        final LinearLayout revenueLayout = findViewById(R.id.revenue_layout);
        final LinearLayout settingsLayout = findViewById(R.id.settings_layout);

        final ImageView controlIv = findViewById(R.id.control_iv);
        final ImageView profileIv = findViewById(R.id.profile_iv);
        final ImageView revenueIv = findViewById(R.id.revenue_iv);
        final ImageView settingsIv = findViewById(R.id.settings_iv);

        final TextView controlTv = findViewById(R.id.control_tv);
        final TextView profileTv = findViewById(R.id.profile_tv);
        final TextView revenueTv = findViewById(R.id.revenue_tv);
        final TextView settingsTv = findViewById(R.id.settings_tv);

        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fragment_container_view, controlFragment, null)
                .commit();
        controlLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedTab != 1) {
                    getSupportFragmentManager().beginTransaction()
                            .setReorderingAllowed(true)
                            .replace(R.id.fragment_container_view, controlFragment)
                            .commit();

                    profileTv.setVisibility(View.GONE);
                    revenueTv.setVisibility(View.GONE);
                    settingsTv.setVisibility(View.GONE);
                    profileIv.setImageResource(R.drawable.icon_profile);
                    revenueIv.setImageResource(R.drawable.icon_revenue);
                    settingsIv.setImageResource(R.drawable.icon_settings);
                    profileLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    revenueLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    settingsLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));

                    controlTv.setVisibility(View.VISIBLE);
                    controlIv.setImageResource(R.drawable.icon_control_selected);
                    controlLayout.setBackgroundResource(R.drawable.round_back_dark_blue);

                    ScaleAnimation scaleAnimation = new ScaleAnimation(0.8f, 1.0f, 1f, 1f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f);
                    scaleAnimation.setDuration(200);
                    scaleAnimation.setFillAfter(true);
                    controlLayout.startAnimation(scaleAnimation);

                    selectedTab = 1;
                }
            }
        });
        profileLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedTab != 2) {
                    getSupportFragmentManager().beginTransaction()
                            .setReorderingAllowed(true)
                            .replace(R.id.fragment_container_view, profileFragment)
                            .commit();

                    controlTv.setVisibility(View.GONE);
                    revenueTv.setVisibility(View.GONE);
                    settingsTv.setVisibility(View.GONE);
                    controlIv.setImageResource(R.drawable.icon_control);
                    revenueIv.setImageResource(R.drawable.icon_revenue);
                    settingsIv.setImageResource(R.drawable.icon_settings);
                    controlLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    revenueLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    settingsLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));

                    profileTv.setVisibility(View.VISIBLE);
                    profileIv.setImageResource(R.drawable.icon_profile_selected);
                    profileLayout.setBackgroundResource(R.drawable.round_back_dark_blue);

                    ScaleAnimation scaleAnimation = new ScaleAnimation(0.8f, 1.0f, 1f, 1f, Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
                    scaleAnimation.setDuration(200);
                    scaleAnimation.setFillAfter(true);
                    profileLayout.startAnimation(scaleAnimation);

                    selectedTab = 2;
                }
            }
        });
        revenueLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedTab != 3) {
                    getSupportFragmentManager().beginTransaction()
                            .setReorderingAllowed(true)
                            .replace(R.id.fragment_container_view, revenueFragment)
                            .commit();

                    profileTv.setVisibility(View.GONE);
                    controlTv.setVisibility(View.GONE);
                    settingsTv.setVisibility(View.GONE);
                    profileIv.setImageResource(R.drawable.icon_profile);
                    controlIv.setImageResource(R.drawable.icon_control);
                    settingsIv.setImageResource(R.drawable.icon_settings);
                    profileLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    controlLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    settingsLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));

                    revenueTv.setVisibility(View.VISIBLE);
                    revenueIv.setImageResource(R.drawable.icon_revenue_selected);
                    revenueLayout.setBackgroundResource(R.drawable.round_back_dark_blue);

                    ScaleAnimation scaleAnimation = new ScaleAnimation(0.8f, 1.0f, 1f, 1f, Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
                    scaleAnimation.setDuration(200);
                    scaleAnimation.setFillAfter(true);
                    revenueLayout.startAnimation(scaleAnimation);

                    selectedTab = 3;
                }
            }
        });
        settingsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedTab != 4) {
                    getSupportFragmentManager().beginTransaction()
                            .setReorderingAllowed(true)
                            .replace(R.id.fragment_container_view, settingsFragment)
                            .commit();

                    profileTv.setVisibility(View.GONE);
                    controlTv.setVisibility(View.GONE);
                    revenueTv.setVisibility(View.GONE);
                    profileIv.setImageResource(R.drawable.icon_profile);
                    controlIv.setImageResource(R.drawable.icon_control);
                    revenueIv.setImageResource(R.drawable.icon_revenue);
                    profileLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    controlLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    revenueLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));

                    settingsTv.setVisibility(View.VISIBLE);
                    settingsIv.setImageResource(R.drawable.icon_settings_selected);
                    settingsLayout.setBackgroundResource(R.drawable.round_back_dark_blue);

                    ScaleAnimation scaleAnimation = new ScaleAnimation(0.8f, 1.0f, 1f, 1f, Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
                    scaleAnimation.setDuration(200);
                    scaleAnimation.setFillAfter(true);
                    settingsLayout.startAnimation(scaleAnimation);

                    selectedTab = 4;
                }
            }
        });
    }
}
