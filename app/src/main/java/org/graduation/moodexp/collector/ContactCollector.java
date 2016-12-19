package org.graduation.moodexp.collector;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;

import org.graduation.moodexp.database.DatabaseManager;
import org.graduation.moodexp.healthylife.MainApplication;

/**
 * Created by javan on 2016/6/9.
 */
public class ContactCollector
{
    private static final String[] PHONES_PROJECTION = new String[]{
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER };
    private static final String[] CONTACT_RECORD_PROJECTION = { CallLog.Calls.DATE, // 日期
            CallLog.Calls.NUMBER, // 号码
            CallLog.Calls.TYPE, // 类型
            CallLog.Calls.DURATION
    };
    private static final String[] SMS_PROJECTION=new String[]{
            Telephony.Sms.ADDRESS,
            Telephony.Sms.DATE,
            Telephony.Sms.TYPE
    };
    /**联系人显示名称**/
    private static final int PHONES_DISPLAY_NAME_INDEX = 0;
    /**电话号码**/
    private static final int PHONES_NUMBER_INDEX = 1;

    private static final int CALLS_DATE_INDEX=0;
    private static final int CALLS_NUMBER_INDEX=1;
    private static final int CALLS_TYPE_INDEX=2;
    private static final int CALLS_DURATION_INDEX=3;

    private static final int SMS_ADDRESS_INDEX=0;
    private static final int SMS_DATE_INDEX=1;
    private static final int SMS_TYPE_INDEX=2;

    public void collect()
    {//Log.e("contact","0");
        ContentResolver resolver = MainApplication.getContext().getContentResolver();
        // 获取手机联系人
        DatabaseManager databaseManager=DatabaseManager.getDatabaseManager();
        SQLiteDatabase db=databaseManager.getDatabase();
        db.beginTransaction();
        db.execSQL("delete from contacts");
        db.execSQL("delete from calls");
        db.execSQL("delete from sms");


        if (ActivityCompat.checkSelfPermission(MainApplication.getContext(), Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED)
        {
            Cursor phoneCursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,PHONES_PROJECTION, null, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID);
            if (phoneCursor != null)
            {
                while (phoneCursor.moveToNext()) {
                    //得到手机号码
                    String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
                    //当手机号码为空的或者为空字段 跳过当前循环
                    if (TextUtils.isEmpty(phoneNumber))
                        continue;
                    long phoneNumHash=phoneNumber.hashCode();;
                    //if(phoneNumber==null) phoneNumHash=phoneNumber.hashCode();
                    //else phoneNumHash=0;


                    //得到联系人名称
                    String contactName = phoneCursor.getString(PHONES_DISPLAY_NAME_INDEX);
                    long nameHash;
                    if(contactName==null)
                    {
                        nameHash=0;
                    }
                    else nameHash=contactName.hashCode();


                    databaseManager.saveContacts(nameHash,phoneNumHash);
                }
                phoneCursor.close();
            }
        }

        if (ActivityCompat.checkSelfPermission(MainApplication.getContext(), Manifest.permission.READ_CALL_LOG)
                == PackageManager.PERMISSION_GRANTED)
        {
            //获取通话记录
            if ( Build.VERSION.SDK_INT >= 23) MainApplication.getContext().checkSelfPermission(android.Manifest.permission.READ_CALL_LOG);
            Cursor contactRecordCursor = resolver.query(CallLog.Calls.CONTENT_URI, CONTACT_RECORD_PROJECTION, null, null, CallLog.Calls.DATE + " DESC");

            //Log.e("contact","2");

            while (contactRecordCursor.moveToNext()) {


                String s = contactRecordCursor.getString(CALLS_NUMBER_INDEX);
                long phNumberHash;

                if(s==null) phNumberHash=0;
                else phNumberHash=s.hashCode();

                String callType = contactRecordCursor.getString(CALLS_TYPE_INDEX);
                String callDate = contactRecordCursor.getString(CALLS_DATE_INDEX);
                long callTime=Long.valueOf(callDate);
                String callDuration = contactRecordCursor.getString(CALLS_DURATION_INDEX);
                int callDurationTime=Integer.valueOf(callDuration);
                int dircode = Integer.parseInt(callType);
                //1 is incomming,2 is outgoing,3 is missed
                databaseManager.saveCalls(callTime,phNumberHash,dircode,callDurationTime);
            }
            contactRecordCursor.close();
        }

        if (ActivityCompat.checkSelfPermission(MainApplication.getContext(), Manifest.permission.READ_SMS)
                == PackageManager.PERMISSION_GRANTED)
        {
            Uri smsUri = Uri.parse("content://sms/");
            String SORT_ORDER = "date DESC";
            Cursor smsCursor=resolver.query(smsUri,SMS_PROJECTION,null,null,SORT_ORDER);
            while(smsCursor.moveToNext())
            {
                long date=smsCursor.getLong(SMS_DATE_INDEX);

                String s=smsCursor.getString(SMS_ADDRESS_INDEX);
                long address;
                if(s==null) address=0;
                else address=s.hashCode();//.hashCode();

                int type=smsCursor.getInt(SMS_TYPE_INDEX);
                if(type!=1&&type!=2) continue;
                databaseManager.saveSms(date,address,type);
            }
        }


        db.setTransactionSuccessful();
        db.endTransaction();
    }
}
