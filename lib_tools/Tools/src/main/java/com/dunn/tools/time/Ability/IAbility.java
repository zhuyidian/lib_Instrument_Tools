package com.dunn.tools.time.Ability;

import android.content.Context;

import com.dunn.tools.time.bean.RemoteCommand;

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
