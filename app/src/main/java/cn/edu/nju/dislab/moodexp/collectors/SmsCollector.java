package cn.edu.nju.dislab.moodexp.collectors;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.Telephony;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.edu.nju.dislab.moodexp.EasyPermissions;
import cn.edu.nju.dislab.moodexp.MainApplication;

/**
 * Created by zhantong on 2016/12/21.
 */

public class SmsCollector {
    private static final String TAG = "SmsCollector";
    private static final String[] PERMISSIONS = {Manifest.permission.READ_SMS};
    private Context mContext;
    private ContentResolver mContentResolver;
    private SmsData result;

    private static final Logger LOG = LoggerFactory.getLogger(SmsCollector.class);

    public SmsCollector() {
        this(MainApplication.getContext());
    }

    public SmsCollector(Context context) {
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
            cursor = mContentResolver.query(Telephony.Sms.CONTENT_URI, null, null, null, null);
        } catch (Exception e) {
            LOG.info("error get cursor {}", e);
            return Collector.NO_PERMISSION;
        }
        if (cursor == null) {
            LOG.info("null cursor");
            return Collector.NO_PERMISSION;
        }
        if (cursor.getCount() > 0) {
            LOG.info("start collecting");
            result = new SmsData();
            while (cursor.moveToNext()) {
                String address = cursor.getString(cursor.getColumnIndex(Telephony.Sms.ADDRESS));
                String type = cursor.getString(cursor.getColumnIndex(Telephony.Sms.TYPE));
                String date = cursor.getString(cursor.getColumnIndex(Telephony.Sms.DATE));
                String person = cursor.getString(cursor.getColumnIndex(Telephony.Sms.PERSON));
                result.put(address, type, date, person);
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

    public SmsData getResult() {
        return result;
    }
}
