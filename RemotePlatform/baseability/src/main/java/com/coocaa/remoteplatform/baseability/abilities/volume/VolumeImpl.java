package com.coocaa.remoteplatform.baseability.abilities.volume;

import android.content.Context;
import android.media.AudioManager;
import android.util.Log;

import com.coocaa.remoteplatform.baseability.abilities.AbsAbility;
import com.coocaa.remoteplatform.baseability.abilities.time.TimeManager;
import com.coocaa.remoteplatform.core.common.RemoteCommand;
import com.google.gson.Gson;
import com.remoteplatform.commom.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

import swaiotos.sal.SAL;
import swaiotos.sal.audio.IAudio;

/**
 * @ClassName: VolumeImpl
 * @Author: XuZeXiao
 * @CreateDate: 3/31/21 8:30 PM
 * @Description:
 */
public class VolumeImpl extends AbsAbility {
    private static final String TAG = "VolumeImpl";

    @Override
    public void handleMessage(RemoteCommand command) {
        boolean isReal = TimeManager.getInstance().onMessage(command, this);
        if (isReal) return;
        LogUtil.i("time", "---handle message--- command=" + command);

        Gson gson = new Gson();
        Volume volume = gson.fromJson(command.content, Volume.class);
        dealVolume(mContext, command, volume);
    }

    @Override
    public void realMessage(RemoteCommand command) {
        LogUtil.i("time", "---real message--- command=" + command);

        Gson gson = new Gson();
        Volume volume = gson.fromJson(command.content, Volume.class);
        dealVolume(mContext, command, volume);
    }

    @Override
    public String getName() {
        return null;
    }

    private void dealVolume(Context mContext, RemoteCommand command, Volume volume) {
        if (volume == null) {
            command.replyError(mContext).reply();
            return;
        }

        LogUtil.i("time", "---deal message--- volume.Type=" + volume.getType() + ", reboot.Volume=" + volume.getVolume());

        IAudio iAudio = SAL.getModule(mContext, SAL.AUDIO);
        int currentVolume = 0;
        try {
            currentVolume = iAudio.getVolume();
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e("time", "---deal message--- e=" + e);
        }

        int maxVolume = getMaxVolume();
        int targetVolume = convertType(volume.getType(), currentVolume, volume.getVolume());
        int convertVolume = convertVolume(targetVolume, maxVolume);
        LogUtil.i("time", "---deal message--- currentVolume=" + currentVolume + ", maxVolume=" + maxVolume + ", targetVolume=" + targetVolume +
                ", convertVolume=" + convertVolume);
        setVolume(iAudio, convertVolume);

        resultReply(mContext, command, targetVolume);
    }

    private void setVolume(IAudio iAudio, int volume) {
        try {
            iAudio.setVolume(volume);
            return;
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e("time", "e=" + e);
        }

        try {
            AudioManager manager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
            manager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, AudioManager.FLAG_SHOW_UI);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e("time", "e=" + e);
        }
    }

    private int getVolume() {
        AudioManager manager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        return manager.getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    private int getMaxVolume() {
        try {
            AudioManager manager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
            return manager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e("time", "e=" + e);
        }

        return 100;
    }

    private int convertType(int type, int currentVolume, int volume) {
        int targetVolume = 0;
        switch (type) {
            case 0:
                targetVolume = volume;
                break;
            case 1:
                targetVolume = Math.max(currentVolume - volume, 0);
                break;
            case 2:
                targetVolume = Math.min(currentVolume + volume, 100);
                break;
        }

        return targetVolume;
    }

    private int convertVolume(int targetVolume, int maxValue) {
        int target = (int) ((float) targetVolume / 100 * maxValue);
        if (target > maxValue) target = maxValue;
        if (target < 0) target = 0;
        return target;
    }

    private void resultReply(Context mContext, RemoteCommand command, int targetVolume) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("volume", targetVolume);
        } catch (JSONException e) {
            e.printStackTrace();
            LogUtil.i("time", "e=" + e);
        }
        command.replyFinish(mContext).withContent(jsonObject.toString()).reply();
    }
}
