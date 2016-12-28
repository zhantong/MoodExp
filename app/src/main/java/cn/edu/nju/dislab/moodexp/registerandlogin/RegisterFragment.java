package cn.edu.nju.dislab.moodexp.registerandlogin;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;

import cn.edu.nju.dislab.moodexp.R;
import cn.edu.nju.dislab.moodexp.httputils.HttpAPI;

/**
 * Created by zhantong on 2016/12/24.
 */

public class RegisterFragment extends Fragment {
    public interface OnRegisterSuccessListener{
        void onRegisterSuccess(String studentId,String studentName,String studentClass,String studentPhone);
    }
    private View mView;
    private OnRegisterSuccessListener mCallback;
    private EditText mEditTextId;
    private EditText mEditTextName;
    private EditText mEditTextPhone;
    private Button mButtonRegister;

    private TextWatcher mTextWatcher=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            checkEmptyValues();
        }
    };
    private void checkEmptyValues(){
        String id=mEditTextId.getText().toString();
        String name=mEditTextName.getText().toString();
        String phone=mEditTextPhone.getText().toString();
        if(id.equals("")||name.equals("")||phone.equals("")){
            mButtonRegister.setEnabled(false);
        }else{
            mButtonRegister.setEnabled(true);
        }
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView=inflater.inflate(R.layout.register_fragment,container,false);


        mButtonRegister=(Button)mView.findViewById(R.id.btn_register);
        mButtonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });

        mEditTextId=(EditText)mView.findViewById(R.id.text_id);
        mEditTextName=(EditText)mView.findViewById(R.id.text_name);
        mEditTextPhone=(EditText)mView.findViewById(R.id.text_phone);

        mEditTextId.addTextChangedListener(mTextWatcher);
        mEditTextName.addTextChangedListener(mTextWatcher);
        mEditTextPhone.addTextChangedListener(mTextWatcher);

        checkEmptyValues();
        return mView;
    }
    private void register(){

        final String id=mEditTextId.getText().toString();
        final String name=mEditTextName.getText().toString();
        final String phone=mEditTextPhone.getText().toString();
        AsyncTask registerTask=new RegisterTask(getActivity()).execute(id,name,phone);
    }
    private class RegisterTask extends AsyncTask<String,Object,JsonObject>{
        private ProgressDialog mProgressDialog;
        private Context mContext;
        public RegisterTask(Context context){
            mContext=context;
            mProgressDialog=new ProgressDialog(mContext);
        }

        @Override
        protected void onPreExecute() {
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setMessage("注册中...");
            mProgressDialog.show();
        }

        @Override
        protected JsonObject doInBackground(String... params) {
            String id=params[0];
            String name=params[1];
            String phone=params[2];

            JsonObject registerResult= HttpAPI.register("0",name,id,phone);
            if(registerResult!=null&&registerResult.get("status").getAsBoolean()){
                JsonObject studentInfo= HttpAPI.studentInfo(id);
                return studentInfo;
            }
            return registerResult;
        }

        @Override
        protected void onPostExecute(JsonObject studentInfo) {
            mProgressDialog.dismiss();
            if(studentInfo==null){
                Toast.makeText(getActivity(),"未知错误，请检查网络连接是否正常",Toast.LENGTH_SHORT).show();
            }else if(!studentInfo.get("status").getAsBoolean()){
                Toast.makeText(getActivity(),"注册失败，"+studentInfo.get("message"),Toast.LENGTH_SHORT).show();
            }else{
                String studentId=studentInfo.get("id").getAsString();
                String studentClass=studentInfo.get("class").getAsString();
                final String studentName=studentInfo.get("name").getAsString();
                String studentPhone=studentInfo.get("phone").getAsString();
                mCallback.onRegisterSuccess(studentId,studentName,studentClass,studentPhone);
            }
        }
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try{
            mCallback=(OnRegisterSuccessListener) activity;
        }catch (ClassCastException e){
            throw new ClassCastException(activity.toString()+" must implement OnRegisterSuccessListener");
        }
    }
}
