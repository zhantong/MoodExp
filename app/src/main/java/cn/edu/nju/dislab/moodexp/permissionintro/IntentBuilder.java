package cn.edu.nju.dislab.moodexp.permissionintro;

import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;

import cn.edu.nju.dislab.moodexp.MainApplication;

/**
 * Created by zhantong on 2017/1/4.
 */

public class IntentBuilder {
    private String mName;
    private String mTitle;
    private String mMessage;
    private String mAction;
    private String mPackage;
    private String mClass;
    private String mCategory;
    private String mExtraKey;
    private String mExtraValue;
    private Uri mData;

    private Intent mIntent;

    public IntentBuilder() {
    }

    public IntentBuilder setName(String name) {
        mName = name;
        return this;
    }

    public IntentBuilder setTitle(String title) {
        mTitle = title;
        return this;
    }

    public IntentBuilder setMessage(String message) {
        mMessage = message;
        return this;
    }

    public IntentBuilder setAction(String action) {
        mAction = action;
        return this;
    }

    public IntentBuilder setPackage(String mPackage) {
        this.mPackage = mPackage;
        return this;
    }

    public IntentBuilder setClass(String mClass) {
        this.mClass = mClass;
        return this;
    }

    public IntentBuilder setCategory(String category) {
        mCategory = category;
        return this;
    }

    public IntentBuilder setExtraKey(String extraKey) {
        mExtraKey = extraKey;
        return this;
    }

    public IntentBuilder setExtraValue(String extraValue) {
        mExtraValue = extraValue;
        return this;
    }

    public IntentBuilder setData(Uri data) {
        mData = data;
        return this;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getMessage() {
        return mMessage;
    }

    public Intent build() {
        if (mIntent != null) {
            return mIntent;
        }
        mIntent = new Intent();
        if (mAction != null) {
            mIntent.setAction(mAction);
        }
        if (mData != null) {
            mIntent.setData(mData);
        }
        if (mPackage != null && mClass != null) {
            mIntent.setComponent(new ComponentName(mPackage, mClass));
        } else if (mPackage != null) {
            mIntent = MainApplication.getContext().getPackageManager().getLaunchIntentForPackage(mPackage);
        }
        if (mCategory != null) {
            mIntent.addCategory(mCategory);
        }
        if (mExtraKey != null && mExtraValue != null) {
            mIntent.putExtra(mExtraKey, mExtraValue);
        }
        return mIntent;
    }
}
