package com.coocaa.remoteplatform.core.reply;

import com.coocaa.remoteplatform.core.connection.IConnectionManager;
import com.coocaa.remoteplatform.core.common.RemoteCommand;

/**
 * @ClassName: MessageReply
 * @Author: XuZeXiao
 * @CreateDate: 3/11/21 8:09 PM
 * @Description:
 */
public class MessageReply implements IMessageReply {
    private IConnectionManager mConnectionManager;

    public MessageReply(IConnectionManager mConnectionManager) {
        this.mConnectionManager = mConnectionManager;
    }

    @Override
    public void replyMessage(RemoteCommand command) {
        mConnectionManager.replyMessage(command);
    }
}
