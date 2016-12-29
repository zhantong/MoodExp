package cn.edu.nju.dislab.moodexp.permissionintro;

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
import android.widget.TextView;

import agency.tango.materialintroscreen.SlideFragment;
import cn.edu.nju.dislab.moodexp.MainApplication;
import cn.edu.nju.dislab.moodexp.R;

import static cn.edu.nju.dislab.moodexp.permissionintro.IntentWrapper.getApplicationName;

/**
 * Created by zhantong on 2016/12/24.
 */

public class UsagePermissionSlide extends SlideFragment {
    private int mCount=0;
    //private boolean canMoveFurther=false;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.usage_permission_slide,container,false);

        TextView textViewTitle=(TextView)view.findViewById(R.id.textView_title);
        TextView textViewMessage=(TextView)view.findViewById(R.id.textView_message);
        String title="第 "+mCount+" 步\n"+"设置 "+ getApplicationName() + " 的统计信息权限";
        textViewTitle.setText(title);
        String textButton="现在设置";
        String message=getApplicationName() + " 需要分析应用的使用情况。\n\n" +
                "请点击『"+textButton+"』，"+
                "请点击『确定』，在弹出的『查看使用情况』列表中，将 " + getApplicationName() + " 对应的开关打开。";
        textViewMessage.setText(message);

        Button button=(Button)view.findViewById(R.id.button_permission_slide);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent().setAction(Settings.ACTION_USAGE_ACCESS_SETTINGS));
            }
        });
        return view;
    }
    public void setCount(int count){
        mCount=count;
    }

/*    @Override
    public boolean canMoveFurther() {
        return canMoveFurther;
    }*/
    @Override
    public String cantMoveFurtherErrorMessage() {
        return "请点击按钮";
    }
    @Override
    public int backgroundColor() {
        return R.color.lightGrey;
    }

    @Override
    public int buttonsColor() {
        return R.color.colorAccent;
    }
}
