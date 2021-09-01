package com.dunn.tools.time.Ability;

import android.content.Context;
import android.media.AudioManager;
import android.util.Log;

import com.dunn.tools.log.LogUtil;
import com.dunn.tools.time.TimeManager;
import com.dunn.tools.time.bean.RemoteCommand;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @ClassName: VolumeImpl
 * @Author: XuZeXiao
 * @CreateDate: 3/31/21 8:30 PM
 * @Description:
 */
public class TestImpl extends AbsAbility {
    private static final String TAG = "VolumeImpl";

    @Override
    public void handleMessage(RemoteCommand command) {
        boolean isReal = TimeManager.getInstance().onMessage(command, this);
        if (isReal) return;
        LogUtil.i("time", "---handle message--- command=" + command);
    }

    @Override
    public void realMessage(RemoteCommand command) {
        LogUtil.i("time", "---real message--- command=" + command);
    }

    @Override
    public String getName() {
        return null;
    }
}
