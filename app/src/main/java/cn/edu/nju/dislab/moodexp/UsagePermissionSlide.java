package cn.edu.nju.dislab.moodexp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import agency.tango.materialintroscreen.SlideFragment;

import static cn.edu.nju.dislab.moodexp.IntentWrapper.getApplicationName;

/**
 * Created by zhantong on 2016/12/24.
 */

public class UsagePermissionSlide extends SlideFragment {
    private boolean canMoveFurther=false;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.usage_permission_slide,container,false);
        Button button=(Button)view.findViewById(R.id.button_permission_slide);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                usageStatsCheck();
                canMoveFurther=true;
            }
        });
        return view;
    }

    @Override
    public boolean canMoveFurther() {
        return canMoveFurther;
    }
    @Override
    public String cantMoveFurtherErrorMessage() {
        return "请点击按钮";
    }
    @Override
    public int backgroundColor() {
        return R.color.colorPrimary;
    }

    @Override
    public int buttonsColor() {
        return R.color.colorAccent;
    }

    private void usageStatsCheck(){
        if(!MainApplication.isUsageStatsGranted()) {
            new AlertDialog.Builder(getContext())
                    .setCancelable(false)
                    .setTitle(getApplicationName() + " 需要查看系统统计信息")
                    .setMessage(getApplicationName() + " 需要分析应用的使用情况。\n\n" +
                            "请点击『确定』，在弹出的『查看使用情况』列表中，将 " + getApplicationName() + " 对应的开关打开。")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startActivity(new Intent().setAction(Settings.ACTION_USAGE_ACCESS_SETTINGS));
                        }
                    })
                    .show();
        }
    }
}
