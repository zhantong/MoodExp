package cn.edu.nju.dislab.moodexp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.gson.JsonObject;

import cn.edu.nju.dislab.moodexp.httputils.HttpAPI;

/**
 * Created by zhantong on 2016/12/26.
 */

public class CheckUpdate extends AsyncTask<Void,Void,JsonObject> {
    private ProgressDialog mProgressDialog;
    private Context mContext;
    private boolean mIsShowNoUpdateNotification;
    public CheckUpdate(Context context, boolean isShowNoUpdateNotification){
        mContext =context;
        mProgressDialog=new ProgressDialog(mContext);
        mIsShowNoUpdateNotification=isShowNoUpdateNotification;
    }
    @Override
    protected void onPreExecute() {
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("正在检查...");
        mProgressDialog.show();
    }
    @Override
    protected JsonObject doInBackground(Void... params) {
        String id=MainApplication.getUserId();
        String version= MainApplication.getVersionName();
        JsonObject result= HttpAPI.checkUpdate(id,version);
        return result;
    }

    @Override
    protected void onPostExecute(JsonObject result) {
        mProgressDialog.dismiss();
        if(result==null){
            if(mIsShowNoUpdateNotification) {
                Toast.makeText(mContext, "未知错误，请检查网络连接是否正常", Toast.LENGTH_SHORT).show();
            }
        }else if(!result.get("status").getAsBoolean()){
            if(mIsShowNoUpdateNotification) {
                Toast.makeText(mContext, "检查更新失败", Toast.LENGTH_SHORT).show();
            }
        }else if(!result.get("has_update").getAsBoolean()) {
            if(mIsShowNoUpdateNotification) {
                Toast.makeText(mContext, "已经是最新版本", Toast.LENGTH_SHORT).show();
            }
        }else{
            String latestVersion=result.get("latest_version").getAsString();
            final String latestUrl=result.get("latest_url").getAsString();
            new AlertDialog.Builder(mContext)
                    .setTitle("发现新版本")
                    .setMessage("发现新版本 "+latestVersion+" 是否立即更新？")
                    .setNegativeButton("取消",null)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            new DownloadAndInstallAPK(mContext).execute(latestUrl);
                        }
                    })
                    .show();
        }
    }
}
