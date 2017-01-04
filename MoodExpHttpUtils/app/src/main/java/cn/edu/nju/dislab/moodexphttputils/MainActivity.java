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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MainActivity extends Activity {
    private static final String TAG = "MoodExp";
    private static final String HOST = "114.212.80.16";
    private static final int PORT = 9000;

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

    public static JsonObject register(String class_name, String name, String id, String phone) {
        Map<String, String> params = new HashMap<>();
        params.put("class", class_name);
        params.put("name", name);
        params.put("id", id);
        params.put("phone", phone);

        HttpRequest request = new HttpRequest();
        try {
            JsonElement element = request.getReturnJson(HOST, PORT, "register", params);
            JsonObject result = element.getAsJsonObject();
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static JsonElement statistic() {
        HttpRequest request = new HttpRequest();
        try {
            return request.getReturnJson(HOST, PORT, "statistic", null);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean upload(String filePath, String id, int count, String version) {
        Map<String, String> params = new HashMap<>();
        params.put("id", id);
        params.put("count", Integer.toString(count));
        params.put("version", version);

        HttpRequest request = new HttpRequest();
        try {
            JsonElement element = request.postReturnJson(HOST, PORT, "upload", params, filePath);
            JsonObject result = element.getAsJsonObject();
            if (result.get("status").getAsBoolean()) {
                String serverSHA1 = result.get("sha1").getAsString();
                String localSHA1 = Utils.fileToSHA1(filePath);
                if (localSHA1 != null && localSHA1.toLowerCase().equals(serverSHA1.toLowerCase())) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean download(String id, int count, String version, String filePath) {
        Map<String, String> params = new HashMap<>();
        params.put("id", id);
        params.put("count", Integer.toString(count));
        params.put("version", version);

        HttpRequest request = new HttpRequest();
        try {
            request.download(HOST, PORT, "download", params, filePath);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String studentClass(String id) {
        JsonObject info = studentInfo(id);
        if (info != null && info.get("status").getAsBoolean()) {
            return info.get("class").getAsString();
        }
        return null;
    }

    public static JsonObject studentInfo(String id) {
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

    public static boolean delete(String id) {
        Map<String, String> params = new HashMap<>();
        params.put("id", id);

        HttpRequest request = new HttpRequest();
        try {
            JsonElement element = request.getReturnJson(HOST, PORT, "delete", params);
            JsonObject result = element.getAsJsonObject();
            return result.get("status").getAsBoolean();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String getReleaseVersion() {
        return getVersion("release");
    }

    public static String getDebugVersion() {
        return getVersion("debug");
    }

    private static String getVersion(String type) {
        Map<String, String> params = new HashMap<>();
        params.put("type", type);

        HttpRequest request = new HttpRequest();
        try {
            JsonElement element = request.getReturnJson(HOST, PORT, "version", params);
            JsonObject result = element.getAsJsonObject();
            if (result.get("status").getAsBoolean()) {
                return result.get("version").getAsString();
            }
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static JsonObject checkUpdate(String id, String currentVersion) {
        Map<String, String> params = new HashMap<>();
        params.put("id", id);
        params.put("version", currentVersion);

        HttpRequest request = new HttpRequest();
        try {
            JsonElement element = request.getReturnJson(HOST, PORT, "checkUpdate", params);
            JsonObject result = element.getAsJsonObject();
            if (result.get("status").getAsBoolean()) {
                return result;
            }
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean setReleaseVersion(String version) {
        return setVersion("release", version);
    }

    public static boolean setDebugVersion(String version) {
        return setVersion("debug", version);
    }

    private static boolean setVersion(String type, String version) {
        Map<String, String> params = new HashMap<>();
        params.put("type", type);
        params.put("version", version);

        HttpRequest request = new HttpRequest();
        try {
            JsonElement element = request.postReturnJson(HOST, PORT, "version", params, null);
            JsonObject result = element.getAsJsonObject();
            return result.get("status").getAsBoolean();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean heartBeat(String id) {
        Map<String, String> params = new HashMap<>();
        params.put("id", id);

        HttpRequest request = new HttpRequest();
        try {

            JsonElement element = request.getReturnJson(HOST, PORT, "heartbeat", params);
            JsonObject result = element.getAsJsonObject();
            return result.get("status").getAsBoolean();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static JsonObject getSurvey(String id) {
        Map<String, String> params = new HashMap<>();
        params.put("id", id);

        HttpRequest request = new HttpRequest();
        try {
            JsonElement element = request.getReturnJson(HOST, PORT, "survey", params);
            JsonObject result = element.getAsJsonObject();
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static JsonObject submitSurvey(String id, String session, String answer) {
        Map<String, String> params = new HashMap<>();
        params.put("id", id);
        params.put("session", session);
        params.put("answer", answer);

        HttpRequest request = new HttpRequest();
        try {
            JsonElement element = request.postReturnJson(HOST, PORT, "submitSurvey", params, null);
            JsonObject result = element.getAsJsonObject();
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static JsonObject getSurveyCount(String id) {
        Map<String, String> params = new HashMap<>();
        params.put("id", id);

        HttpRequest request = new HttpRequest();
        try {
            JsonElement element = request.getReturnJson(HOST, PORT, "surveyCount", params);
            JsonObject result = element.getAsJsonObject();
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static JsonObject feedback(String id, String feedback) {
        Map<String, String> params = new HashMap<>();
        params.put("id", id);
        params.put("feedback", feedback);

        HttpRequest request = new HttpRequest();
        try {
            JsonElement element = request.getReturnJson(HOST, PORT, "feedback", params);
            JsonObject result = element.getAsJsonObject();
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getQuestionnaireUrl(String group) {
        Map<String, String> params = new HashMap<>();
        params.put("group", group);

        HttpRequest request = new HttpRequest();
        try {
            JsonElement element = request.getReturnJson(HOST, PORT, "questionnaireurl", params);
            JsonObject result = element.getAsJsonObject();
            if (result.get("status").getAsBoolean()) {
                return result.get("url").getAsString();
            }
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean setQuestionnaireUrl(String group, String url) {
        Map<String, String> params = new HashMap<>();
        params.put("group", group);
        params.put("url", url);

        HttpRequest request = new HttpRequest();
        try {
            JsonElement element = request.postReturnJson(HOST, PORT, "questionnaireurl", params, null);
            JsonObject result = element.getAsJsonObject();
            return result.get("status").getAsBoolean();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void testHttpRequest() {
        boolean resultBoolean;
        JsonObject resultJson;

        if (false) {
            resultJson = register("一班", "我", "150001", "13888888888");
            if (resultJson.get("status").getAsBoolean()) {
                Log.d(TAG, "success");
            }
            resultJson = register("二班", "你", "150003", "18666666666");
            if (resultJson.get("status").getAsBoolean()) {
                Log.d(TAG, "success");
            }
        }


        if (false) {
            List<String[]> filesInfo = new ArrayList<>();
            filesInfo.add(new String[]{"150001", "1", "1.2", "150001_1.db"});
            filesInfo.add(new String[]{"150001", "3", "1.0", "150001_3.db"});
            filesInfo.add(new String[]{"150003", "2", "1.2", "150003_2.db"});
            filesInfo.add(new String[]{"150003", "4", "1.2", "150003_4.db"});
            for (String[] fileInfo : filesInfo) {
                resultBoolean = upload(Utils.combinePaths(Environment.getExternalStorageDirectory().getAbsolutePath(), fileInfo[3]), fileInfo[0], Integer.parseInt(fileInfo[1]), fileInfo[2]);
                if (resultBoolean) {
                    Log.d(TAG, "uploaded: " + Arrays.toString(fileInfo));
                }
            }
        }

        if (false) {
            List<String[]> filesInfo = new ArrayList<>();
            filesInfo.add(new String[]{"150001", "1", "1.2", "150001_1.db"});
            filesInfo.add(new String[]{"150001", "3", "1.0", "150001_3.db"});
            filesInfo.add(new String[]{"150003", "2", "1.2", "150003_2.db"});
            filesInfo.add(new String[]{"150003", "4", "1.2", "150003_4.db"});
            for (String[] fileInfo : filesInfo) {
                resultBoolean = download(fileInfo[0], Integer.parseInt(fileInfo[1]), fileInfo[2], Utils.combinePaths(Environment.getExternalStorageDirectory().getAbsolutePath(), fileInfo[3]));
                if (resultBoolean) {
                    Log.d(TAG, "downloaded " + Arrays.toString(fileInfo));
                }
            }
        }

        if (false) {
            JsonElement result = statistic();
            if (result != null) {
                JsonArray students = result.getAsJsonArray();
                for (Iterator<JsonElement> it = students.iterator(); it.hasNext(); ) {
                    JsonObject student = it.next().getAsJsonObject();
                    Log.d(TAG, "name: " + student.get("name").getAsString());
                    Log.d(TAG, "class: " + student.get("class").getAsString());
                    Log.d(TAG, "id: " + student.get("id").getAsString());
                    Log.d(TAG, "phone: " + student.get("phone").getAsString());
                    JsonObject uploads = student.get("uploads").getAsJsonObject();
                    for (Map.Entry<String, JsonElement> entry : uploads.entrySet()) {
                        String version = entry.getKey();
                        int[] counts = new Gson().fromJson(entry.getValue(), int[].class);
                        Log.d(TAG, "version: " + version + " counts: " + Arrays.toString(counts));
                    }
                }
            }
        }
        if (false) {
            String[] ids = {"150001", "150002"};
            for (String id : ids) {
                String className = studentClass(id);
                if (className != null) {
                    Log.d(TAG, "id: " + id + ", class: " + className);
                } else {
                    Log.d(TAG, "id: " + id + ", class not found, you may try again");
                }
            }
        }
        if (false) {
            String[] ids = {"150001", "150002"};
            for (String id : ids) {
                resultBoolean = delete(id);
                if (resultBoolean) {
                    Log.d(TAG, "successfully deleted " + id);
                }
            }
        }
        if (false) {
            String releaseVersion = "2.5.0";
            resultBoolean = setReleaseVersion(releaseVersion);
            if (resultBoolean) {
                Log.d(TAG, "release version has set");
            }
        }
        if (false) {
            String debugVersion = "2.6.1";
            resultBoolean = setDebugVersion(debugVersion);
            if (resultBoolean) {
                Log.d(TAG, "debug version has set");
            }
        }
        if (true) {
            String releaseVersion = getReleaseVersion();
            if (releaseVersion != null) {
                Log.d(TAG, "release version: " + releaseVersion);
            }
        }
        if (true) {
            String debugVersion = getDebugVersion();
            if (debugVersion != null) {
                Log.d(TAG, "debug version: " + debugVersion);
            }
        }
        if (false) {
            String[] ids = {"150001", "150003"};
            for (String id : ids) {
                resultBoolean = heartBeat(id);
                if (resultBoolean) {
                    Log.d(TAG, id + " heart beats");
                }
            }
        }
        if (false) {
            Map<String, String> questionnaireUrls = new HashMap<>();
            questionnaireUrls.put("一组", "http://a.b.com");
            questionnaireUrls.put("二组", "http://c.d.com");
            questionnaireUrls.put("三组", "http://e.f.com");

            for (Map.Entry<String, String> entry : questionnaireUrls.entrySet()) {
                String group = entry.getKey();
                String url = entry.getValue();
                resultBoolean = setQuestionnaireUrl(group, url);
                if (resultBoolean) {
                    Log.d(TAG, "set group " + group + " questionnaire url success");
                }
            }
        }
        if (false) {
            String[] groups = {"一组", "二组", "三组", "四组"};
            for (String group : groups) {
                String url = getQuestionnaireUrl(group);
                if (url != null) {
                    Log.d(TAG, group + " questionnair url: " + url);
                }
            }
        }
    }
}
