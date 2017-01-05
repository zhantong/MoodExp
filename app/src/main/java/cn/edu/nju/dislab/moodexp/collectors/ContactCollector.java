package cn.edu.nju.dislab.moodexp.collectors;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.edu.nju.dislab.moodexp.EasyPermissions;
import cn.edu.nju.dislab.moodexp.MainApplication;

/**
 * Created by zhantong on 2016/12/21.
 */

public class ContactCollector {
    private static final String TAG = "ContactCollector";
    private static final String[] PERMISSIONS = {Manifest.permission.READ_CONTACTS};
    private Context mContext;
    private ContentResolver mContentResolver;
    private ContactData result;

    private static final Logger LOG = LoggerFactory.getLogger(ContactCollector.class);

    public ContactCollector() {
        this(MainApplication.getContext());
    }

    public ContactCollector(Context context) {
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
            cursor = mContentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        } catch (Exception e) {
            LOG.info("error get cursor {}", e);
            return Collector.NO_PERMISSION;
        }
        if (cursor == null) {
            LOG.info("null cursor");
            return Collector.NO_PERMISSION;
        }
        if (cursor.getCount() > 0) {
            result = new ContactData();
            LOG.info("start collecting");
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                result.put(name, number);
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

    public ContactData getResult() {
        return result;
    }
}
