package cn.edu.nju.dislab.moodexp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import cn.edu.nju.dislab.moodexp.httputils.HttpAPI;

import static cn.edu.nju.dislab.moodexp.ScheduledService.gzip;

/**
 * Created by zhantong on 2016/12/25.
 */

public class AboutActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);

        TextView textViewVersionName = (TextView) findViewById(R.id.txt_version_name);
        textViewVersionName.setText(getString(R.string.display_version_name, MainApplication.getVersionName()));

        Button buttonUploadLog = (Button) findViewById(R.id.btn_upload_log);
        buttonUploadLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new UploadLog(AboutActivity.this).execute();
            }
        });
    }

    private class UploadLog extends AsyncTask<Void, Void, Boolean> {
        private ProgressDialog mProgressDialog;
        private Context mContext;

        public UploadLog(Context context) {
            mContext = context;
            mProgressDialog = new ProgressDialog(mContext);
        }

        @Override
        protected void onPreExecute() {
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setMessage("上传中...");
            mProgressDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            File logDir = new File(getFilesDir(), "log");
            File[] files = logDir.listFiles();
            if (files != null) {
                File tempDir = MainApplication.getContext().getCacheDir();
                String id = MainApplication.getUserId();
                String version = MainApplication.getVersionName();
                for (File file : files) {
                    File gzipFile = new File(tempDir, file.getName() + ".gz");
                    if (gzipFile.exists()) {
                        gzipFile.delete();
                    }
                    boolean result = gzip(file.getAbsolutePath(), gzipFile.getAbsolutePath());
                    String uploadFilePath;
                    if (result) {
                        uploadFilePath = gzipFile.getAbsolutePath();
                    } else {
                        uploadFilePath = file.getAbsolutePath();
                    }
                    boolean isUploadSuccess = HttpAPI.uploadLog(uploadFilePath, id, version);
                    if (!isUploadSuccess) {
                        return false;
                    }
                    if (gzipFile.exists()) {
                        gzipFile.delete();
                    }
                }
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            mProgressDialog.dismiss();
            if (result == null) {
                Toast.makeText(mContext, "未知错误，请检查网络连接是否正常", Toast.LENGTH_SHORT).show();
            } else if (!result) {
                Toast.makeText(mContext, "上传失败，请重试", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mContext, "上传成功", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
