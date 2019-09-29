package com.example.aashish.instasaverapp.ui.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.devbrackets.android.exomedia.listener.OnPreparedListener;
import com.devbrackets.android.exomedia.ui.widget.VideoView;
import com.example.aashish.instasaverapp.R;
import com.example.aashish.instasaverapp.entity.ImageData;
import com.example.aashish.instasaverapp.utils.AppConstants;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class FragmentHome extends Fragment {


    Button btn_Check;
    ImageView imageDownload;
    VideoView videoDownload;
    String imgUrl = "";
    int width;
    int height;


    public FragmentHome() {
    }

    // TODO: Rename and change types and number of parameters
    public static FragmentHome newInstance(ImageData imageData) {
        FragmentHome fragment = new FragmentHome();

        if (imageData != null) {
            Bundle bundle = new Bundle();
            bundle.putString("url", imageData.is_Video ? "Video:" + imageData.video_url : imageData.url);
            bundle.putInt("width", imageData.width);
            bundle.putInt("height", imageData.height);
            fragment.setArguments(bundle);
        }

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            imgUrl = getArguments().getString("url");
            width = getArguments().getInt("width");
            height = getArguments().getInt("height");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        if (AppConstants.SHOW_AD) {
            AdView mAdView = (AdView) view.findViewById(R.id.adView);
            mAdView.setVisibility(GONE);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
        }

        btn_Check = (Button) view.findViewById(R.id.btn_check);
        btn_Check.setVisibility(VISIBLE);

        view.findViewById(R.id.text_noimage).setVisibility(VISIBLE);


        imageDownload = view.findViewById(R.id.imageDownload);
        imageDownload.setVisibility(VISIBLE);
        videoDownload = view.findViewById(R.id.videoDownload);
        videoDownload.setVisibility(GONE);


        btn_Check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Snackbar.make(
                        view, // Parent view
                        "Checking...", // Message to show
                        Snackbar.LENGTH_LONG // How long to display the message.
                ).show();
            }
        });

        if (!imgUrl.equals("")) {
            btn_Check.setVisibility(GONE);
            view.findViewById(R.id.text_noimage).setVisibility(GONE);
            if (imgUrl.startsWith("Video:")) {
                String url = imgUrl.substring(6, imgUrl.length());
                videoDownload.setVideoURI(Uri.parse(url));
                videoDownload.setVisibility(VISIBLE);
                videoDownload.setOnPreparedListener(new OnPreparedListener() {
                    @Override
                    public void onPrepared() {
                        videoDownload.start();
                    }
                });
            } else {
                Picasso.get().load(imgUrl).resize(width, height).into(imageDownload);
                imageDownload.setVisibility(VISIBLE);
                imageDownload.getLayoutParams().height = height;
            }
        }
        return view;
    }

    public void loadImage(final String imgUrl, final View navigation) {


    }

}
