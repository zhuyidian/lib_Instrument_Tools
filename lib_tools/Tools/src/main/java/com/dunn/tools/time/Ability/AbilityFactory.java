package com.dunn.tools.time.Ability;

import android.content.Context;
import android.util.Log;

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
        COMMAND_HANDLERS.put(0, TestImpl.class);
        COMMAND_HANDLERS.put(1, TestImpl.class);
        COMMAND_HANDLERS.put(2, TestImpl.class);
        COMMAND_HANDLERS.put(3, TestImpl.class);
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
