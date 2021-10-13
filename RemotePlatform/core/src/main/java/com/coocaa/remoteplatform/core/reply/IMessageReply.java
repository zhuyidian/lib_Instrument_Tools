package com.coocaa.remoteplatform.core.reply;

import com.coocaa.remoteplatform.core.common.RemoteCommand;

/**
 * @ClassName: IMessageReply
 * @Author: XuZeXiao
 * @CreateDate: 3/11/21 8:09 PM
 * @Description:
 */
public interface IMessageReply {
    void replyMessage(RemoteCommand command);
}
