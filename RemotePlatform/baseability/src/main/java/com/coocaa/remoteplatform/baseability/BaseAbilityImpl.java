package com.coocaa.remoteplatform.baseability;

import android.content.Context;
import android.util.Log;

import com.coocaa.remoteplatform.baseability.abilities.AbilityFactory;
import com.coocaa.remoteplatform.baseability.abilities.IAbility;
import com.coocaa.remoteplatform.core.common.RemoteCommand;
import com.coocaa.remoteplatform.sdk.RemotePlatform;

/**
 * @ClassName: BaseAbilityImpl
 * @Author: XuZeXiao
 * @CreateDate: 3/16/21 5:12 PM
 * @Description:
 */
public class BaseAbilityImpl implements IBaseAbility {
    private static final String TAG = "BaseAbilityImpl";

    public static class InstanceClass {
        public static BaseAbilityImpl instance = new BaseAbilityImpl();
    }

    public static BaseAbilityImpl getInstance() {
        return InstanceClass.instance;
    }

    @Override
    public void init(Context context) {
        RemotePlatform.getInstance().init(context);
    }

    @Override
    public void handleMessage(Context context, RemoteCommand command) {
        IAbility ability = AbilityFactory.getAbility(context, command.cmdType);
        if (ability == null) {
            Log.e(TAG, "no ability match command: " + command.toString());
            return;
        }
        ability.handleMessage(command);
    }
}
