package cn.edu.nju.dislab.moodexp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by zhantong on 2016/12/24.
 */

public class IntroSilde extends Fragment {
    private static final String ARG_LAYOUT_RES_ID = "layoutResId";
    private int layoutResId;
    public static IntroSilde newInstance(int layoutResId) {
        IntroSilde introSilde = new IntroSilde();

        Bundle args = new Bundle();
        args.putInt(ARG_LAYOUT_RES_ID, layoutResId);
        introSilde.setArguments(args);

        return introSilde;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null && getArguments().containsKey(ARG_LAYOUT_RES_ID))
            layoutResId = getArguments().getInt(ARG_LAYOUT_RES_ID);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(layoutResId, container, false);
    }
}
