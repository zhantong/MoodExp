package cn.edu.nju.dislab.moodexp.registerandlogin;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;

import cn.edu.nju.dislab.moodexp.R;
import cn.edu.nju.dislab.moodexp.httputils.HttpAPI;
import cn.edu.nju.dislab.moodexp.survey.OnSubmitAnswerListener;

/**
 * Created by zhantong on 2016/12/24.
 */

public class LoginFragment extends Fragment {
    public interface OnLoginSuccessListener{
        void onLoginSuccess(String studentId,String studentName,String studentClass,String studentPhone);
    }
    private View mView;
    private OnLoginSuccessListener mCallback;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView=inflater.inflate(R.layout.login_fragment,container,false);


        Button buttonLogin=(Button)mView.findViewById(R.id.btn_login);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        return mView;
    }
    private void login(){
        TextView textViewId=(TextView)mView.findViewById(R.id.text_id);
        final String id=textViewId.getText().toString();
        AsyncTask loginTask=new LoginTask().execute(id);
    }
    private class LoginTask extends AsyncTask<String,Object,JsonObject>{
        private ProgressDialog mProgressDialog;
        public LoginTask(){
            mProgressDialog=new ProgressDialog(getActivity());
        }

        @Override
        protected void onPreExecute() {
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setMessage("登录中...");
            mProgressDialog.show();
        }

        @Override
        protected JsonObject doInBackground(String... params) {
            String id=params[0];
            JsonObject studentInfo=HttpAPI.studentInfo(id);
            return studentInfo;
        }

        @Override
        protected void onPostExecute(JsonObject studentInfo) {
            mProgressDialog.dismiss();
            if(studentInfo==null){
                Toast.makeText(getActivity(),"未知错误，请检查网络连接是否正常",Toast.LENGTH_SHORT).show();
            }else if(!studentInfo.get("status").getAsBoolean()){
                Toast.makeText(getActivity(),"登录失败",Toast.LENGTH_SHORT).show();
            }else{

                String studentId=studentInfo.get("id").getAsString();
                String studentClass=studentInfo.get("class").getAsString();
                final String studentName=studentInfo.get("name").getAsString();
                String studentPhone=studentInfo.get("phone").getAsString();
                mCallback.onLoginSuccess(studentId,studentName,studentClass,studentPhone);
            }
        }
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try{
            mCallback=(OnLoginSuccessListener) activity;
        }catch (ClassCastException e){
            throw new ClassCastException(activity.toString()+" must implement OnLoginSuccessListener");
        }
    }
}
