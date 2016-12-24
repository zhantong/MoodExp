package cn.edu.nju.dislab.moodexp;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;

/**
 * Created by zhantong on 2016/12/24.
 */

public class RegisterAndLoginActivity extends Activity{
    private static final String TAG="RegisterAndLogin";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_and_login);

        final FragmentManager fragmentManager=getFragmentManager();
        RadioGroup radioGroup=(RadioGroup)findViewById(R.id.tabs);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Log.i(TAG,checkedId+"");
                FragmentTransaction transaction=fragmentManager.beginTransaction();
                Fragment fragment=null;
                switch (checkedId){
                    case 1:
                        fragment=new RegisterFragment();
                        break;
                    case 2:
                        fragment=new LoginFragment();
                        break;
                }
                transaction.replace(R.id.content,fragment);
                transaction.commit();
            }
        });
        ((RadioButton)radioGroup.getChildAt(0)).setChecked(true);
    }
}
