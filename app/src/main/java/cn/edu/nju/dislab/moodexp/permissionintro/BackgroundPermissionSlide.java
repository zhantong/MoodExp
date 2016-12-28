package cn.edu.nju.dislab.moodexp.permissionintro;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import agency.tango.materialintroscreen.SlideFragment;
import cn.edu.nju.dislab.moodexp.R;

import static cn.edu.nju.dislab.moodexp.permissionintro.IntentWrapper.*;

/**
 * Created by zhantong on 2016/12/24.
 */

public class BackgroundPermissionSlide extends SlideFragment{
    //private boolean canMoveFurther=false;
    private String mTitle;
    private String mMessage;
    private IntentWrapper mIntentWrapper;

    public void setTitle(String title){
        mTitle=title;
    }
    public void setMessage(String message){
        mMessage=message;
    }
    public void setIntentWrapper(IntentWrapper intentWrapper){
        mIntentWrapper=intentWrapper;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.background_permission_slide,container,false);

        TextView textViewTitle=(TextView)view.findViewById(R.id.textView_title);
        TextView textViewMessage=(TextView)view.findViewById(R.id.textView_message);
        textViewTitle.setText(mTitle);
        textViewMessage.setText(mMessage);

        Button button=(Button)view.findViewById(R.id.button_permission_slide);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mIntentWrapper.startActivity(getActivity());
            }
        });
        return view;
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
