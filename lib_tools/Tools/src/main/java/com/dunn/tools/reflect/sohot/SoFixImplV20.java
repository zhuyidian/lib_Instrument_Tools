package com.dunn.tools.reflect.sohot;

import android.content.Context;
import android.os.Build;

import com.dunn.tools.reflect.ReflectUtil;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import dalvik.system.DexFile;

/**
 * 6.0 以下的版本
 */
public class SoFixImplV20 extends SoFix{
    public SoFixImplV20(Context context){
        super(context);
    }

    @Override
    protected void reflectNativeLibraryElements() {
        mNativeLibraryElementsField = ReflectUtil.getFiled(mPathList, "nativeLibraryDirectories");
    }

    @Override
    public Object createNativeLibraryElement(String soPath) throws Exception{
        return new File(soPath);
    }
}
