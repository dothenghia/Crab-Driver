package com.example.crab_driver.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.crab_driver.Interface.RetryListener;
import com.example.crab_driver.R;

public class NoInternetDialog extends Dialog {
    private RetryListener retryListener;
    public NoInternetDialog(@NonNull Context context, RetryListener retryListener) {
        super(context);
        this.retryListener = retryListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_no_internet);

        Button tryAgainBtn = findViewById(R.id.try_again_btn);
        tryAgainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (retryListener != null) {
                    retryListener.onRetry();
                }
                dismiss();
            }
        });
    }
}
