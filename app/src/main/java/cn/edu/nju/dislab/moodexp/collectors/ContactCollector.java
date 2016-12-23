package cn.edu.nju.dislab.moodexp.collectors;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;

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

    public ContactCollector() {
        this(MainApplication.getContext());
    }

    public ContactCollector(Context context) {
        mContext = context;
        mContentResolver = mContext.getContentResolver();
    }

    public int collect() {
        if (!EasyPermissions.hasPermissions(PERMISSIONS)) {
            return Collector.NO_PERMISSION;
        }
        Cursor cursor = mContentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        if (cursor == null) {
            Log.i(TAG, "null cursor");
            return Collector.NO_PERMISSION;
        }
        if (cursor.getCount() > 0) {
            result = new ContactData();
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                result.put(name, number);
            }
        } else {
            cursor.close();
            Log.i(TAG, "empty cursor");
            return Collector.COLLECT_FAILED;
        }
        cursor.close();
        return Collector.COLLECT_SUCCESS;
    }

    public ContactData getResult() {
        return result;
    }

    public static String[] getPermissions() {
        return PERMISSIONS;
    }
}
