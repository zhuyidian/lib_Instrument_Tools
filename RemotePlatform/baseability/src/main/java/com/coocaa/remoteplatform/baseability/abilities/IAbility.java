package com.coocaa.remoteplatform.baseability.abilities;

import android.content.Context;

import com.coocaa.remoteplatform.core.common.RemoteCommand;

/**
 * @ClassName: IAbility
 * @Author: XuZeXiao
 * @CreateDate: 3/31/21 8:27 PM
 * @Description:
 */
public interface IAbility {
    void init(Context context);

    void handleMessage(RemoteCommand command);

    void realMessage(RemoteCommand command);

    String getName();
}
