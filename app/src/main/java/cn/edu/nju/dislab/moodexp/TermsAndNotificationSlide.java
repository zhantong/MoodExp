package cn.edu.nju.dislab.moodexp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import agency.tango.materialintroscreen.SlideFragment;

/**
 * Created by zhantong on 2016/12/27.
 */

public class TermsAndNotificationSlide extends SlideFragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.terms_and_notification_slide, container, false);
        return view;
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
