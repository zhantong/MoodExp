package cn.edu.nju.dislab.moodexp.permissionintro;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import agency.tango.materialintroscreen.SlideFragment;
import cn.edu.nju.dislab.moodexp.R;

/**
 * Created by zhantong on 2016/12/24.
 */

public class PermissionSlide extends SlideFragment {
    //private boolean canMoveFurther=false;
    private String mTitle;
    private String mMessage;
    private Intent mIntent;

    public void setTitle(String title) {
        mTitle = title;
    }

    public void setMessage(String message) {
        mMessage = message;
    }

    public void setIntent(Intent intent) {
        mIntent = intent;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.background_permission_slide, container, false);

        TextView textViewTitle = (TextView) view.findViewById(R.id.textView_title);
        TextView textViewMessage = (TextView) view.findViewById(R.id.textView_message);
        textViewTitle.setText(mTitle);
        textViewMessage.setText(mMessage);

        Button button = (Button) view.findViewById(R.id.button_permission_slide);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(mIntent);
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
