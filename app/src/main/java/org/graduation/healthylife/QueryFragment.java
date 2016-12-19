package org.graduation.healthylife;

import android.app.Fragment;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.graduation.R;
import org.graduation.database.DatabaseManager;
import org.graduation.database.SharedPreferenceManager;

/**
 * Created by javan on 2016/6/13.
 */
public class QueryFragment extends Fragment
{
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.content_query, container, false);
        TextView textView=(TextView)view.findViewById(R.id.emotionTv);
        TextView idTv=(TextView)view.findViewById(R.id.idTv);

        idTv.setText("ID:"+SharedPreferenceManager.getManager().getString("phoneID", null));
        String text="";

        Cursor cursor= DatabaseManager.getDatabaseManager().queryEmotion();
        int cnt=1;

        while(cursor.moveToNext())
        {
            text+="  "+cnt;
            for(int i=1;i<=6;i++)
            {
                text+="     ";
                int emotion=cursor.getInt(i);
                if(emotion==0) text+="无";
                else if(emotion==1) text+="低";
                else if(emotion==2) text+="中";
                else if(emotion==3) text+="高";
            }
            text+="\n";
            cnt++;
        }
        textView.setText(text);
        return view;
    }
}
