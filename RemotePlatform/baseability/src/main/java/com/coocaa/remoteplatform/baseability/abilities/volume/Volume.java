package com.coocaa.remoteplatform.baseability.abilities.volume;

import com.google.gson.annotations.SerializedName;

public class Volume {

    /**
     * type : 0:绝对值 1:减小音量 2:增加音量
     * volume : 0-100  type=0时，代表绝对值音量; type=1或2时，代表想要增加或减少多少音量 type=1时 音量值最低减为0,最高增到100
     */

    @SerializedName("type")
    private int type;
    @SerializedName("volume")
    private int volume;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }
}
