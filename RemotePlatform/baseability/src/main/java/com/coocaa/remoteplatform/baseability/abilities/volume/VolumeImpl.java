package com.coocaa.remoteplatform.baseability.abilities.volume;

import android.content.Context;
import android.media.AudioManager;
import android.util.Log;

import com.coocaa.remoteplatform.baseability.abilities.AbsAbility;
import com.coocaa.remoteplatform.core.common.RemoteCommand;
import com.google.gson.Gson;

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
        Gson gson = new Gson();

        Volume volume = gson.fromJson(command.content, Volume.class);
        if (volume == null) {
            command.replyError(mContext).reply();
            return;
        }
        IAudio iAudio = SAL.getModule(mContext, SAL.AUDIO);
        int currentVolume = 0;
        try {
            currentVolume = iAudio.getVolume();
        } catch (Exception e) {
            e.printStackTrace();
        }
        int targetVolume = 0;
        Log.i(TAG, "handleMessage volume.Type=" + volume.getType()+", reboot.Volume="+volume.getVolume());
        switch (volume.getType()) {
            case 0:
                targetVolume = volume.getVolume();
                break;
            case 1:
                targetVolume = Math.max(currentVolume - volume.getVolume(), 0);
                break;
            case 2:
                targetVolume = Math.min(currentVolume + volume.getVolume(), 100);
                break;
        }

        try {
            iAudio.setVolume(targetVolume);
        } catch (Exception e) {
            e.printStackTrace();
            setVolume(targetVolume);
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("volume", targetVolume);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        command.replyFinish(mContext).withContent(jsonObject.toString()).reply();
    }

    @Override
    public String getName() {
        return null;
    }

    private void setVolume(int volume) {
        AudioManager manager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        manager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, AudioManager.FLAG_SHOW_UI);
    }

    private int getVolume() {
        AudioManager manager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        return manager.getStreamVolume(AudioManager.STREAM_MUSIC);
    }
}
