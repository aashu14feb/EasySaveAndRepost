package com.example.aashish.instasaverapp.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

import com.example.aashish.instasaverapp.R;


public class CustomTextView extends AppCompatTextView {

    String defaultFont = "segoe_ui_regular.ttf";
    private Context context;
    private String font;

    public CustomTextView(Context context) {
        super(context);
        this.context = context;
    }

    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        loadStateFromAttrs(context, attrs);
    }

    public CustomTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        loadStateFromAttrs(context, attrs);

    }

    private void loadStateFromAttrs(Context context, AttributeSet attributeSet) {
        if (attributeSet == null) {
            return; // quick exit
        }

        TypedArray a = null;
        try {
            a = getContext().obtainStyledAttributes(attributeSet, R.styleable.FontStyle);
            font = a.getString(R.styleable.FontStyle_custom_font);

            if (font != null)
                setTypeface(Typeface.createFromAsset(context.getAssets(), font));
            else
                setTypeface(Typeface.createFromAsset(context.getAssets(), defaultFont));

            boolean isUnderline = a.getBoolean(R.styleable.FontStyle_underline, false);
            if (isUnderline)
                this.setPaintFlags(this.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        } finally {
            if (a != null) {
                a.recycle(); // ensure this is always called
            }
        }
    }

    public String getFont() {
        return font;
    }

    public void setFont(String font) {
        this.font = font;
        setTypeface(Typeface.createFromAsset(context.getAssets(), font));
        invalidate();
        requestLayout();
    }
}