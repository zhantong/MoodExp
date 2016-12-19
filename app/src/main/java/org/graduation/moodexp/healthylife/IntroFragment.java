package org.graduation.moodexp.healthylife;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.graduation.moodexp.R;

/**
 * Created by javan on 2016/6/13.
 */
public class IntroFragment extends Fragment
{
    TextView TextEmail,TextPhoneNumber,TextStudentName,TextStudentId;
    String Email="",PhoneNumber="",studentName="",studentId="";
    SharedPreferences shared;
    SharedPreferences.Editor editor;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.content_introduction,container,false);

        TextEmail=(TextView)view.findViewById(R.id.textViewEmail);
        TextPhoneNumber=(TextView)view.findViewById(R.id.textViewPhone);
        TextStudentName=(TextView)view.findViewById(R.id.textViewStudentName);
        TextStudentId=(TextView)view.findViewById(R.id.textViewStudentId);


        shared=getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        editor=shared.edit();

        studentId=TextStudentId.getText().toString();
        studentName=TextStudentName.getText().toString();
        Email=TextEmail.getText().toString();
        PhoneNumber+=TextPhoneNumber.getText().toString();

        studentId+=shared.getString("studentId","");
        studentName+=shared.getString("studentName","");
        Email+=shared.getString("email","") ;
        PhoneNumber+=shared.getString("phoneNumber","") ;

        TextStudentName.setText(studentName);
        TextStudentId.setText(studentId);
        TextEmail.setText(Email);
        TextPhoneNumber.setText(PhoneNumber);


        return view;
    }


}
