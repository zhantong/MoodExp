package cn.edu.nju.dislab.moodexp;

import android.os.Bundle;
import android.support.annotation.Nullable;

import agency.tango.materialintroscreen.MaterialIntroActivity;

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
