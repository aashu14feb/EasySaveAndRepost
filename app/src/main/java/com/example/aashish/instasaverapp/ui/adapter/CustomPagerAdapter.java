package com.example.aashish.instasaverapp.ui.adapter;

import android.content.Context;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.aashish.instasaverapp.entity.ImageData;
import com.example.aashish.instasaverapp.ui.fragment.FragmentFeed;
import com.example.aashish.instasaverapp.ui.fragment.FragmentHome;

public class CustomPagerAdapter extends FragmentStatePagerAdapter {

    private Context mContext;
    final int PAGE_COUNT = 2;

    ImageData imageData;
    int width = 0;
    int  height = 0;

    public CustomPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        mContext = context;
    }

    public void handleIntent(ImageData imageData) {
        this.imageData = imageData;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                FragmentHome fragment1 = FragmentHome.newInstance(imageData);
                return fragment1;

            case 1:
                FragmentFeed fragment2 = new FragmentFeed();
                return fragment2;
            default:
                return null;
        }
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }


    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {

        switch (position) {
            case 0:
                return "Home";
            case 1:
                return "Saved";
            default:
                return "";
        }
    }
}
