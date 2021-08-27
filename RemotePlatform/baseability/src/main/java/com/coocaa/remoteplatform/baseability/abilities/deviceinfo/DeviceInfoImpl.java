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
            reportInfo.volume = mAudio.getVolume();
        } catch (Exception e) {
            e.printStackTrace();
        }
        reportInfo.bootPlain = DeviceInfoUtils.getAlarm(mContext);
        reportInfo.supportOpen = DeviceInfoUtils.isSupportAlarm();
        Map<String, String> params = new HashMap<>();
        params.put(Constant.REMOTE_METHOD, "isQuickKeyOpen");
        Map<String, String> result = RemotePlatform.getInstance().packageMethod("com.fusionmedia.show", params);
        if (result != null) {
            boolean isOpen = Boolean.parseBoolean(result.get(Constant.REMOTE_METHOD_RESULT_KEY));
            Log.i(TAG, "getInfo: isQuickKeyOpen: " + isOpen);
            reportInfo.quickKey = isOpen;
        }
        return reportInfo;
    }

    @Override
    public String getName() {
        return null;
    }
}
