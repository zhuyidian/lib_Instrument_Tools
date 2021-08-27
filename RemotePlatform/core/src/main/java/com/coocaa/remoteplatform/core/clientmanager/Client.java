package com.coocaa.remoteplatform.core.clientmanager;

import android.content.ComponentName;

import java.io.Serializable;

/**
 * @ClassName: Client
 * @Author: XuZeXiao
 * @CreateDate: 3/9/21 3:21 PM
 * @Description:
 */
public class Client implements Serializable {
    public String id;
    public ComponentName componentName;
    public int version;

    public Client(String id, String packageName, String className, int version) {
        this.id = id;
        this.componentName = new ComponentName(packageName, className);
        this.version = version;
    }

    @Override
    public String toString() {
        return "Client{" + "id='" + id + '\'' + ", componentName=" + componentName + ", version=" + version + '}';
    }
}
