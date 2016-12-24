package cn.edu.nju.dislab.moodexp;

import android.os.Bundle;
import android.support.annotation.Nullable;

import agency.tango.materialintroscreen.MaterialIntroActivity;
import agency.tango.materialintroscreen.SlideFragmentBuilder;

/**
 * Created by zhantong on 2016/12/24.
 */

public class IntroActivity extends MaterialIntroActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addSlide(new SlideFragmentBuilder()
                .backgroundColor(R.color.colorPrimary)
                .buttonsColor(R.color.colorAccent)
                .title(getString(R.string.terms_and_notification))
                .description(getString(R.string.terms_and_notification_hint_1))
                .build());
        addSlide(new SlideFragmentBuilder()
                        .backgroundColor(R.color.colorPrimary)
                        .buttonsColor(R.color.colorAccent)
                        .title(getString(R.string.terms_and_notification))
                        .description(getString(R.string.terms_and_notification_hint_2))
                        .build());
        addSlide(new BackgroundPermissionSlide());
        if(!MainApplication.isUsageStatsGranted()){
            addSlide(new UsagePermissionSlide());
        }
        addSlide(new SlideFragmentBuilder()
                .backgroundColor(R.color.colorPrimary)
                .buttonsColor(R.color.colorAccent)
                .title(getString(R.string.privacy_permission))
                .description(getString(R.string.privacy_permission_hint))
                .build());
    }
}
