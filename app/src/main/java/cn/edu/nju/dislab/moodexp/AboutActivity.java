package cn.edu.nju.dislab.moodexp;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Created by zhantong on 2016/12/25.
 */

public class AboutActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);

        TextView textViewVersionName=(TextView)findViewById(R.id.txt_version_name);
        textViewVersionName.setText(getString(R.string.display_version_name,MainApplication.getVersionName()));
    }
}
