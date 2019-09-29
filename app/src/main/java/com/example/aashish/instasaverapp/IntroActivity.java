package com.example.aashish.instasaverapp;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.github.paolorotolo.appintro.model.SliderPage;

public class IntroActivity extends AppIntro {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SliderPage sliderPage = new SliderPage();
        sliderPage.setTitle(getResources().getString(R.string.intro_title1));
        sliderPage.setDescription(getResources().getString(R.string.intro_desc1));
        sliderPage.setTitleColor(getResources().getColor(R.color.colorAccent));
        sliderPage.setDescColor(getResources().getColor(R.color.text_color));
        sliderPage.setImageDrawable(R.drawable.intro1);
        sliderPage.setBgColor(getResources().getColor(R.color.bg));
        addSlide(AppIntroFragment.newInstance(sliderPage));

        SliderPage sliderPage2 = new SliderPage();
        sliderPage2.setTitle(getResources().getString(R.string.intro_title2));
        sliderPage2.setDescription(getResources().getString(R.string.intro_desc2));
        sliderPage2.setImageDrawable(R.drawable.intro2);
        sliderPage2.setBgColor(getResources().getColor(R.color.bg));
        sliderPage2.setTitleColor(getResources().getColor(R.color.colorAccent));
        sliderPage2.setDescColor(getResources().getColor(R.color.text_color));
        addSlide(AppIntroFragment.newInstance(sliderPage2));

        SliderPage sliderPage3 = new SliderPage();
        sliderPage3.setTitle(getResources().getString(R.string.intro_title3));
        sliderPage3.setDescription(getResources().getString(R.string.intro_desc3));
        sliderPage3.setImageDrawable(R.drawable.intro3);
        sliderPage3.setBgColor(getResources().getColor(R.color.bg));
        sliderPage3.setTitleColor(getResources().getColor(R.color.colorAccent));
        sliderPage3.setDescColor(getResources().getColor(R.color.text_color));
        addSlide(AppIntroFragment.newInstance(sliderPage3));


        // OPTIONAL METHODS
        // Override bar/separator color.
        setBarColor(getResources().getColor(R.color.colorAccent));
        setSeparatorColor(getResources().getColor(R.color.colorAccent));

        // Hide Skip/Done button.
        showSkipButton(true);
        setProgressButtonEnabled(true);

        // Turn vibration on and set intensity.
        // NOTE: you will probably need to ask VIBRATE permission in Manifest.
        setVibrate(true);
        setVibrateIntensity(30);

    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        this.startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        this.startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);

    }
}
