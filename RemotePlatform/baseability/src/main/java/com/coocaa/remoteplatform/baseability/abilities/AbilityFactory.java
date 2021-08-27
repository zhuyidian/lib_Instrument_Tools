package com.coocaa.remoteplatform.baseability.abilities;

import android.content.Context;
import android.util.Log;

import com.coocaa.remoteplatform.baseability.abilities.deviceinfo.DeviceInfoImpl;
import com.coocaa.remoteplatform.baseability.abilities.file.FileImpl;
import com.coocaa.remoteplatform.baseability.abilities.logcat.LogcatImpl;
import com.coocaa.remoteplatform.baseability.abilities.reboot.RebootImpl;
import com.coocaa.remoteplatform.baseability.abilities.rtc.AlarmImpl;
import com.coocaa.remoteplatform.baseability.abilities.screenshot.ScreenShotImpl;
import com.coocaa.remoteplatform.baseability.abilities.socket.SocketImpl;
import com.coocaa.remoteplatform.baseability.abilities.volume.VolumeImpl;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName: AbilityFactory
 * @Author: XuZeXiao
 * @CreateDate: 3/31/21 8:56 PM
 * @Description:
 */
public class AbilityFactory {
    private static final String TAG = "AbilityFactory";
    private static final Map<Integer, Class<? extends IAbility>> COMMAND_HANDLERS = new HashMap<>();
    private static final Map<Integer, IAbility> INSTANCES = new ConcurrentHashMap<>();

    static {
        COMMAND_HANDLERS.put(1, AlarmImpl.class);
        COMMAND_HANDLERS.put(2, RebootImpl.class);
        COMMAND_HANDLERS.put(3, VolumeImpl.class);
        COMMAND_HANDLERS.put(4, ScreenShotImpl.class);
        COMMAND_HANDLERS.put(5, FileImpl.class);
        COMMAND_HANDLERS.put(7, DeviceInfoImpl.class);
        COMMAND_HANDLERS.put(9, SocketImpl.class);
        COMMAND_HANDLERS.put(10, SocketImpl.class);
        COMMAND_HANDLERS.put(12, FileImpl.class);
        COMMAND_HANDLERS.put(16, LogcatImpl.class);
        COMMAND_HANDLERS.put(17, LogcatImpl.class);
    }

    public static synchronized IAbility getAbility(Context context, int cmdType) {
        if (INSTANCES.containsKey(cmdType)) {
            return INSTANCES.get(cmdType);
        }
        IAbility newInstance = createInstance(context, cmdType);
        if (newInstance == null) {
            Log.e(TAG, "getAbility: create instance error");
            return null;
        }
        INSTANCES.put(cmdType, newInstance);
        return newInstance;
    }

    private static IAbility createInstance(Context context, int cmdType) {
        Class<? extends IAbility> clazz = COMMAND_HANDLERS.get(cmdType);
        if (clazz == null) {
            return null;
        }
        IAbility ability = null;
        try {
            ability = clazz.newInstance();
            ability.init(context);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return ability;
    }
}
