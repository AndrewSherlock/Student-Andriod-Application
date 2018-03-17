package com.itbstudentapp.AdminSystem;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.itbstudentapp.ForumManager;
import com.itbstudentapp.ForumPost;
import com.itbstudentapp.R;

import java.util.Calendar;

public class ReportedPostModal extends DialogFragment implements View.OnClickListener {

    private View v;
    private static String dbRef;
    private static Context ct;
    private static ForumPost fp;

    static ReportedPostModal newInstance(ForumPost forumPost, String ref, Context context)
    {
        dbRef = ref;
        ct = context;
        fp = forumPost;
        return new ReportedPostModal();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.reported_post_dialog, container, false);
        LinearLayout layout = v.findViewById(R.id.report_layout);

        return v;
    }

    @Override
    public void onClick(View v) {

    }
}
