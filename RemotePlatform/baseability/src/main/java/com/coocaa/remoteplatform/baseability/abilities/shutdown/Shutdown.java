package com.coocaa.remoteplatform.baseability.abilities.shutdown;

import com.google.gson.annotations.SerializedName;

public class Shutdown {
    /**
     * planType : 0:立即实时关机
     */

    @SerializedName("planType")
    private int planType;

    public int getPlanType() {
        return planType;
    }

    public void setPlanType(int planType) {
        this.planType = planType;
    }

}
