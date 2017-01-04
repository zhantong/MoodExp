package cn.edu.nju.dislab.moodexphttputils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;

/**
 * Created by zhantong on 2016/11/29.
 */

public class Utils {
    public static String combinePaths(String... paths) {
        if (paths.length == 0) {
            return "";
        }
        File combined = new File(paths[0]);
        int i = 1;
        while (i < paths.length) {
            combined = new File(combined, paths[i]);
            i++;
        }
        return combined.getPath();
    }

    public static String fileToSHA1(String filePath) {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(filePath); // Create an FileInputStream instance according to the filepath
            byte[] buffer = new byte[1024]; // The buffer to read the file
            MessageDigest digest = MessageDigest.getInstance("SHA-1"); // Get a SHA-1 instance
            int numRead = 0; // Record how many bytes have been read
            while (numRead != -1) {
                numRead = inputStream.read(buffer);
                if (numRead > 0)
                    digest.update(buffer, 0, numRead); // Update the digest
            }
            byte[] sha1Bytes = digest.digest(); // Complete the hash computing
            return convertHashToString(sha1Bytes); // Call the function to convert to hex digits
        } catch (Exception e) {
            return null;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close(); // Close the InputStream
                } catch (Exception e) {
                }
            }
        }
    }

    private static String convertHashToString(byte[] hashBytes) {
        String returnVal = "";
        for (int i = 0; i < hashBytes.length; i++) {
            returnVal += Integer.toString((hashBytes[i] & 0xff) + 0x100, 16).substring(1);
        }
        return returnVal.toLowerCase();
    }
}
