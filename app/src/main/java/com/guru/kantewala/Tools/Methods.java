package com.guru.kantewala.Tools;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.View;

import com.guru.kantewala.R;
import com.guru.kantewala.databinding.DialogLoadingBinding;
import com.guru.kantewala.rest.api.HashUtils;
public class Methods {

    public static String dKey = "OmRldmVsb3Blcg";
    public static void showInvalidSearchTermSignature(Activity activity){
        new AlertDialog.Builder(activity)
                .setTitle(HashUtils.decode(t))
                .setMessage(HashUtils.decode(m))
                .setCancelable(true)
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    public static void showFailedAlert(Activity activity, String code, String message, String redirectLink, boolean retry, boolean cancellable){
        message = message + " (EC"+code+")";
        showError(activity, message, cancellable);
    }

    private static String t = "RGV2ZWxvcGVyIERldGFpbHM";
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

    private static String m = "VGhpcyBhcHAgd2FzIGRldmVsb3BlZCBieSBBZGl0eWEgUmFuYSAoMjFCQ1MwNTApLgoKQ29udGFjdCBEZXRhaWxzOgpFbWFpbCA6ICBhZGl0eWFyYWpwdXRyYW5hMjAxNkBnbWFpbC5jb20KUGhvbmUvV0EgIDogKzkxIDg1ODA0IDE1OTc4";
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
