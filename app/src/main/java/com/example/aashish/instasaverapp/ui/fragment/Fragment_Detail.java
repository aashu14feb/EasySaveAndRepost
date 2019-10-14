package com.example.aashish.instasaverapp.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.devbrackets.android.exomedia.listener.OnPreparedListener;
import com.example.aashish.instasaverapp.AppActivity;
import com.example.aashish.instasaverapp.R;
import com.example.aashish.instasaverapp.entity.ImageData;
import com.example.aashish.instasaverapp.utils.AppConstants;
import com.example.aashish.instasaverapp.utils.Extras;
import com.example.aashish.instasaverapp.utils.ImageDownloadTask;
import com.example.aashish.instasaverapp.utils.Util;
import com.example.aashish.instasaverapp.widget.CustomTextView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.squareup.picasso.Picasso;

public class Fragment_Detail extends AppCompatActivity implements View.OnClickListener {

    Context mContext;
    ImageData imageData;

    private final int PERMISSION_DOWNLOAD = 1000;
    private final int PERMISSION_SHARE = 1001;
    private final int PERMISSION_REPOST = 1002;

    public Fragment_Detail() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.fragment_detail);

        Toolbar appbar = findViewById(R.id.toolbar);
        setSupportActionBar(appbar);

        imageData = (ImageData) getIntent().getSerializableExtra(Extras.IMAGE_DATA);

        initActionBar();

        if (AppConstants.SHOW_AD) {
            AdView mAdView = (AdView) findViewById(R.id.adView_detail);
            mAdView.setVisibility(View.VISIBLE);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
        }


        if (imageData != null) {

            ((CustomTextView) findViewById(R.id.tv_actionbar_title)).setText(imageData.profileName);
            ImageView profile_image = findViewById(R.id.profile_image);
            Picasso.get().load(imageData.profileUrl).fit().centerCrop().into(profile_image);

            TextView profileName = findViewById(R.id.profileName);
            profileName.setText(imageData.profileName);

            ImageView imageView = findViewById(R.id.image);
            imageView.setVisibility(View.GONE);
            final com.devbrackets.android.exomedia.ui.widget.VideoView videoView = findViewById(R.id.video);
            videoView.setVisibility(View.GONE);
            if (imageData.is_Video) {
                videoView.getLayoutParams().height = imageData.height;
                videoView.setVisibility(View.VISIBLE);
                videoView.setVideoURI(Uri.parse(imageData.video_url));
                videoView.setOnPreparedListener(new OnPreparedListener() {
                    @Override
                    public void onPrepared() {
                        videoView.start();
                    }
                });
            } else {
                imageView.setVisibility(View.VISIBLE);
                imageView.getLayoutParams().height = imageData.height;
                Picasso.get().load(imageData.url).resize(imageData.width, imageData.height).into(imageView);
            }

            TextView textView = findViewById(R.id.desc);
            textView.setText(imageData.description);

            TextView likes = findViewById(R.id.likes);
            likes.setText(imageData.likes + " Likes");

            findViewById(R.id.tv_download).setOnClickListener(view -> {
                if (Util.checkPermission(Fragment_Detail.this, PERMISSION_DOWNLOAD)) {
                    downLoadImage();
                }
            });

            findViewById(R.id.tv_repost).setOnClickListener(view -> {

                if (Util.isPackageInstalled("com.instagram.android", mContext.getPackageManager())) {
                    if (Util.checkPermission(Fragment_Detail.this, PERMISSION_REPOST)) {
                        repostImage();
                    }
                } else {
                    Toast.makeText(mContext, "Instagram not installed", Toast.LENGTH_SHORT).show();
                }

            });

            findViewById(R.id.tv_share).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Util.checkPermission(Fragment_Detail.this, PERMISSION_SHARE)) {
                        shareImage();
                    }
                }
            });

            findViewById(R.id.tv_folder).setOnClickListener(view -> {
                ImageData.deleteSafe(imageData);
                startActivity(new Intent(this, AppActivity.class));
            });
        }
    }


    private void initActionBar() {

        findViewById(R.id.tv_app_name).setVisibility(View.GONE);
        findViewById(R.id.tv_actionbar_title).setVisibility(View.VISIBLE);
        findViewById(R.id.btn_menu).setVisibility(View.GONE);
        findViewById(R.id.btn_back).setVisibility(View.VISIBLE);
        findViewById(R.id.icon_heart).setVisibility(View.GONE);
        findViewById(R.id.icon_instagram).setVisibility(View.GONE);
        findViewById(R.id.icon_setting).setVisibility(View.GONE);
        findViewById(R.id.btn_back).setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.btn_back:
                onBackPressed();
                break;
        }
    }

    void downLoadImage() {
        new ImageDownloadTask(mContext).execute(imageData.url, imageData.name, imageData.video_url, String.valueOf(imageData.is_Video));
    }

    void shareImage() {
        Util.shareImageTask(mContext, imageData.url, imageData.name, imageData.description, imageData.is_Video, imageData.video_url);
    }

    void repostImage() {
        Util.postOnInstagram(mContext, imageData.url, imageData.name, imageData.description, imageData.is_Video, imageData.video_url);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            switch (requestCode) {
                case PERMISSION_DOWNLOAD:
                    downLoadImage();
                    break;

                case PERMISSION_SHARE:
                    shareImage();
                    break;

                case PERMISSION_REPOST:
                    repostImage();
                    break;

            }
        }
    }
}
