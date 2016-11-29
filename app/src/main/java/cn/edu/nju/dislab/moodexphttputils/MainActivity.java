package cn.edu.nju.dislab.moodexphttputils;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MainActivity extends Activity {
    private static final String TAG = "MoodExp";
    private static final String BASE_URL="http://114.212.80.16:9000";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnTest = (Button) findViewById(R.id.button_test);
        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        testHttpRequest();
                    }
                });
                thread.start();
                Toast.makeText(getApplicationContext(), "clicked", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void testHttpRequest() {
        boolean resultBoolean;
        JsonElement resultJson;

        if(false) {
            resultBoolean = register("一班", "我", "150001", "13888888888");
            if (resultBoolean) {
                Log.d(TAG, "success");
            }
            resultBoolean = register("二班", "你", "150003", "18666666666");
            if (resultBoolean) {
                Log.d(TAG, "success");
            }
        }



        if(false) {
            String[] fileNames = {"150001_1.db", "150001_3.db", "150003_2.db", "150003_4.db"};
            for (String fileName : fileNames) {
                resultBoolean = upload(Utils.combinePaths(Environment.getExternalStorageDirectory().getAbsolutePath(), fileName),fileName);
                if (resultBoolean) {
                    Log.d(TAG, "uploaded " + fileName);
                }
            }
        }

        if(false) {
            String[] fileNames = {"150001.db", "150003.db"};
            for (String fileName : fileNames) {
                resultBoolean = download(fileName, Utils.combinePaths(Environment.getExternalStorageDirectory().getAbsolutePath(), fileName));
                if (resultBoolean) {
                    Log.d(TAG, "downloaded " + fileName);
                }
            }
        }

        if(true) {
            resultJson = statistic();
            if(resultJson!=null) {
                JsonArray students=resultJson.getAsJsonArray();
                for (Iterator<JsonElement> it = students.iterator(); it.hasNext(); ) {
                    JsonObject student=it.next().getAsJsonObject();
                    Log.d(TAG, "name: " + student.get("name").getAsString());
                    Log.d(TAG, "class: " + student.get("class").getAsString());
                    Log.d(TAG, "id: " + student.get("id").getAsString());
                    Log.d(TAG, "phone: " + student.get("phone").getAsString());
                    JsonArray counts= student.getAsJsonArray("count");
                    Type intListType=new TypeToken<ArrayList<Integer>>() {}.getType();
                    List<Integer> countsList=new Gson().fromJson(counts,intListType);
                    Log.d(TAG,"counts: "+countsList.toString());
                }
            }
        }
        if(false){
            String[] ids={"150001","150002"};
            for(String id:ids){
                String className=studentClass(id);
                if(className!=null){
                    Log.d(TAG,"id: "+id+", class: "+className);
                }else{
                    Log.d(TAG,"id: "+id+", class not found, you may try again");
                }
            }
        }
        if(false){
            String[] ids={"150001","150002"};
            for(String id:ids){
                resultBoolean=delete(id);
                if(resultBoolean){
                    Log.d(TAG,"successfully deleted "+id);
                }
            }
        }
    }

    public boolean register(String class_name, String name, String id, String phone) {
        Map<String, String> params = new HashMap<>();
        params.put("class", class_name);
        params.put("name", name);
        params.put("id", id);
        params.put("phone", phone);

        HttpRequest request = new HttpRequest();
        try {
            JsonElement element = request.get(BASE_URL+"/register", params);
            JsonObject result=element.getAsJsonObject();
            return result.get("status").getAsBoolean();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public JsonElement statistic() {
        HttpRequest request = new HttpRequest();
        try {
            JsonElement element = request.get(BASE_URL+"/statistic", null);
            return element;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean upload(String filePath,String fileName) {
        Map<String, String> params = new HashMap<>();
        params.put("filename", fileName);

        HttpRequest request = new HttpRequest();
        try {
            JsonElement element = request.upload(BASE_URL+"/upload", filePath, params);
            JsonObject result=element.getAsJsonObject();
            return result.get("status").getAsBoolean();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean download(String fileName, String filePath) {
        HttpRequest request = new HttpRequest();
        try {
            request.download(BASE_URL+"/uploads/" + fileName, filePath);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    public String studentClass(String id){
        JsonObject info=studentInfo(id);
        if(info!=null&&info.get("status").getAsBoolean()){
            return info.get("class").getAsString();
        }
        return null;
    }
    private JsonObject studentInfo(String id){
        Map<String, String> params = new HashMap<>();
        params.put("id",id);

        HttpRequest request = new HttpRequest();
        try {
            JsonElement element = request.get(BASE_URL+"/info", params);
            JsonObject result=element.getAsJsonObject();
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    public boolean delete(String id){
        Map<String, String> params = new HashMap<>();
        params.put("id",id);

        HttpRequest request = new HttpRequest();
        try {
            JsonElement element = request.get(BASE_URL+"/delete", params);
            JsonObject result=element.getAsJsonObject();
            return result.get("status").getAsBoolean();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
