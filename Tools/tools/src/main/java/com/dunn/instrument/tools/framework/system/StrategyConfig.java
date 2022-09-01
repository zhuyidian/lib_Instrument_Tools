package com.dunn.instrument.tools.framework.system;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Locale;

/**
 * Created by Administrator on 2018/3/9.
 */

/*
常用的手机信息获取
 */
public class StrategyConfig {
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

    private volatile static StrategyConfig instance;

    private StrategyConfig(){

    }
    public static StrategyConfig getInstance(){
        if(instance==null){
            synchronized (StrategyConfig.class){
                if(instance==null){
                    instance=new StrategyConfig();
                }
            }
        }
        return instance;
    }

    private void initStrategy(final Context mContext){
        PackageManager pkgManager = mContext.getPackageManager();
        // 读写 sd card 权限非常重要, android6.0默认禁止的, 建议初始化之前就弹窗让用户赋予该权限
        boolean sdCardWritePermission =
                pkgManager.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, mContext.getPackageName()) == PackageManager.PERMISSION_GRANTED;
        // read phone state用于获取 imei 设备信息
        boolean phoneSatePermission =
                pkgManager.checkPermission(Manifest.permission.READ_PHONE_STATE, mContext.getPackageName()) == PackageManager.PERMISSION_GRANTED;
        //DeviceBrand
        String mDeviceBrand = getDeviceBrand();
        //language
        String mLanguage = getSystemLanguage();
        //SystemVersion
        String mSystemVersion = getSystemVersion();
        //SystemModel
        String mSystemModel = getSystemModel();
        //IMEI
        String mIMEI = getIMEI(mContext);
        //SDK
        int mSDK = getSDK();
        int mSDKINT = getSDKINT();
        //EMUI
        String mEmui = getEMUI();
        String emuiApi = getEMUIApi();
        int versionEmui = 0;
        if(emuiApi!=null){
            if(!emuiApi.isEmpty()){
                versionEmui = Integer.parseInt(getEMUIApi());
            }
        }
        ROM_TYPE eType = getRomType(mContext);

        if (((mDeviceBrand.toUpperCase().indexOf("HUAWEI") != -1) || (mDeviceBrand.toUpperCase().indexOf("HONOR") != -1) || (Build.MANUFACTURER.toUpperCase().indexOf("HUAWEI") != -1)) &&
                (eType == ROM_TYPE.EMUI) &&
                (versionEmui > 9)  //>4.1
                ) {
            //HUAWEI
        } else if (((mDeviceBrand.toUpperCase().indexOf("XIAOMI") != -1) || (Build.MANUFACTURER.toUpperCase().indexOf("XIAOMI") != -1)) &&
                (eType == ROM_TYPE.MIUI)
                ) {
            //XIAOMI
        } else if (((mDeviceBrand.toUpperCase().indexOf("MEIZU") != -1) || (Build.MANUFACTURER.toUpperCase().indexOf("MEIZU") != -1)) &&
                (eType == ROM_TYPE.FLYME)
                ) {
            //MEIZU
        } else if ((mDeviceBrand.toUpperCase().indexOf("GOOGLE") != -1) && isGooglePhone((Activity) mContext)) {
           //GOOGLE
        } else {

        }
    }

    /**
     * 获取当前手机系统语言。
     *
     * @return 返回当前系统语言。例如：当前设置的是“中文-中国”，则返回“zh-CN”
     */
    private String getSystemLanguage() {
        return Locale.getDefault().getLanguage();
    }

    /**
     * 获取当前系统上的语言列表(Locale列表)
     *
     * @return  语言列表
     */
    private Locale[] getSystemLanguageList() {
        return Locale.getAvailableLocales();
    }

    /**
     * 获取当前手机系统版本号
     *
     * @return  系统版本号
     */
    private String getSystemVersion() {
        return Build.VERSION.RELEASE;
    }

    /**
     * 获取手机型号
     *
     * @return  手机型号
     */
    private String getSystemModel() {
        return Build.MODEL;
    }

    /**
     * 获取手机厂商
     *
     * @return  手机厂商
     */
    private String getDeviceBrand() {
        return Build.BRAND;
    }

    /**
     * 获取手机IMEI(需要“android.permission.READ_PHONE_STATE”权限)
     *
     * @return  手机IMEI
     */
    private String getIMEI(Context ctx) {
        TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Activity.TELEPHONY_SERVICE);
        if (tm != null) {
            return tm.getDeviceId();
        }
        return null;
    }

    /**
     * 获取SDK版本
     *
     * @return
     */
    private int getSDK() {
        return Integer.parseInt(Build.VERSION.SDK);
    }

    private int getSDKINT(){
        return Build.VERSION.SDK_INT;
    }

    //判断是不是google原生手机
    public static boolean isGooglePhone(final Activity context){
        //需要使用google API
//        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
//        int   resultCode = googleApiAvailability.isGooglePlayServicesAvailable(context);
//        if(resultCode != ConnectionResult.SUCCESS) {
//            if(googleApiAvailability.isUserResolvableError(resultCode)) {
//                googleApiAvailability.getErrorDialog(context, resultCode, 2404).show();
//            }
//            return false;
//        }
//        return true;
        return false;
    }

    public String getEMUI() {
        Class<?> classType = null;
        String buildVersion = null;
        try {
            classType = Class.forName("android.os.SystemProperties");
            Method getMethod = classType.getDeclaredMethod("get", new Class<?>[]{String.class});
            buildVersion = (String) getMethod.invoke(classType, new Object[]{"ro.build.version.emui"});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buildVersion==null||buildVersion.equals("")?"0":buildVersion;
    }

    public String getEMUIApi() {
        Class<?> classType = null;
        String buildVersion = null;
        try {
            classType = Class.forName("android.os.SystemProperties");
            Method getMethod = classType.getDeclaredMethod("get", new Class<?>[]{String.class});
            buildVersion = (String) getMethod.invoke(classType, new Object[]{"ro.build.hw_emui_api_level"});
        } catch (Exception e) {
            e.printStackTrace();
            buildVersion="0";
        }
        return buildVersion==null||buildVersion.equals("")?"0":buildVersion;
    }

    public String getEMUIVersion() {
        Class<?> classType = null;
        String buildVersion = null;
        try {
            classType = Class.forName("android.os.SystemProperties");
            Method getMethod = classType.getDeclaredMethod("get", new Class<?>[]{String.class});
            buildVersion = (String) getMethod.invoke(classType, new Object[]{"ro.confg.hw_systemversion"});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buildVersion;
    }

    /**
     * @param
     * @return ROM_TYPE ROM类型的枚举
     * @description获取ROM类型: MIUI_ROM, FLYME_ROM, EMUI_ROM, OTHER_ROM
     */

    public ROM_TYPE getRomType(final Context mContext) {
        ROM_TYPE rom_type = ROM_TYPE.OTHER;
        try {
            BuildProperties buildProperties = BuildProperties.newInstance();

            if (buildProperties.containsKey(KEY_EMUI_VERSION_CODE) || buildProperties.containsKey(KEY_EMUI_API_LEVEL) || buildProperties.containsKey(KEY_EMUI_CONFIG_HW_SYS_VERSION)) {
                return ROM_TYPE.EMUI;
            }
            if (buildProperties.containsKey(KEY_MIUI_VERSION_CODE) || buildProperties.containsKey(KEY_MIUI_VERSION_NAME) || buildProperties.containsKey(KEY_MIUI_INTERNAL_STORAGE)) {
                return ROM_TYPE.MIUI;
            }
            if (buildProperties.containsKey(KEY_FLYME_ICON_FALG) || buildProperties.containsKey(KEY_FLYME_SETUP_FALG) || buildProperties.containsKey(KEY_FLYME_PUBLISH_FALG)) {
                return ROM_TYPE.FLYME;
            }
            if (buildProperties.containsKey(KEY_FLYME_ID_FALG_KEY)) {
                String romName = buildProperties.getProperty(KEY_FLYME_ID_FALG_KEY);
                if (!TextUtils.isEmpty(romName) && romName.contains(KEY_FLYME_ID_FALG_VALUE_KEYWORD)) {
                    return ROM_TYPE.FLYME;
                }
            }
        } catch (IOException e) {
            //这里会存在权限问题
        }

        return rom_type;
    }

    public enum ROM_TYPE {
        MIUI,
        FLYME,
        EMUI,
        OTHER
    }

}
