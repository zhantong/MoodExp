package cn.edu.nju.dislab.moodexp.registerandlogin;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cn.edu.nju.dislab.moodexp.R;

/**
 * Created by zhantong on 2016/12/24.
 */

public class RegisterFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.register_fragment,container,false);
        return view;
    }
}
