package com.coocaa.remoteplatform.core.common;

import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * @ClassName: RemoteCommand
 * @Author: XuZeXiao
 * @CreateDate: 3/23/21 5:01 PM
 * @Description:
 */
public class RemoteCommand implements Parcelable, Cloneable {
    public String userId;
    public String activeId;
    public String msgId;
    public String clientId;
    public long timestamp;
    public int cmdType;
    public String content;
    public String status;
    public String msg;
    public String msgOrigin;
    public String deviceId;

    public RemoteCommand() {
    }

    public RemoteCommand(Parcel in) {
        userId = in.readString();
        activeId = in.readString();
        msgId = in.readString();
        clientId = in.readString();
        timestamp = in.readLong();
        cmdType = in.readInt();
        content = in.readString();
        status = in.readString();
        msg = in.readString();
        msgOrigin = in.readString();
        deviceId = in.readString();
    }

    public static class ReplyBuilder {
        private RemoteCommand replyCommand;
        private Context context;

        public ReplyBuilder(Context context, RemoteCommand replyCommand, String code) {
            this.context = context;
            this.replyCommand = replyCommand.copyInstance();
            this.replyCommand.status = code;
        }

        public ReplyBuilder withStatus(String code) {
            replyCommand.status = code;
            return this;
        }

        public ReplyBuilder withContent(String content) {
            replyCommand.content = content;
            return this;
        }

        public ReplyBuilder withMessage(String msg) {
            replyCommand.msg = msg;
            return this;
        }

        public ReplyBuilder withDeviceId(String id) {
            replyCommand.deviceId = id;
            return this;
        }

        public void reply() {
            Intent intent = new Intent();
            intent.putExtra(Constant.INTENT_COMMAND_KEY, replyCommand);
            intent.setAction(Constant.REPLY_SERVICE_ACTION);
            intent.setPackage(Constant.HOST_PACKAGE_NAME);
            Utils.startService(context, intent);
        }

        public RemoteCommand getCommand() {
            return replyCommand;
        }
    }

    private RemoteCommand copyInstance() {
        RemoteCommand replyCommand = null;
        try {
            replyCommand = (RemoteCommand) clone();
            replyCommand.timestamp = System.currentTimeMillis();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return replyCommand;
    }

    public ReplyBuilder replyReceived(Context context) {
        return new ReplyBuilder(context, this, Constant.COMMAND_STATUS_RECEIVE);
    }

    public ReplyBuilder replyProcessing(Context context) {
        return new ReplyBuilder(context, this, Constant.COMMAND_STATUS_PROCESSING);
    }

    public ReplyBuilder replyFinish(Context context) {
        return new ReplyBuilder(context, this, Constant.COMMAND_STATUS_FINISH);
    }

    public ReplyBuilder replyError(Context context) {
        return new ReplyBuilder(context, this, Constant.COMMAND_STATUS_EXCEPTION);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userId);
        dest.writeString(activeId);
        dest.writeString(msgId);
        dest.writeString(clientId);
        dest.writeLong(timestamp);
        dest.writeInt(cmdType);
        dest.writeString(content);
        dest.writeString(status);
        dest.writeString(msg);
        dest.writeString(msgOrigin);
        dest.writeString(deviceId);
    }

    public static final Creator<RemoteCommand> CREATOR = new Creator<RemoteCommand>() {
        @Override
        public RemoteCommand createFromParcel(Parcel source) {
            return new RemoteCommand(source);
        }

        @Override
        public RemoteCommand[] newArray(int size) {
            return new RemoteCommand[size];
        }
    };

    @Override
    public String toString() {
        return "RemoteCommand{" + "userId='" + userId + '\'' + ", activeId='" + activeId + '\'' + ", msgId='" + msgId + '\'' + ", clientId='" + clientId + '\'' + ", timestamp=" + timestamp + ", cmdType=" + cmdType + ", content='" + content + '\'' + ", status='" + status + '\'' + ", msg='" + msg + '\'' + ", msgOrigin='" + msgOrigin + '\'' + ", deviceId='" + deviceId + '\'' + '}';
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
