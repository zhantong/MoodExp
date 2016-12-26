package cn.edu.nju.dislab.moodexp.registerandlogin;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import cn.edu.nju.dislab.moodexp.R;

/**
 * Created by zhantong on 2016/12/24.
 */

public class RegisterAndLoginActivity extends Activity implements LoginFragment.OnLoginSuccessListener{
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
        ((RadioButton)radioGroup.getChildAt(0)).setEnabled(false);
        ((RadioButton)radioGroup.getChildAt(1)).setChecked(true);
    }

    @Override
    public void onLoginSuccess(String studentId, String studentName, String studentClass, String studentPhone) {
        Intent intent=new Intent();
        intent.putExtra("id",studentId);
        intent.putExtra("name",studentName);
        intent.putExtra("class",studentClass);
        intent.putExtra("phone",studentPhone);
        setResult(Activity.RESULT_OK,intent);
        finish();
    }
}
