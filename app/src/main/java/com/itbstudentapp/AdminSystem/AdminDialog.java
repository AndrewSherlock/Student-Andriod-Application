package com.itbstudentapp.AdminSystem;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.TextView;

import com.itbstudentapp.R;

public class AdminDialog extends Dialog {
    public AdminDialog(@NonNull Context context, String title, String topic) {
        super(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        setContentView(R.layout.admin_dialog);

        TextView titleText = findViewById(R.id.admin_section);
        title = title.substring(0,1).toUpperCase() + title.substring(1, title.length()).toLowerCase();
        titleText.setText(title);

    }
}
