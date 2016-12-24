package cn.edu.nju.dislab.moodexp;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro;

/**
 * Created by zhantong on 2016/12/24.
 */

public class IntroActivity extends AppIntro {
    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }

    @Override
    public void init(@Nullable Bundle savedInstanceState) {
        showSkipButton(false);
        addSlide(IntroSilde.newInstance(R.layout.intro));
        addSlide(IntroSilde.newInstance(R.layout.intro2));
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        finish();
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        finish();
    }
}
