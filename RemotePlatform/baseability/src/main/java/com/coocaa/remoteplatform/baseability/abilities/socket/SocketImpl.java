package com.coocaa.remoteplatform.baseability.abilities.socket;

import android.content.Context;

import com.coocaa.remoteplatform.baseability.abilities.AbsAbility;
import com.coocaa.remoteplatform.baseability.abilities.screensync.ScreenSyncImpl;
import com.coocaa.remoteplatform.baseability.abilities.terminal.TerminalImpl;
import com.coocaa.remoteplatform.core.common.RemoteCommand;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName: SocketImpl
 * @Author: XuZeXiao
 * @CreateDate: 4/10/21 2:53 PM
 * @Description:
 */
public class SocketImpl extends AbsAbility {
    private static final String TERM = "term";
    private static final String MEDIA = "media";
    public static final Map<String, Class<? extends AbsAbility>> HANDLERS = new HashMap<>();
    public static final Map<String, AbsAbility> INSTANCE = new HashMap<>();

    static {
        HANDLERS.put(TERM, TerminalImpl.class);
        HANDLERS.put(MEDIA, ScreenSyncImpl.class);
    }

    @Override
    public void handleMessage(RemoteCommand command) {
        SocketData content = new Gson().fromJson(command.content, SocketData.class);
        if (content == null) {
            return;
        }
        AbsAbility absAbility = getInstance(mContext, content.topicName);
        if (absAbility != null) {
            absAbility.handleMessage(command);
        }
    }

    private AbsAbility getInstance(Context context, String type) {
        AbsAbility absAbility = INSTANCE.get(type);
        if (absAbility != null) {
            return absAbility;
        }

        Class<? extends AbsAbility> clazz = HANDLERS.get(type);
        if (clazz == null) {
            return null;
        }
        AbsAbility instance = null;
        try {
            instance = clazz.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (instance != null) {
            instance.init(context);
        }
        INSTANCE.put(type, instance);
        return instance;
    }

    @Override
    public String getName() {
        return null;
    }
}
