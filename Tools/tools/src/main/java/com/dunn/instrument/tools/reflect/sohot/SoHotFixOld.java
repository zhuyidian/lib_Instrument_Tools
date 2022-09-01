package com.dunn.instrument.tools.reflect.sohot;

import android.content.Context;
import android.os.Environment;

import com.dunn.instrument.tools.reflect.ReflectUtil;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

/**
 * so 动态加载与热修复
 */
public class SoHotFixOld {
    private Context mContext;

    public SoHotFixOld(Context context) {
        mContext = context.getApplicationContext();
    }

    /**
     * 基于android8.0测试
     * @param soDir
     * @throws Exception
     */
    public void injectLoadPath(String soDir) throws Exception {
        ClassLoader classLoader = mContext.getClassLoader();
        Object pathList = ReflectUtil.getFiledValue(classLoader, "pathList");
        Field nativeLibraryPathElementsField = ReflectUtil.getFiled(pathList, "nativeLibraryPathElements");
        Object nativeLibraryPathElements = nativeLibraryPathElementsField.get(pathList);
        // 构造 NativeLibraryElement
        Class<?> elementClass = nativeLibraryPathElements.getClass().getComponentType();
        Constructor<?> elementConstructor = elementClass.getConstructor(File.class);
        elementConstructor.setAccessible(true);
        Object firstElement = elementConstructor.newInstance(new File(soDir));
        // 往 nativeLibraryPathElements 数组最前面插入一个 element
        Object newLibraryPathElements = ReflectUtil.insertElementAtFirst(firstElement, nativeLibraryPathElements);
        // 重新给 pathList 中的 nativeLibraryPathElements 重新赋值
        nativeLibraryPathElementsField.set(pathList, newLibraryPathElements);

        // 这个代码问题很大？下周在讲，源码适配问题
    }
}
