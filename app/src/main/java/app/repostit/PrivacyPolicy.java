package app.repostit;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import app.repostit.utils.Util;
import app.repostit.widget.CustomTextView;


public class PrivacyPolicy extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Util.setLightWhiteStatusBar(this);
        findViewById(R.id.btn_back).setVisibility(View.VISIBLE);
        findViewById(R.id.tv_app_name).setVisibility(View.GONE);
        findViewById(R.id.tv_actionbar_title).setVisibility(View.VISIBLE);
        findViewById(R.id.icon_setting).setVisibility(View.GONE);
        findViewById(R.id.icon_heart).setVisibility(View.GONE);
        findViewById(R.id.btn_back).setOnClickListener(view -> onBackPressed());

        ((CustomTextView) findViewById(R.id.tv_actionbar_title)).setText(getString(R.string.title_activity_privacy_policy));
        ((WebView) findViewById(R.id.wv)).loadUrl("file:///android_asset/policy.html");

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
