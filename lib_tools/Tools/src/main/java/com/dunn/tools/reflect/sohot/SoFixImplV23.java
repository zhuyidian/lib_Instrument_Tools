package com.dunn.tools.reflect.sohot;

import android.content.Context;
import android.os.Build;

import com.dunn.tools.reflect.ReflectUtil;

import java.io.File;
import java.lang.reflect.Constructor;

import dalvik.system.DexFile;

/**
 * [6.0 , 8.0) 版本
 */
public class SoFixImplV23 extends SoFixImplV20 {
    public SoFixImplV23(Context context) {
        super(context);
    }

    @Override
    protected void reflectNativeLibraryElements() {
        mNativeLibraryElementsField = ReflectUtil.getFiled(mPathList, "nativeLibraryPathElements");
    }

    @Override
    public Object createNativeLibraryElement(String soPath) throws Exception {
        // 构造 Element
        Class<?> elementClass = Class.forName("dalvik.system.DexPathList$Element");
        Constructor<?> elementConstructor = elementClass.getConstructor(File.class, boolean.class, File.class, DexFile.class);
        elementConstructor.setAccessible(true);
        return elementConstructor.newInstance(new File(soPath), true, null, null);
    }
}
