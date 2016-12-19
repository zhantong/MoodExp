package org.graduation.slide;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.graduation.R;
import org.graduation.database.DatabaseManager;
import org.graduation.database.SharedPreferenceManager;
import org.graduation.healthylife.Guide;
import org.graduation.healthylife.HttpRequest;
import org.graduation.healthylife.MainApplication;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.TELEPHONY_SERVICE;

public class ThirdSlide extends Fragment
{

    EditText TextEmail,TextPhoneNumber,TextStudentName,TextStudentId;
    Button btn;
    TextView t;
    String IMEI,SIM_Serial,WLAN_MAC,IP,Email,PhoneNumber,studentName,studentId;
    String className;
    boolean bIsGetStudentGroup=false,bIsFinish=false;
    public SharedPreferences shared;
    public SharedPreferences.Editor editor;
    private static final String HOST = "114.212.80.16";
    private static final int PORT = 9000;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.thirdslide, container, false);

        t=(TextView)view.findViewById(R.id.textViewInfo);
        TextEmail=(EditText)view.findViewById(R.id.editTextEmail);
        TextPhoneNumber=(EditText)view.findViewById(R.id.editTextPhone);
        TextStudentName=(EditText)view.findViewById(R.id.editTextStudentName);
        TextStudentId=(EditText)view.findViewById(R.id.editTextStudentId);


        shared=getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        editor=shared.edit();

        //TextEmail.setText(shared.getString("email","") );
        //TextPhoneNumber.setText(shared.getString("phoneNumber","") );



        DatabaseManager manager = DatabaseManager.getDatabaseManager();
        Cursor cursor=manager.queryPhoneInfo();

        TelephonyManager tm = (TelephonyManager) getActivity().getSystemService(TELEPHONY_SERVICE);

        String str="请正确填写邮箱地址和联系电话，我们在调查结束后会以邮箱或联系电话为依据发放活动奖品\n";
        str+="IMEI ："+tm.getDeviceId()+"\n";
        IMEI=tm.getDeviceId();

        str+="sim serial number ："+tm.getSimSerialNumber()+"\n";
        SIM_Serial=tm.getSimSerialNumber();

        try
        {
            str+="mac addr ："+getMac()+"   \n";
            WLAN_MAC=getMac();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        str+="ip:  ："+getHostIp()+"   \n";
        IP=getHostIp();

        //t.setText(str);

        btn=(Button)view.findViewById(R.id.button_submit_info);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                studentId=TextStudentId.getText().toString();
                studentName=TextStudentName.getText().toString();
                Email=TextEmail.getText().toString();
                PhoneNumber=TextPhoneNumber.getText().toString();

                if(studentName.length()==0)
                {
                    Toast.makeText(MainApplication.getContext(),"请填姓名",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(studentId.length()==0)
                {
                    Toast.makeText(MainApplication.getContext(),"请填学号",Toast.LENGTH_SHORT).show();
                    return;
                }

                if(Email.length()==0)
                {
                    Toast.makeText(MainApplication.getContext(),"请填写邮箱",Toast.LENGTH_SHORT).show();
                    return;
                }

                if(PhoneNumber.length()==0)
                {
                    Toast.makeText(MainApplication.getContext(),"请填写联系电话",Toast.LENGTH_SHORT).show();
                    return;
                }

                bIsGetStudentGroup=false;
                bIsFinish=false;
                new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Looper.prepare();
                        className=studentClass("test0");
                        if(className!=null)
                        {
                            Log.e("group","id: "+studentId+", class: "+className);
                            //Toast.makeText(MainApplication.getContext(),"学生组别"+className,Toast.LENGTH_SHORT).show();
                            bIsGetStudentGroup=true;
                            bIsFinish=true;

                        }
                        else
                        {
                            Log.e("group","id: "+studentId+", class not found, you may try again");
                            bIsGetStudentGroup=false;
                            bIsFinish=true;

                            return;
                        }
                        Looper.loop();
                    }
                }).start();


                while(bIsFinish==false);

                if(bIsGetStudentGroup==false)
                {
                    Toast.makeText(MainApplication.getContext(),"网络没有连接",Toast.LENGTH_SHORT).show();
                    return;
                }





                bIsGetStudentGroup=false;
                bIsFinish=false;
                new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Looper.prepare();
                        className=studentClass(studentId);
                        if(className!=null)
                        {
                            Log.e("group","id: "+studentId+", class: "+className);
                            //Toast.makeText(MainApplication.getContext(),"学生组别"+className,Toast.LENGTH_SHORT).show();
                            bIsGetStudentGroup=true;
                            bIsFinish=true;

                        }
                        else
                        {
                            Log.e("group","id: "+studentId+", class not found, you may try again");
                            bIsGetStudentGroup=false;
                            bIsFinish=true;

                            return;
                        }
                        Looper.loop();
                    }
                }).start();


                while(bIsFinish==false);

                if(bIsGetStudentGroup==false)
                {
                    Toast.makeText(MainApplication.getContext(),"学号错误",Toast.LENGTH_SHORT).show();
                    return;
                }

                editor.putString("studentId",studentId);
                editor.putString("studentName",studentName);
                editor.putString("email",Email);
                editor.putString("phoneNumber",PhoneNumber);

                editor.commit();


                SharedPreferenceManager sm=SharedPreferenceManager.getManager();
                sm.put("studentGroup",className);


                DatabaseManager manager = DatabaseManager.getDatabaseManager();
                manager.savePhoneInfo(IMEI,SIM_Serial,WLAN_MAC,IP,Email,PhoneNumber);
                manager.saveStudentInfo(studentName,studentId,Email,PhoneNumber);

                Cursor cursor=manager.queryStudentInfo();

                while(cursor.moveToNext())
                {
                    Toast.makeText(MainApplication.getContext(),"上传成功\n"
                                    +cursor.getString(0)+"\n"+cursor.getString(1)+"\n"+cursor.getString(2)
                                    +"\n"+cursor.getString(3)+"\n",
                            //+cursor.getString(4)+"\n"
                            //+cursor.getString(5),
                            Toast.LENGTH_SHORT).show();
                }

                Guide.isSubmit=true;

            }
        });





        return view;
    }

    public String studentClass(String id){
        JsonObject info=studentInfo(id);
        if(info!=null&&info.get("status").getAsBoolean()){
            return info.get("class").getAsString();
        }
        return null;
    }
    private JsonObject studentInfo(String id) {
        Map<String, String> params = new HashMap<>();
        params.put("id", id);

        HttpRequest request = new HttpRequest();
        try {
            JsonElement element = request.getReturnJson(HOST, PORT, "info", params);
            JsonObject result = element.getAsJsonObject();
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }



    public static String getHostIp()
    {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements();)
            {
                NetworkInterface intf = en.nextElement();

                for (Enumeration<InetAddress> ipAddr = intf.getInetAddresses(); ipAddr
                        .hasMoreElements();)
                {
                    InetAddress inetAddress = ipAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException ex) {
        } catch (Exception e) {
        }
        return null;
    }

    private String getMac() throws IOException {
        String macSerial = null;
        String str = "";

        try
        {
            Process pp = Runtime.getRuntime().exec("cat /sys/class/net/wlan0/address ");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);

            for (; null != str;)
            {
                str = input.readLine();
                if (str != null)
                {
                    macSerial = str.trim();// 去空格
                    break;
                }
            }
        } catch (IOException ex) {
            // 赋予默认值
            ex.printStackTrace();
        }
        return macSerial;
    }
}
