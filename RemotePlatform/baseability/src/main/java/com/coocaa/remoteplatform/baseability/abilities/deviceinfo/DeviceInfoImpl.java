package com.coocaa.remoteplatform.baseability.abilities.deviceinfo;

import android.content.Context;
import android.util.Log;

import com.coocaa.remoteplatform.baseability.abilities.AbsAbility;
import com.coocaa.remoteplatform.baseability.common.DeviceInfoUtils;
import com.coocaa.remoteplatform.core.common.Constant;
import com.coocaa.remoteplatform.core.common.RemoteCommand;
import com.coocaa.remoteplatform.core.service.AttachInfo;
import com.coocaa.remoteplatform.sdk.RemotePlatform;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import swaiotos.sal.SalModule;
import swaiotos.sal.audio.IAudio;

/**
 * @ClassName: DeviceInfoImpl
 * @Author: XuZeXiao
 * @CreateDate: 4/8/21 10:55 AM
 * @Description:
 */
public class DeviceInfoImpl extends AbsAbility {
    private static final String TAG = "DeviceInfoImpl";
    private static final String COM_FUSIONMEDIA_SHOW = "com.fusionmedia.show";
    private IAudio mAudio = null;
    private Gson mGson = null;

    @Override
    public void init(Context context) {
        super.init(context);
        mAudio = swaiotos.sal.SAL.getModule(mContext, SalModule.AUDIO);
        mGson = new Gson();
    }

    @Override
    public void handleMessage(RemoteCommand command) {
        Log.i(TAG, "handleMessage: ");
        command.replyProcessing(mContext).reply();
        ReportInfo reportInfo = getInfo();
        String json = mGson.toJson(reportInfo);
        Log.i(TAG, "handleMessage: json " + json);
        command.replyFinish(mContext).withContent(json).reply();
    }

    private ReportInfo getInfo() {
        ReportInfo reportInfo = new ReportInfo();
        AttachInfo attachInfo = RemotePlatform.getInstance().getAttachInfo();
        reportInfo.mac = attachInfo.getMac();
        reportInfo.deviceId = attachInfo.getDeviceId();
        reportInfo.activeId = attachInfo.getActiveId();
        try {
            reportInfo.volume = DeviceInfoUtils.convertSystemVolume(mAudio.getVolume(), DeviceInfoUtils.getMaxVolume(mContext));
            reportInfo.minVolume = DeviceInfoUtils.getMinVolume(mContext);
            reportInfo.maxVolume = DeviceInfoUtils.getMaxVolume(mContext);
        } catch (Exception e) {
            e.printStackTrace();
        }
        reportInfo.bootPlain = DeviceInfoUtils.getAlarm(mContext);
        reportInfo.supportOpen = DeviceInfoUtils.isSupportAlarm();

        reportInfo.quickKey = booleanPackageMethod(COM_FUSIONMEDIA_SHOW, "isQuickKeyOpen");
        reportInfo.isBlackScreen = booleanPackageMethod(COM_FUSIONMEDIA_SHOW, "isBlackScreen");

        return reportInfo;
    }

    private boolean booleanPackageMethod(String packageName, String name) {
        Map<String, String> packageMethodResult = packageMethod(packageName, name);
        if (packageMethodResult == null) {
            return false;
        }
        boolean result = Boolean.parseBoolean(packageMethodResult.get(Constant.REMOTE_METHOD_RESULT_KEY));
        Log.i(TAG, "booleanPackageMethod: " + name + " result: " + result);
        return result;
    }

    private Map<String, String> packageMethod(String packageName, String name) {
        Map<String, String> params = new HashMap<>(1);
        params.put(Constant.REMOTE_METHOD, name);
        return RemotePlatform.getInstance().packageMethod(packageName, params);
    }

    @Override
    public String getName() {
        return null;
    }
}
