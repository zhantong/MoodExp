package cn.edu.nju.dislab.moodexp.collectors;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.CallLog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.edu.nju.dislab.moodexp.EasyPermissions;
import cn.edu.nju.dislab.moodexp.MainApplication;

/**
 * Created by zhantong on 2016/12/21.
 */

public class CallLogCollector {
    private static final String TAG = "ContactCollector";
    private static final String[] PERMISSIONS = {Manifest.permission.READ_CALL_LOG};
    private Context mContext;
    private ContentResolver mContentResolver;
    private CallLogData result;

    private static final Logger LOG = LoggerFactory.getLogger(CallLogCollector.class);

    public CallLogCollector() {
        this(MainApplication.getContext());
    }

    public CallLogCollector(Context context) {
        mContext = context;
        mContentResolver = mContext.getContentResolver();
    }

    public static String[] getPermissions() {
        return PERMISSIONS;
    }

    public int collect() {
        if (!EasyPermissions.hasPermissions(PERMISSIONS)) {
            return Collector.NO_PERMISSION;
        }
        LOG.info("preparing to collect");
        Cursor cursor;
        try {
            cursor = mContentResolver.query(CallLog.Calls.CONTENT_URI, null, null, null, null);
        } catch (Exception e) {
            LOG.info("error get cursor {}", e);
            return Collector.NO_PERMISSION;
        }
        if (cursor == null) {
            LOG.info("null cursor");
            return Collector.NO_PERMISSION;
        }
        if (cursor.getCount() > 0) {
            result = new CallLogData();
            LOG.info("start collecting");
            while (cursor.moveToNext()) {
                String number = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));
                String type = cursor.getString(cursor.getColumnIndex(CallLog.Calls.TYPE));
                String date = cursor.getString(cursor.getColumnIndex(CallLog.Calls.DATE));
                String duration = cursor.getString(cursor.getColumnIndex(CallLog.Calls.DURATION));
                result.put(number, type, date, duration);
            }
        } else {
            cursor.close();
            LOG.info("empty cursor");
            return Collector.COLLECT_FAILED;
        }
        cursor.close();
        LOG.info("finished collect");
        return Collector.COLLECT_SUCCESS;
    }

    public CallLogData getResult() {
        return result;
    }
}
