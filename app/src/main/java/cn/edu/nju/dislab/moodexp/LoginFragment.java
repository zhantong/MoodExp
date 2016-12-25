package cn.edu.nju.dislab.moodexp;

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

import cn.edu.nju.dislab.moodexp.httputils.HttpAPI;

/**
 * Created by zhantong on 2016/12/24.
 */

public class LoginFragment extends Fragment {

    private View mView;

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

                DbHelper dbHelper=new DbHelper();
                SQLiteDatabase writableDb=dbHelper.getWritableDatabase();
                if(studentId!=null){
                    ContentValues values=new ContentValues();
                    values.put(DbHelper.UserTable.COLUMN_NAME_KEY,"id");
                    values.put(DbHelper.UserTable.COLUMN_NAME_VALUE,studentId);
                    values.put(DbHelper.UserTable.COLUMN_NAME_TIMESTAMP,System.currentTimeMillis());
                    writableDb.insertWithOnConflict(DbHelper.UserTable.TABLE_NAME,null,values,SQLiteDatabase.CONFLICT_REPLACE);
                }
                if(studentClass!=null){
                    ContentValues values=new ContentValues();
                    values.put(DbHelper.UserTable.COLUMN_NAME_KEY,"class");
                    values.put(DbHelper.UserTable.COLUMN_NAME_VALUE,studentClass);
                    values.put(DbHelper.UserTable.COLUMN_NAME_TIMESTAMP,System.currentTimeMillis());
                    writableDb.insertWithOnConflict(DbHelper.UserTable.TABLE_NAME,null,values,SQLiteDatabase.CONFLICT_REPLACE);
                }
                if(studentName!=null){
                    ContentValues values=new ContentValues();
                    values.put(DbHelper.UserTable.COLUMN_NAME_KEY,"name");
                    values.put(DbHelper.UserTable.COLUMN_NAME_VALUE,studentName);
                    values.put(DbHelper.UserTable.COLUMN_NAME_TIMESTAMP,System.currentTimeMillis());
                    writableDb.insertWithOnConflict(DbHelper.UserTable.TABLE_NAME,null,values,SQLiteDatabase.CONFLICT_REPLACE);
                }
                if(studentPhone!=null){
                    ContentValues values=new ContentValues();
                    values.put(DbHelper.UserTable.COLUMN_NAME_KEY,"phone");
                    values.put(DbHelper.UserTable.COLUMN_NAME_VALUE,studentPhone);
                    values.put(DbHelper.UserTable.COLUMN_NAME_TIMESTAMP,System.currentTimeMillis());
                    writableDb.insertWithOnConflict(DbHelper.UserTable.TABLE_NAME,null,values,SQLiteDatabase.CONFLICT_REPLACE);
                }
                MainApplication.getUserId(true);
                if(studentName!=null) {
                    Toast.makeText(getActivity(), "欢迎你，" +studentName+"。",Toast.LENGTH_SHORT).show();
                }
                getActivity().finish();
            }
        }
    }
}
