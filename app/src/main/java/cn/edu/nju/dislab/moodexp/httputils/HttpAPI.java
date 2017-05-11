package cn.edu.nju.dislab.moodexp.httputils;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpAPI {
    private static final String TAG = "MoodExp";
    private static final String HOST = "114.212.80.16";
    private static final int PORT = 9000;
    private static OkHttpClient client;
    private static JsonParser jsonParser = new JsonParser();

    private static OkHttpClient getClient() {
        if (client == null) {
            client = new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .writeTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(10, TimeUnit.SECONDS)
                    .build();
        }
        return client;
    }

    public static JsonElement register(String class_name, String name, String id, String phone, Callback callback) throws IOException {
        HttpUrl url = new HttpUrl.Builder()
                .scheme("http")
                .host(HOST)
                .port(PORT)
                .addPathSegments("register")
                .addQueryParameter("class", class_name)
                .addQueryParameter("name", name)
                .addQueryParameter("id", id)
                .addQueryParameter("phone", phone)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .build();
        if (callback != null) {
            getClient().newCall(request).enqueue(callback);
            return null;
        } else {
            Response response = getClient().newCall(request).execute();
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            return jsonParser.parse(response.body().charStream());
        }
    }

    public static JsonElement upload(String filePath, String id, int count, String version, Callback callback) throws IOException {
        HttpUrl url = new HttpUrl.Builder()
                .scheme("http")
                .host(HOST)
                .port(PORT)
                .addPathSegments("upload")
                .build();
        File file = new File(filePath);
        if (!file.exists()) {
            throw new IOException("file " + file.getAbsolutePath() + " not exists");
        }
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("id", id)
                .addFormDataPart("count", Integer.toString(count))
                .addFormDataPart("version", version)
                .addFormDataPart("file", file.getName(), RequestBody.create(MediaType.parse("application/octet-stream"), file))
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        if (callback != null) {
            getClient().newCall(request).enqueue(callback);
            return null;
        } else {
            Response response = getClient().newCall(request).execute();
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            return jsonParser.parse(response.body().charStream());
        }
    }

    public static JsonElement uploadLog(String filePath, String id, String version, Callback callback) throws IOException {
        HttpUrl url = new HttpUrl.Builder()
                .scheme("http")
                .host(HOST)
                .port(PORT)
                .addPathSegments("uploadLog")
                .build();
        File file = new File(filePath);
        if (!file.exists()) {
            throw new IOException("file " + file.getAbsolutePath() + " not exists");
        }
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("id", id)
                .addFormDataPart("version", version)
                .addFormDataPart("file", file.getName(), RequestBody.create(MediaType.parse("application/octet-stream"), file))
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        if (callback != null) {
            getClient().newCall(request).enqueue(callback);
            return null;
        } else {
            Response response = getClient().newCall(request).execute();
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            return jsonParser.parse(response.body().charStream());
        }
    }

    public static JsonElement studentInfo(String id, Callback callback) throws IOException {
        HttpUrl url = new HttpUrl.Builder()
                .scheme("http")
                .host(HOST)
                .port(PORT)
                .addPathSegments("info")
                .addQueryParameter("id", id)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .build();
        if (callback != null) {
            getClient().newCall(request).enqueue(callback);
            return null;
        } else {
            Response response = getClient().newCall(request).execute();
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            return jsonParser.parse(response.body().charStream());
        }
    }

    public static JsonElement checkUpdate(String id, String currentVersion, Callback callback) throws IOException {
        HttpUrl url = new HttpUrl.Builder()
                .scheme("http")
                .host(HOST)
                .port(PORT)
                .addPathSegments("checkUpdate")
                .addQueryParameter("id", id)
                .addQueryParameter("version", currentVersion)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .build();
        if (callback != null) {
            getClient().newCall(request).enqueue(callback);
            return null;
        } else {
            Response response = getClient().newCall(request).execute();
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            return jsonParser.parse(response.body().charStream());
        }
    }

    public static JsonElement heartBeat(String id, Callback callback) throws IOException {
        HttpUrl url = new HttpUrl.Builder()
                .scheme("http")
                .host(HOST)
                .port(PORT)
                .addPathSegments("heartbeat")
                .addQueryParameter("id", id)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .build();
        if (callback != null) {
            getClient().newCall(request).enqueue(callback);
            return null;
        } else {
            Response response = getClient().newCall(request).execute();
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            return jsonParser.parse(response.body().charStream());
        }
    }

    public static JsonElement getSurvey(String id, Callback callback) throws IOException {
        HttpUrl url = new HttpUrl.Builder()
                .scheme("http")
                .host(HOST)
                .port(PORT)
                .addPathSegments("survey")
                .addQueryParameter("id", id)
                .build();
        Request request = new Request.Builder()
                .addHeader("Accept-Language", Locale.getDefault().getLanguage())
                .url(url)
                .build();
        if (callback != null) {
            getClient().newCall(request).enqueue(callback);
            return null;
        } else {
            Response response = getClient().newCall(request).execute();
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            return jsonParser.parse(response.body().charStream());
        }
    }

    public static JsonElement submitSurvey(String id, String session, String answer, Callback callback) throws IOException {
        HttpUrl url = new HttpUrl.Builder()
                .scheme("http")
                .host(HOST)
                .port(PORT)
                .addPathSegments("submitSurvey")
                .build();
        RequestBody requestBody = new FormBody.Builder()
                .add("id", id)
                .add("session", session)
                .add("answer", answer)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        if (callback != null) {
            getClient().newCall(request).enqueue(callback);
            return null;
        } else {
            Response response = getClient().newCall(request).execute();
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            return jsonParser.parse(response.body().charStream());
        }
    }

    public static JsonElement getSurveyCount(String id, Callback callback) throws IOException {
        HttpUrl url = new HttpUrl.Builder()
                .scheme("http")
                .host(HOST)
                .port(PORT)
                .addPathSegments("surveyCount")
                .addQueryParameter("id", id)
                .build();
        Request request = new Request.Builder()
                .addHeader("Accept-Language", Locale.getDefault().getLanguage())
                .url(url)
                .build();
        if (callback != null) {
            getClient().newCall(request).enqueue(callback);
            return null;
        } else {
            Response response = getClient().newCall(request).execute();
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            return jsonParser.parse(response.body().charStream());
        }
    }

    public static JsonElement feedback(String id, String feedback, Callback callback) throws IOException {
        HttpUrl url = new HttpUrl.Builder()
                .scheme("http")
                .host(HOST)
                .port(PORT)
                .addPathSegments("feedback")
                .addQueryParameter("id", id)
                .addQueryParameter("feedback", feedback)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .build();
        if (callback != null) {
            getClient().newCall(request).enqueue(callback);
            return null;
        } else {
            Response response = getClient().newCall(request).execute();
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            return jsonParser.parse(response.body().charStream());
        }
    }
}
