package com.coocaa.remoteplatform.core.service;

import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;

import com.coocaa.remoteplatform.core.common.Constant;
import com.coocaa.remoteplatform.core.common.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import static com.coocaa.remoteplatform.core.common.Constant.ATTACH_INFO_CHANGE_ACTION;
import static com.coocaa.remoteplatform.core.common.Constant.ATTACH_INFO_CHANGE_NEW_VALUE;
import static com.coocaa.remoteplatform.core.common.Constant.ATTACH_INFO_CHANGE_WHICH;

/**
 * @ClassName: AttachInfo
 * @Author: XuZeXiao
 * @CreateDate: 3/22/21 8:49 PM
 * @Description:
 */
public class AttachInfo implements Parcelable {
    private static final String TAG = "AttachInfo";
    private static final String SP_KEY = "attach_info";
    private String deviceId;
    private String activeId;
    private String mac;
    private Context mContext = null;

    public AttachInfo(Context c) {
        mContext = c;
        Log.i(TAG, "AttachInfo: " + toString());
    }

    public AttachInfo(Parcel source) {
        deviceId = source.readString();
        activeId = source.readString();
        mac = source.readString();
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        Log.i(TAG, "setDeviceId: " + deviceId);
        checkChanged("deviceId", this.deviceId, deviceId);
        this.deviceId = deviceId;
        saveSp("deviceId", deviceId);
    }

    public String getActiveId() {
        return activeId;
    }

    public void setActiveId(String activeId) {
        Log.i(TAG, "setActiveId: " + activeId);
        checkChanged("activeId", this.activeId, activeId);
        this.activeId = activeId;
        saveSp("activeId", activeId);
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        checkChanged("mac", this.mac, mac);
        this.mac = mac;
    }

    private void checkChanged(String which, String oldValue, String newValue) {
        if (!Utils.equals(oldValue, newValue)) {
            Log.i(TAG, "attach value change: " + which + " old: " + " new: " + newValue);
            Intent intent = new Intent(ATTACH_INFO_CHANGE_ACTION);
            intent.putExtra(ATTACH_INFO_CHANGE_WHICH, which);
            intent.putExtra(ATTACH_INFO_CHANGE_NEW_VALUE, newValue);
            mContext.sendBroadcast(intent);
        }
    }

    private synchronized void saveSp(String key, String value) {
        String json = Utils.readFromSp(mContext, Constant.SP_FILE_NAME, SP_KEY);
        JSONObject jsonObject = null;
        try {
            jsonObject = TextUtils.isEmpty(json) ? new JSONObject() : new JSONObject(json);
            jsonObject.put(key, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (jsonObject != null) {
            String write = jsonObject.toString();
            Log.i(TAG, "saveSp: write: " + write);
            Utils.saveToSp(mContext, Constant.SP_FILE_NAME, SP_KEY, write);
        }
    }

    public void recoverFromSp() {
        Log.i(TAG, "recoverFromSp: ");
        String json = Utils.readFromSp(mContext, Constant.SP_FILE_NAME, SP_KEY);
        if (TextUtils.isEmpty(json)) {
            Log.i(TAG, "recoverFromSp: json empty");
            return;
        }
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(json);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (jsonObject == null) {
            Log.i(TAG, "recoverFromSp: parse json to object error");
            return;
        }
        try {
            this.activeId = jsonObject.getString("activeId");
            Log.i(TAG, "recoverFromSp: activeId: " + activeId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            this.deviceId = jsonObject.getString("deviceId");
            Log.i(TAG, "recoverFromSp: deviceId: " + deviceId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(deviceId);
        dest.writeString(activeId);
        dest.writeString(mac);
    }

    public static final Creator<AttachInfo> CREATOR = new Creator<AttachInfo>() {
        @Override
        public AttachInfo createFromParcel(Parcel source) {
            return new AttachInfo(source);
        }

        @Override
        public AttachInfo[] newArray(int size) {
            return new AttachInfo[size];
        }
    };

    @Override
    public String toString() {
        return "AttachInfo{" + "deviceId='" + deviceId + '\'' + ", activeId='" + activeId + '\'' + ", mac='" + mac + '\'' + '}';
    }
}
