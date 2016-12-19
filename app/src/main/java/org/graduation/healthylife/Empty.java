package org.graduation.healthylife;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.graduation.R;

/**
 * Created by javan on 2016/6/13.
 */
public class Empty extends Fragment
{
    TextView TextEmail,TextPhoneNumber,TextStudentName,TextStudentId;
    String Email="",PhoneNumber="",studentName="",studentId="";
    SharedPreferences shared;
    SharedPreferences.Editor editor;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.empty_fragment,container,false);

        return view;
    }


}
