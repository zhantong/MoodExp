package cn.edu.nju.dislab.moodexp.permissionintro;

import android.os.Bundle;
import android.support.annotation.Nullable;

import agency.tango.materialintroscreen.MaterialIntroActivity;
import cn.edu.nju.dislab.moodexp.MainApplication;

/**
 * Created by zhantong on 2016/12/26.
 */

public class PermissionIntroActivity extends MaterialIntroActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addSlide(new BackgroundPermissionSlide());
        if(!MainApplication.isUsageStatsGranted()){
            addSlide(new UsagePermissionSlide());
        }
    }
}
