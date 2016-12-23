package cn.edu.nju.dislab.moodexp;

import android.app.Application;
import android.content.Context;

/**
 * Created by zhantong on 2016/12/21.
 */

public class MainApplication extends Application {
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    public static Context getContext() {
        return mContext;
    }
}
