package cn.edu.nju.dislab.moodexp;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;

import agency.tango.materialintroscreen.MaterialIntroActivity;
import agency.tango.materialintroscreen.SlideFragmentBuilder;

/**
 * Created by zhantong on 2016/12/24.
 */

public class FirstTimeIntroActivity extends MaterialIntroActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addSlide(new TermsAndNotificationSlide());
        addSlide(new PrivacyPermissionSilde());
    }
}
