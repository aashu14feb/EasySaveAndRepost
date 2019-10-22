package app.repostit.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;


import app.repostit.R;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

/**
 * Created by ist on 3/5/17.
 */

public class PopUtil {

    private static PopUtil instance;
    private static Context context;
    private AlertDialog dialog;
    private AlertDialog.Builder alertDialog;
    private View alertView;
    private LayoutInflater inflater;

    public PopUtil(Context mContext) {
        context = mContext;
    }

    public static PopUtil getInstance(Context mContext) {
        if (instance == null) {
            instance = new PopUtil(mContext);
        }
        context = mContext;
        return instance;
    }

    public View inflateLayoutAndGetView(int resId, boolean cancelable) {
        dismiss();
        alertDialog = new AlertDialog.Builder(context);
        alertDialog.setCancelable(cancelable);
        inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        alertView = inflater != null ? inflater.inflate(resId, null) : null;
        dialog = alertDialog.create();
        dialog.setView(alertView);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
        dialog.show();
        return alertView;
    }

    public void dismiss() {
        try {
            if (null != dialog) dialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
