package com.coocaa.remoteplatform.core.connection.push;

import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;

import static com.coocaa.remoteplatform.core.connection.push.PushConstant.MSG_ID_KEY;
import static com.coocaa.remoteplatform.core.connection.push.PushConstant.MSG_RESULT_KEY;
import static com.coocaa.remoteplatform.core.connection.push.PushConstant.REGID_RESULT_KEY;

/**
 * @ClassName: PushBean
 * @Author: XuZeXiao
 * @CreateDate: 3/18/21 4:18 PM
 * @Description:
 */
public class PushCommand implements Parcelable {
    public String msg;
    public String msgId;
    public String pushId;


    public PushCommand() {
    }

    public PushCommand(Parcel source) {
        this.msg = source.readString();
        this.msgId = source.readString();
        this.pushId = source.readString();
    }

    public PushCommand(Intent intent) {
        msg = intent.getStringExtra(MSG_RESULT_KEY);
        msgId = intent.getStringExtra(MSG_ID_KEY);
        pushId = intent.getStringExtra(REGID_RESULT_KEY);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(msg);
        dest.writeString(msgId);
        dest.writeString(pushId);
    }

    public static final Creator<PushCommand> CREATOR = new Creator<PushCommand>() {
        @Override
        public PushCommand createFromParcel(Parcel source) {
            return new PushCommand(source);
        }

        @Override
        public PushCommand[] newArray(int size) {
            return new PushCommand[size];
        }
    };

    @Override
    public String toString() {
        return "PushCommand{" + "msg='" + msg + '\'' + ", msgId='" + msgId + '\'' + ", pushId='" + pushId + '\'' + '}';
    }
}
