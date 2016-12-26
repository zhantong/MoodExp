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
        SlideFragmentBuilder builder=new SlideFragmentBuilder()
                .backgroundColor(R.color.colorPrimary)
                .buttonsColor(R.color.colorAccent)
                .title(getString(R.string.privacy_permission))
                .description(getString(R.string.privacy_permission_hint));
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            builder.neededPermissions(new String[]{Manifest.permission.READ_CONTACTS,
                    Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.RECORD_AUDIO,Manifest.permission.READ_PHONE_STATE,Manifest.permission.READ_CALL_LOG,
            Manifest.permission.READ_SMS,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE});
        }
        addSlide(builder.build());
    }
}
