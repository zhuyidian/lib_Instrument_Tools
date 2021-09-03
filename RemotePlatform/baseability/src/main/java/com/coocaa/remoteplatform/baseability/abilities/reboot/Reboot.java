package com.coocaa.remoteplatform.baseability.abilities.reboot;

import com.google.gson.annotations.SerializedName;

public class Reboot {
    /**
     * planType : 0:立即实时重启 1:延时（一次性）
     * delay : 当planType为1时必须填，表示重启时间点:2021-03-05 23:59:59
     */

    @SerializedName("planType")
    private int planType;
    @SerializedName("delay")
    private String delay;

    public int getPlanType() {
        return planType;
    }

    public void setPlanType(int planType) {
        this.planType = planType;
    }

    public String getDelay() {
        return delay;
    }

    public void setDelay(String delay) {
        this.delay = delay;
    }
}
