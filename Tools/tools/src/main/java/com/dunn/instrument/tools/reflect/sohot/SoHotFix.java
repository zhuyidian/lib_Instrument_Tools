package com.dunn.instrument.tools.reflect.sohot;

import android.content.Context;
import android.os.Build;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import dalvik.system.DexFile;

/**
 * so 动态加载与热修复
 */
public class SoHotFix {
    private SoFix mSoFix;

    public SoHotFix(Context context) {
        context = context.getApplicationContext();
        // 各大版本适配
        int version = Build.VERSION.SDK_INT;
        if (version >= Build.VERSION_CODES.O) {
            mSoFix = new SoFixImplV26(context);
        } else if (version >= Build.VERSION_CODES.M) {
            mSoFix = new SoFixImplV23(context);
        } else {
            mSoFix = new SoFixImplV20(context);
        }
    }

    public void injectLoadPath(String soDir) throws Exception {
        mSoFix.hotFix(soDir);
    }
}
