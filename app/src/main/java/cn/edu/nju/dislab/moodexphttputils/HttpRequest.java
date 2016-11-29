package cn.edu.nju.dislab.moodexphttputils;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by zhantong on 2016/11/29.
 */

public class HttpRequest {
    OkHttpClient client;

    public HttpRequest() {
        client = new OkHttpClient();
    }

    public ResponseBody get(String host, int port, String segments, Map<String, String> params) throws IOException {
        HttpUrl url = parseUrl(host, port, segments, params);
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            throw new IOException("Server returned non-OK status: " + response.toString());
        }
        return response.body();
    }

    public String getReturnString(String host, int port, String segments, Map<String, String> params) throws IOException {
        return get(host, port, segments, params).string();
    }

    public JsonElement getReturnJson(String host, int port, String segments, Map<String, String> params) throws IOException {
        JsonParser parser = new JsonParser();
        return parser.parse(getReturnString(host, port, segments, params));
    }

    public byte[] getReturnBytes(String host, int port, String segments, Map<String, String> params) throws IOException {
        return get(host, port, segments, params).bytes();
    }

    public void download(String host, int port, String segments, Map<String, String> params, String filePath) throws IOException {
        byte[] fileInBytes = getReturnBytes(host, port, segments, params);
        OutputStream os = new FileOutputStream(filePath);
        os.write(fileInBytes);
        os.close();
    }

    public void download(String host, int port, String segments, String filePath) throws IOException {
        download(host, port, segments, null, filePath);
    }

    public ResponseBody post(String host, int port, String segments, Map<String, String> params, String filePath) throws IOException {
        HttpUrl url = parseUrl(host, port, segments, null);
        MultipartBody.Builder builder = new MultipartBody.Builder();
        if (params != null) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                builder.addFormDataPart(entry.getKey(), entry.getValue());
            }
        }
        if (filePath != null) {
            String contentType = "application/octet-stream; charset=utf-8";
            File file = new File(filePath);
            RequestBody requestBody = RequestBody.create(MediaType.parse(contentType), file);
            builder.addFormDataPart("file", file.getName(), requestBody);
        }
        MultipartBody multipartBody = builder.build();
        Request request = new Request.Builder()
                .url(url)
                .post(multipartBody)
                .build();
        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            throw new IOException("Server returned non-OK status: " + response.toString());
        }
        return response.body();
    }

    public String postReturnString(String host, int port, String segments, Map<String, String> params, String filePath) throws IOException {
        return post(host, port, segments, params, filePath).string();
    }

    public JsonElement postReturnJson(String host, int port, String segments, Map<String, String> params, String filePath) throws IOException {
        JsonParser parser = new JsonParser();
        return parser.parse(postReturnString(host, port, segments, params, filePath));
    }

    public static HttpUrl parseUrl(String host, int port, String segments, Map<String, String> params) {
        HttpUrl.Builder builder = new HttpUrl.Builder().scheme("http").host(host);
        if (port != -1) {
            builder.port(port);
        }
        if (segments != null) {
            builder.addPathSegments(segments);
        }
        if (params != null) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                builder.addQueryParameter(entry.getKey(), entry.getValue());
            }
        }
        return builder.build();
    }
}
