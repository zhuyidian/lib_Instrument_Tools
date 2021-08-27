package com.coocaa.remoteplatform.core.clientmanager;

/**
 * @ClassName: IClientManager
 * @Author: XuZeXiao
 * @CreateDate: 3/9/21 3:22 PM
 * @Description:
 */
public interface IClientManager {
    void findClients();

    Client getClient(String id);

    void destroy();
}
