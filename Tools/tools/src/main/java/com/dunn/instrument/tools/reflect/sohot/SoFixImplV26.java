package com.dunn.instrument.tools.reflect.sohot;

import android.content.Context;

import java.io.File;
import java.lang.reflect.Constructor;

import dalvik.system.DexFile;

/**
 * 8.0 以上的版本（含）
 */
public class SoFixImplV26 extends SoFixImplV23 {
    public SoFixImplV26(Context context) {
        super(context);
    }

    @Override
    public Object createNativeLibraryElement(String soPath) throws Exception {
        // 构造 Element
        Class<?> elementClass = Class.forName("dalvik.system.DexPathList$NativeLibraryElement");
        Constructor<?> elementConstructor = elementClass.getConstructor(File.class);
        elementConstructor.setAccessible(true);
        return elementConstructor.newInstance(new File(soPath));
    }
}
