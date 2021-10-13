package com.coocaa.remoteplatform.core.connection.socket;

import java.io.Serializable;

/**
 * @ClassName: SocketContnet
 * @Author: XuZeXiao
 * @CreateDate: 3/29/21 3:47 PM
 * @Description:
 */
public class SocketContent implements Serializable {
    public String topicName;
    public int width;
    public int height;
    public String resolution;
    public int frame;
}
