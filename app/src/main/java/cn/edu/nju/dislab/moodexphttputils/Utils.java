package cn.edu.nju.dislab.moodexphttputils;

import java.io.File;

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
}
