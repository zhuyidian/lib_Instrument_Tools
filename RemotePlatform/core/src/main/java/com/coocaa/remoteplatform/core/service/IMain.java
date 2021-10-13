package com.coocaa.remoteplatform.core.service;

import com.coocaa.remoteplatform.core.clientmanager.IClientManager;
import com.coocaa.remoteplatform.core.connection.IConnectionManager;
import com.coocaa.remoteplatform.core.connection.push.PushChannel;
import com.coocaa.remoteplatform.core.connection.socket.SocketChannel;
import com.coocaa.remoteplatform.core.dispatcher.IMessageDispatcher;
import com.coocaa.remoteplatform.core.reply.IMessageReply;

/**
 * @ClassName: IMain
 * @Author: XuZeXiao
 * @CreateDate: 3/11/21 8:24 PM
 * @Description:
 */
public interface IMain {
    void init();

    void requestTimeData();

    IClientManager getClientManager();

    IConnectionManager getConnectionManager();

    IMessageDispatcher getMessageDispatcher();

    IMessageReply getMessageReply();

    PushChannel getPushChannel();

    SocketChannel getSocketChannel();

    AttachInfo getAttachInfo();

    void destroy();
}
