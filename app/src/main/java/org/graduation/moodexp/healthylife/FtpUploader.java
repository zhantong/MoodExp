package org.graduation.moodexp.healthylife;

import android.database.Cursor;
import android.util.Log;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.graduation.moodexp.collector.MyDateManager;
import org.graduation.moodexp.database.DatabaseManager;
import org.graduation.moodexp.database.HealthyLifeDBHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Thread.sleep;

/**
 * Created by javan on 2016/6/13.
 */
public class FtpUploader
{
    private final static String addr="114.212.80.16";
    private final static int port=21;
    //private final static String TAG="upload database";

    private static final String TAG = "MoodExp";
    //private ObjectMapper mapper = new ObjectMapper();
    private static final String HOST = "114.212.80.16";
    private static final int PORT = 9000;
    String filePath =MainApplication.getContext().getDatabasePath(HealthyLifeDBHelper.DATABASE_NAME).toString();

    boolean result;
    Cursor c;

    public boolean upload(String filePath, String id, int count, String version) {
        Map<String, String> params = new HashMap<>();
        params.put("id", id);
        params.put("count", Integer.toString(count));
        params.put("version", version);

        HttpRequest request = new HttpRequest();
        try {
            JsonElement element = request.postReturnJson(HOST, PORT, "upload", params, filePath);
            JsonObject result = element.getAsJsonObject();
            Log.e("sub","upload status: "+result.get("status").getAsBoolean());
            if (result.get("status").getAsBoolean())
            {
                String serverSHA1 = result.get("sha1").getAsString();
                String localSHA1 = Utils.fileToSHA1(filePath);


                if (localSHA1 != null && localSHA1.toLowerCase().equals(serverSHA1.toLowerCase()))
                {
                    return true;
                }
                Log.e("sub",""+serverSHA1+" "+localSHA1);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //return true;
        return false;
    }

    public boolean upload()
    {

        result=upload(filePath,getStudnetId(),getSubmitId(),MyUtil.version);

        Log.e("sub","sub result: "+result);

        DatabaseManager sm=DatabaseManager.getDatabaseManager();

//        c=sm.queryAcceleration();
//        int j=0;
//        while (c.moveToNext()!=false)
//        {j++;
//            //Log.e("sub","before refresh"+c.getString(1));
//        }Log.e("sub","before refresh:"+j);

        if(result==true)
        {
            //sm.refresh();
            Log.e("sub","refresh");
        }
//        c=sm.queryAcceleration();
//        j=0;
//        while (c.moveToNext()!=false)
//        {j++;
//            //Log.e("sub","after refresh"+c.getString(1));
//        }Log.e("sub","after refresh:"+j);

        return result;
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

    public String getPhoneId()
    {
        Cursor cursor=DatabaseManager.getDatabaseManager().queryPhoneInfo();
//        private static final String CREATE_PhoneInfo="create table phoneInfo ("
//                +"IMEI varchar(80),"
//                +"SIM_serial varchar(80),"
//                +"WLAN_MAC varchar(80),"
//                +"IP varchar(80),"
//                +"Email varchar(80),"
//                +"Phone_Number varchar(80))";

        cursor.moveToNext();
        return cursor.getString(5);

    }

    public String getStudnetId()
    {
        Cursor cursor=DatabaseManager.getDatabaseManager().queryStudentInfo();
//        private static final String CREATE_StudentInfo="create table studentInfo ("
//                +"name varchar(80),"
//                +"id varchar(80),"
//                +"email varchar(80),"
//                +"phoneNumber varchar(80))";

        cursor.moveToNext();
        return cursor.getString(1);

    }

    public int getSubmitId()
    {
        int sub=MyDateManager.getIntervalDaysFromBase()*3+OptionFragment.getTimeSection();
        return sub;

//        Cursor cursor=DatabaseManager.getDatabaseManager().queryDailyTime();
//        int count=0;
//        while(cursor.moveToNext())
//        {
//            count++;
//        }
//        return count;
    }



    public static void copyfile(File fromFile, File toFile, Boolean rewrite )
    {

        if (!fromFile.exists()) {

            return;

        }

        if (!fromFile.isFile()) {

            return ;

        }

        if (!fromFile.canRead()) {

            return ;

        }

        if (!toFile.getParentFile().exists()) {
            toFile.getParentFile().mkdirs();
        }

        if (toFile.exists() && rewrite) {
            toFile.delete();
        }

        //当文件不存时，canWrite一直返回的都是false

        // if (!toFile.canWrite()) {

        // MessageDialog.openError(new Shell(),"错误信息","不能够写将要复制的目标文件" + toFile.getPath());

        // Toast.makeText(this,"不能够写将要复制的目标文件", Toast.LENGTH_SHORT);

        // return ;

        // }

        try
        {
            java.io.FileInputStream fosfrom = new java.io.FileInputStream(fromFile);
            java.io.FileOutputStream fosto = new FileOutputStream(toFile);
            byte bt[] = new byte[1024];

            int c;

            while ((c = fosfrom.read(bt)) > 0)
            {
                fosto.write(bt, 0, c); //将内容写到新文件当中
            }
            fosfrom.close();
            fosto.close();
        }
        catch (Exception ex)
        {
            Log.e("readfile", ex.getMessage());
        }
    }

}
