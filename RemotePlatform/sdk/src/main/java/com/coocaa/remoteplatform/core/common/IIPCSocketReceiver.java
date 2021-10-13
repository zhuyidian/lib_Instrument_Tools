package com.coocaa.remoteplatform.core.common;

import android.net.LocalSocket;

/**
 * @ClassName: ITopicCallback
 * @Author: XuZeXiao
 * @CreateDate: 4/14/21 4:46 PM
 * @Description:
 */
public interface IIPCSocketReceiver {
    void onReceive(String msg, LocalSocket client);
}
