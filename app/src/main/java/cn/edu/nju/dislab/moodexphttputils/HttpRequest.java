package cn.edu.nju.dislab.moodexphttputils;

import android.net.Uri;

import com.google.common.io.ByteStreams;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

/**
 * Created by zhantong on 2016/11/29.
 */

public class HttpRequest {
    public JsonElement get(String url, Map<String, String> params) throws IOException {
        URL parsedUrl;
        parsedUrl = new URL(parseUrl(url, params));
        HttpURLConnection urlConnection = (HttpURLConnection) parsedUrl.openConnection();
        int status = urlConnection.getResponseCode();
        if (status == HttpURLConnection.HTTP_OK) {
            InputStream inputStream = urlConnection.getInputStream();
            String inputString=new String(ByteStreams.toByteArray(inputStream));
            JsonParser parser=new JsonParser();
            JsonElement element=parser.parse(inputString);
            urlConnection.disconnect();
            return element;
        } else {
            urlConnection.disconnect();
            throw new IOException("Server returned non-OK status: " + status);
        }
    }

    public void download(String url, String filePath) throws IOException {
        File file = new File(filePath);
        URL parsedUrl;
        parsedUrl = new URL(parseUrl(url, null));
        HttpURLConnection urlConnection = (HttpURLConnection) parsedUrl.openConnection();
        int status = urlConnection.getResponseCode();
        if (status == HttpURLConnection.HTTP_OK) {
            InputStream inputStream = urlConnection.getInputStream();
            byte[] fileInByteArray = ByteStreams.toByteArray(inputStream);
            OutputStream os = new FileOutputStream(file);
            os.write(fileInByteArray);
            os.close();
            urlConnection.disconnect();
        } else {
            urlConnection.disconnect();
            throw new IOException("Server returned non-OK status: " + status);
        }
    }

    public JsonElement upload(String url, String filePath, Map<String, String> params) throws IOException {
        URL parsedUrl = new URL(parseUrl(url, null));
        MultipartUtility multipartUtility = new MultipartUtility(parsedUrl.toString(), "UTF-8");
        if (params != null) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                multipartUtility.addFormField(entry.getKey(), entry.getValue());
            }
        }
        multipartUtility.addFilePart("file", new File(filePath));
        return multipartUtility.finish();
    }

    public static String parseUrl(String url, Map<String, String> params) {
        if (params != null) {
            Uri.Builder builder = Uri.parse(url).buildUpon();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                builder.appendQueryParameter(entry.getKey(), entry.getValue());
            }
            return builder.build().toString();
        }
        return Uri.parse(url).toString();
    }
}
