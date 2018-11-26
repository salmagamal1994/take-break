package com.example.android.take_a_break_app.helpers;

import android.app.ProgressDialog;
import android.content.Context;

import com.example.android.take_a_break_app.R;


/**
 * Created by salma gamal on 19/10/2018.
 */

public class Utils {
    private static final String TAG = Utils.class.getSimpleName();
    public static Utils instance;
    Context context;
    private ProgressDialog progressDialog;

    public Utils(Context context) {
        this.context = context;
        progressDialog = new ProgressDialog(context);
        progressDialog.setIndeterminate(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(context.getResources().getString(R.string.Loading));
    }

    public static Utils getInstance(Context context) {
        if (instance == null) {
            instance = new Utils(context);
        }
        return instance;
    }

    public void showProgress() {
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    public void dismissProgress() {
        if (progressDialog != null) {
            if (progressDialog.isShowing())
                progressDialog.dismiss();
            instance = null;
        }
    }

    public ProgressDialog getProgressDialog() {
        return progressDialog;

    }

}
