package com.coocaa.remoteplatform.baseability.abilities;

import android.content.Context;

/**
 * @ClassName: AbsAbility
 * @Author: XuZeXiao
 * @CreateDate: 4/1/21 11:38 AM
 * @Description:
 */
public abstract class AbsAbility implements IAbility {
    protected Context mContext;

    @Override
    public void init(Context context) {
        mContext = context;
    }
}
