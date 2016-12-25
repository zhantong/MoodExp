package cn.edu.nju.dislab.moodexp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class MainActivity extends Activity {
    private static final String TAG="MainActivity";
    private static final int REQUEST_CODE_INTRO=1;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferences= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean isFirstStart=preferences.getBoolean("firstStart",true);
        if(isFirstStart){
            startActivityForResult(new Intent(this,IntroActivity.class),REQUEST_CODE_INTRO);
        }else{
            Log.i(TAG,MainApplication.getUserId());
            if(MainApplication.getUserId().isEmpty()) {
                checkRegisterAndLogin();
            }
            //startScheduledService();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.main_menu,menu);
        return true;
    }

    private void startScheduledService(){
        if (Build.VERSION.SDK_INT>Build.VERSION_CODES.LOLLIPOP_MR1) {
            String pkg=getPackageName();
            PowerManager pm=getSystemService(PowerManager.class);

            if (!pm.isIgnoringBatteryOptimizations(pkg)) {
                Intent i=
                        new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
                                .setData(Uri.parse("package:"+pkg));

                startActivity(i);
            }
        }
        startService(new Intent(this,ScheduledService.class));
    }
    private void checkRegisterAndLogin(){
        startActivity(new Intent(this,RegisterAndLoginActivity.class));
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==REQUEST_CODE_INTRO){
            SharedPreferences.Editor editor=preferences.edit();
            editor.putBoolean("firstStart",false);
            editor.apply();

            if(MainApplication.getUserId().isEmpty()) {
                checkRegisterAndLogin();
            }
            //startScheduledService();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.about:
                startActivity(new Intent(this,AboutActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
