package com.dunn.instrument.tools.framework.system;

import android.content.Context;

import java.io.File;
import java.lang.reflect.Method;

import dalvik.system.DexFile;

/*
通过反射系统类android.os.SystemProperties，来获取配置信息
这里以判断小米，华为，魅族手机为例
反射方式最准确
 */
public class SystemPropertiesProxy {
    //MIUI标识
    private static final String KEY_MIUI_VERSION_CODE = "ro.miui.ui.version.code";
    private static final String KEY_MIUI_VERSION_NAME = "ro.miui.ui.version.name";
    private static final String KEY_MIUI_INTERNAL_STORAGE = "ro.miui.internal.storage";

    //EMUI标识
    private static final String KEY_EMUI_VERSION_CODE = "ro.build.version.emui";
    private static final String KEY_EMUI_API_LEVEL = "ro.build.hw_emui_api_level";
    private static final String KEY_EMUI_CONFIG_HW_SYS_VERSION = "ro.confg.hw_systemversion";

    //Flyme标识
    private static final String KEY_FLYME_ID_FALG_KEY = "ro.build.display.id";
    private static final String KEY_FLYME_ID_FALG_VALUE_KEYWORD = "Flyme";
    private static final String KEY_FLYME_ICON_FALG = "persist.sys.use.flyme.icon";
    private static final String KEY_FLYME_SETUP_FALG = "ro.meizu.setupwizard.flyme";
    private static final String KEY_FLYME_PUBLISH_FALG = "ro.flyme.published";

    public static boolean isFlyme(Context mContext) {
        if(   (getString(mContext,KEY_FLYME_ICON_FALG)!=null && !getString(mContext,KEY_FLYME_ICON_FALG).equals(""))
            ||(getString(mContext,KEY_FLYME_SETUP_FALG)!=null && !getString(mContext,KEY_FLYME_SETUP_FALG).equals(""))
            ||(getString(mContext,KEY_FLYME_PUBLISH_FALG)!=null && !getString(mContext,KEY_FLYME_PUBLISH_FALG).equals("")) ) {
            return true;
        }
        return false;
    }

    public static boolean isEMUI(Context mContext) {
        if(   (getString(mContext,KEY_EMUI_VERSION_CODE)!=null && !getString(mContext,KEY_EMUI_VERSION_CODE).equals(""))
            ||(getString(mContext,KEY_EMUI_API_LEVEL)!=null && !getString(mContext,KEY_EMUI_API_LEVEL).equals(""))
            ||(getString(mContext,KEY_EMUI_CONFIG_HW_SYS_VERSION)!=null && !getString(mContext,KEY_EMUI_CONFIG_HW_SYS_VERSION).equals("")) ) {
            return true;
        }
        return false;
    }

    public static boolean isMIUI(Context mContext) {
        if(   (getString(mContext,KEY_MIUI_VERSION_CODE)!=null && !getString(mContext,KEY_MIUI_VERSION_CODE).equals(""))
            ||(getString(mContext,KEY_MIUI_VERSION_NAME)!=null && !getString(mContext,KEY_MIUI_VERSION_NAME).equals(""))
            ||(getString(mContext,KEY_MIUI_INTERNAL_STORAGE)!=null && !getString(mContext,KEY_MIUI_INTERNAL_STORAGE).equals("")) ) {
            return true;
        }
        return false;
    }

    /**
     * 根据给定的Key返回String类型的值
     *
     * @param context 上下文
     * @param key     获取指定信息所需的key
     * @return 返回一个String类型的值，如果不存在该key则返回空字符串
     */
    private static String getString(Context context, String key) {
        String result = "";
        try {
            ClassLoader classLoader = context.getClassLoader();
            @SuppressWarnings("rawtypes")
            Class SystemProperties = classLoader.loadClass("android.os.SystemProperties");
            //参数类型
            @SuppressWarnings("rawtypes")
            Class[] paramTypes = new Class[1];
            paramTypes[0] = String.class;
            Method getString = SystemProperties.getMethod("get", paramTypes);
            //参数
            Object[] params = new Object[1];
            params[0] = new String(key);

            result = (String) getString.invoke(SystemProperties, params);
        } catch (IllegalArgumentException e) {
            //e.printStackTrace();
            //如果key超过32个字符则抛出该异常

        } catch (Exception e) {

            result = "";
        }
        return result;
    }

    /**
     * 根据给定的Key返回String类型的值
     *
     * @param context 上下文
     * @param key     获取指定信息所需的key
     * @param def     key不存在时的默认值
     * @return 返回一个String类型的值，如果key不存在, 并且如果def不为null则返回def,否则返回空字符串
     */
    private static String getString(Context context, String key, String def) {
        String result = def;
        try {
            ClassLoader classLoader = context.getClassLoader();
            @SuppressWarnings("rawtypes")
            Class SystemProperties = classLoader.loadClass("android.os.SystemProperties");
            //参数类型
            @SuppressWarnings("rawtypes")
            Class[] paramTypes = new Class[2];
            paramTypes[0] = String.class;
            paramTypes[1] = String.class;
            Method getString = SystemProperties.getMethod("get", paramTypes);
            //参数
            Object[] params = new Object[2];
            params[0] = new String(key);
            params[1] = new String(def);

            result = (String) getString.invoke(SystemProperties, params);
        } catch (IllegalArgumentException e) {
            //e.printStackTrace();
            //如果key超过32个字符则抛出该异常

        } catch (Exception e) {

            result = def;
        }
        return result;
    }

    /**
     * 根据给定的key返回int类型的值
     *
     * @param context 上下文
     * @param key     要查询的key
     * @param def     默认返回值
     * @return 返回一个int类型的值，如果没有发现则返回默认值 def
     */
    private static Integer getInt(Context context, String key, int def) {
        Integer result = def;
        try {
            ClassLoader classLoader = context.getClassLoader();
            @SuppressWarnings("rawtypes")
            Class SystemProperties = classLoader.loadClass("android.os.SystemProperties");
            //参数类型
            @SuppressWarnings("rawtypes")
            Class[] paramTypes = new Class[2];
            paramTypes[0] = String.class;
            paramTypes[1] = int.class;
            Method getInt = SystemProperties.getMethod("getInt", paramTypes);
            //参数
            Object[] params = new Object[2];
            params[0] = new String(key);
            params[1] = new Integer(def);
            result = (Integer) getInt.invoke(SystemProperties, params);
        } catch (IllegalArgumentException e) {
            //e.printStackTrace();
            //如果key超过32个字符则抛出该异常
        } catch (Exception e) {
            result = def;
        }
        return result;
    }

    /**
     * 根据给定的key返回long类型的值
     *
     * @param context 上下文
     * @param key     要查询的key
     * @param def     默认返回值
     * @return 返回一个long类型的值，如果没有发现则返回默认值def
     */
    private static Long getLong(Context context, String key, long def) {
        Long result = def;
        try {
            ClassLoader classLoader = context.getClassLoader();
            @SuppressWarnings("rawtypes")
            Class SystemProperties = classLoader.loadClass("android.os.SystemProperties");
            //参数类型
            @SuppressWarnings("rawtypes")
            Class[] paramTypes = new Class[2];
            paramTypes[0] = String.class;
            paramTypes[1] = long.class;
            Method getLong = SystemProperties.getMethod("getLong", paramTypes);
            //参数
            Object[] params = new Object[2];
            params[0] = new String(key);
            params[1] = new Long(def);
            result = (Long) getLong.invoke(SystemProperties, params);
        } catch (IllegalArgumentException e) {
            //e.printStackTrace();
            //如果key超过32个字符则抛出该异常
        } catch (Exception e) {
            result = def;
        }
        return result;
    }

    /**
     * 根据给定的key返回boolean类型的值
     * 如果值为'n','no','0','false' or 'off'返回false
     * 如果值为'y','yes','1','true' or 'on'返回true
     * 如果key不存在, 或者是其它的值, 则返回默认值
     *
     * @param context 上下文
     * @param key     要查询的key
     * @param def     默认返回值
     * @return 返回一个boolean类型的值，如果没有发现则返回默认值def
     */
    private static Boolean getBoolean(Context context, String key, boolean def) {
        Boolean result = def;
        try {
            ClassLoader classLoader = context.getClassLoader();
            @SuppressWarnings("rawtypes")
            Class SystemProperties = classLoader.loadClass("android.os.SystemProperties");
            //参数类型
            @SuppressWarnings("rawtypes")
            Class[] paramTypes = new Class[2];
            paramTypes[0] = String.class;
            paramTypes[1] = boolean.class;
            Method getBoolean = SystemProperties.getMethod("getBoolean", paramTypes);
            //参数
            Object[] params = new Object[2];
            params[0] = new String(key);
            params[1] = new Boolean(def);
            result = (Boolean) getBoolean.invoke(SystemProperties, params);
        } catch (IllegalArgumentException e) {
            //e.printStackTrace();
            //如果key超过32个字符则抛出该异常
        } catch (Exception e) {
            result = def;
        }
        return result;
    }

    /**
     * 根据给定的key和值设置属性, 该方法需要特定的权限才能操作.
     *
     * @param context 上下文
     * @param key     设置属性的key
     * @param val     设置属性的value
     */
    private static void set(Context context, String key, String val) {
        try {
            @SuppressWarnings("rawtypes")
            DexFile df = new DexFile(new File("/system/app/Settings.apk"));
            ClassLoader classLoader = context.getClassLoader();
            @SuppressWarnings("rawtypes")
            Class SystemProperties = Class.forName("android.os.SystemProperties");
            //参数类型
            @SuppressWarnings("rawtypes")
            Class[] paramTypes = new Class[2];
            paramTypes[0] = String.class;
            paramTypes[1] = String.class;
            Method set = SystemProperties.getMethod("set", paramTypes);
            //参数
            Object[] params = new Object[2];
            params[0] = new String(key);
            params[1] = new String(val);
            set.invoke(SystemProperties, params);
        } catch (IllegalArgumentException e) {
            //e.printStackTrace();
            //如果key超过32个字符或者value超过92个字符则抛出该异常
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
