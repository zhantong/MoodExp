package cn.edu.nju.dislab.moodexp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.JsonObject;

import cn.edu.nju.dislab.moodexp.httputils.HttpAPI;

/**
 * Created by zhantong on 2016/12/28.
 */

public class FeedbackActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        final EditText editTextFeedback=(EditText)findViewById(R.id.editText_feedback);
        Button buttonSubmit=(Button)findViewById(R.id.btn_submit);
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String feedback=editTextFeedback.getText().toString();
                new SubmitFeedback(FeedbackActivity.this).execute(feedback);
            }
        });
    }
    private class SubmitFeedback extends AsyncTask<String,Void,JsonObject> {
        private ProgressDialog mProgressDialog;
        private Context mContext;
        public SubmitFeedback(Context context){
            mContext =context;
            mProgressDialog=new ProgressDialog(mContext);
        }
        @Override
        protected void onPreExecute() {
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setMessage("正在提交...");
            mProgressDialog.show();
        }
        @Override
        protected JsonObject doInBackground(String... params) {
            String id=MainApplication.getUserId();
            String feedback=params[0];
            JsonObject result= HttpAPI.feedback(id,feedback);
            return result;
        }

        @Override
        protected void onPostExecute(JsonObject result) {
            mProgressDialog.dismiss();
            if (result == null) {
                Toast.makeText(mContext, "未知错误，请检查网络连接是否正常", Toast.LENGTH_SHORT).show();
            } else{
                Toast.makeText(mContext, "提交成功，感谢您的反馈！", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}
