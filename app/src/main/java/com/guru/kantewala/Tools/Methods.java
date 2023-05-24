package com.guru.kantewala.Tools;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.View;

import com.guru.kantewala.R;
import com.guru.kantewala.databinding.DialogLoadingBinding;

public class Methods {

    public static void showFailedAlert(Activity activity, String code, String message, String redirectLink, boolean retry, boolean cancellable){
        message = message + " (EC"+code+")";
        showError(activity, message, cancellable);
    }


    public static void showError(Activity context, String message, boolean cancellable){
        if (context != null) {

            AlertDialog dialog;
            DialogLoadingBinding dialogBinding;

            dialogBinding = DialogLoadingBinding.inflate(context.getLayoutInflater(), null, false);


            dialogBinding.imageView.setVisibility(View.VISIBLE);
            dialogBinding.imageView.setImageDrawable(context.getDrawable(R.drawable.ic_error_bg));
            dialogBinding.progressBar.setVisibility(View.GONE);
            dialogBinding.titleTxt.setText("Some Error Occurred");
            dialogBinding.bodyTxt.setText(message);

            dialog = new AlertDialog.Builder(context)
                    .setView(dialogBinding.getRoot())
                    .show();
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


            dialog.setCancelable(cancellable);
        } else {
            Log.e("NoActivityError", message);
        }
    }

    public static void showError(Activity context, String message){
        if (context != null) {

            AlertDialog dialog;
            DialogLoadingBinding dialogBinding;

            dialogBinding = DialogLoadingBinding.inflate(context.getLayoutInflater(), null, false);


            dialogBinding.imageView.setVisibility(View.VISIBLE);
            dialogBinding.imageView.setImageDrawable(context.getDrawable(R.drawable.ic_error_bg));
            dialogBinding.progressBar.setVisibility(View.GONE);
            dialogBinding.titleTxt.setText("Some Error Occurred");
            dialogBinding.bodyTxt.setText(message);

            dialog = new AlertDialog.Builder(context)
                        .setView(dialogBinding.getRoot())
                        .show();
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


            dialog.setCancelable(true);
        } else {
            Log.e("NoActivityError", message);
        }
    }
}
