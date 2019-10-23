package app.repostit.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.io.Serializable;

import app.repostit.AppActivity;
import app.repostit.R;
import app.repostit.entity.ImageData;
import app.repostit.listener.OnImageClickListener;
import app.repostit.ui.adapter.AdapterFeed;
import app.repostit.utils.AppConstants;
import app.repostit.utils.Extras;
import app.repostit.utils.Util;
import app.repostit.widget.CustomTextView;

public class FeedActivity extends Fragment implements View.OnClickListener {

    LinearLayout ll_empty;
    CustomTextView btn_start;
    SwipeRefreshLayout srl;
    RecyclerView recycler;
    InterstitialAd mInterstitialAd;

    public FeedActivity() {
    }


    // TODO: Rename and change types and number of parameters
    public static FeedActivity newInstance() {
        FeedActivity fragment = new FeedActivity();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (AppConstants.SHOW_AD) {
            mInterstitialAd = new InterstitialAd(getContext());

            mInterstitialAd.setAdUnitId(getString(R.string.interstitial_detail));

            mInterstitialAd.loadAd(new AdRequest.Builder().build());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_feed, container, false);


        AdView mAdView = (AdView) view.findViewById(R.id.adView_feed);
        if (AppConstants.SHOW_AD) {
            mAdView.setVisibility(View.VISIBLE);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
        }else{
            mAdView.setVisibility(View.GONE);
        }

        ll_empty = view.findViewById(R.id.ll_empty);
        btn_start = view.findViewById(R.id.btn_start);
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Util.openInstagram(getContext());
            }
        });

        srl = view.findViewById(R.id.srl);
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ((AppActivity) getActivity()).refresh();
                srl.setRefreshing(false);
            }
        });

        recycler = view.findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));

        updateList();

        return view;
    }

    public void updateList() {

        ll_empty.setVisibility(View.VISIBLE);
        recycler.setVisibility(View.GONE);

        ImageData.getAllCategory(ImageData.getAllImageList());

        if (ImageData.getAllProfileList().size() > 0) {
            ll_empty.setVisibility(View.GONE);
            recycler.setVisibility(View.VISIBLE);
        }
        AdapterFeed adapterImages = new AdapterFeed(ImageData.getAllProfileList(), new OnImageClickListener() {
            @Override
            public void onImageClick(ImageData image) {

                if (mInterstitialAd != null) mInterstitialAd.show();
                showImageDetailFragment(image);
            }
        });
        recycler.setAdapter(adapterImages);
    }

    public void showImageDetailFragment(final ImageData image) {

        Intent intent = new Intent(getActivity(), Fragment_Detail.class);
        intent.putExtra(Extras.IMAGE_DATA, (Serializable) image);
        getActivity().startActivity(intent);


       /* FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        Fragment_Detail frag = Fragment_Detail.newInstance(image);
        frag.setOnDeleteListener(new OnDeleteListener() {
            @Override
            public void onDeleteImageListener(ImageData imageData) {
                getActivity().onBackPressed();
                Iterator<ImageData> iterator = ImageData.getAllImageList().iterator();
                while (iterator.hasNext()) {
                    ImageData imageData1 = iterator.next();
                    if (imageData1.name.equals(imageData.name)) {
                        ImageData.getAllImageList().remove(imageData);
                        imageData.delete();
                        imageData.save();
                        break;
                        // To Bad
                    }
                }
                updateList();
            }
        });
        transaction.add(R.id.container, frag, "").addToBackStack("");
        transaction.commit();*/
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_start:

                break;
        }
    }
}
