package app.repostit.utils;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import app.repostit.R;
import app.repostit.listener.DialogCallbacks;
import app.repostit.widget.CustomTextView;

public class DialogCoreUtil {

    private static DialogCoreUtil ourInstance;

    private DialogCoreUtil() {
    }

    public static DialogCoreUtil getInstance() {
        if (null == ourInstance) ourInstance = new DialogCoreUtil();
        return ourInstance;
    }

    public void showStandardDialog(Context mContext, final String forWhat, String title,
                                   final String message, Integer icon, Integer iconColor,
                                   Integer imageRes, boolean iconKeepBlinkIcon, boolean cancelable,
                                   String positiveButtonText, String negativeButtonText,
                                   final DialogCallbacks dialogCallbacks) {
        try {

            View view = PopUtil.getInstance(mContext).inflateLayoutAndGetView(R.layout.dialog_standard, cancelable);

            CustomTextView tvTitle = view.findViewById(R.id.dialog_tv_title);
            CustomTextView tvMessage = view.findViewById(R.id.dialog_tv_message);

            CustomTextView btnPositive = view.findViewById(R.id.dialog_btn_okay);
            CustomTextView btnNegative = view.findViewById(R.id.dialog_btn_cancel);

            if (null != title) tvTitle.setText(title);
            else tvTitle.setVisibility(View.GONE);

            if (null != message) tvMessage.setText(message);
            else tvMessage.setVisibility(View.GONE);

            if (null != positiveButtonText) btnPositive.setText(positiveButtonText);
            if (null != negativeButtonText) btnNegative.setText(negativeButtonText);
            else btnNegative.setVisibility(View.INVISIBLE);

            btnPositive.setOnClickListener(view1 -> {
                PopUtil.getInstance(mContext).dismiss();
                if (null != dialogCallbacks) dialogCallbacks.onDialogPositiveButtonClicked(forWhat);
            });

            if (null != negativeButtonText) {
                btnNegative.setOnClickListener(view12 -> {
                    PopUtil.getInstance(mContext).dismiss();
                    if (null != dialogCallbacks)
                        dialogCallbacks.onDialogNegativeButtonClicked(forWhat);
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
