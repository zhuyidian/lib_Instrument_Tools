package com.dunn.tools.reflect;

import android.text.TextUtils;

import com.dunn.tools.log.LogUtil;

import java.lang.reflect.Array;
import java.lang.reflect.Field;

public class ReflectUtil {
    private ReflectUtil() {
        throw new UnsupportedOperationException();
    }

    public static <T> T createClassObject(String className) {
        if (!TextUtils.isEmpty(className))
            try {
                return (T) Class.forName(className).newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
        return null;
    }

    /**
     * 根据ClassName 反射执行方法
     * 注：
     * ClassName是全路径名称:xxx.xxx.xxx.xxx.ClassName
     * @param clazzName : Class名称
     * @param method : 方法名称
     * @param bargs : 参数列表
     */
    public static void exec(String clazzName, String method, Object[] bargs){
        //根据ClassName获取Class
        Class<?> iclazz = null;
        try {
            iclazz = Reflect.on(clazzName).get();  //clazzName必须是全路径
        }catch (Exception e){
            e.printStackTrace();
            LogUtil.i("reflect", "clazzName="+clazzName+", e=" + e);
            return;
        }
//        try {
//            iclazz = Class.forName(clazzName);   //clazzName必须是全路径
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//            LogUtil.i("reflect", "clazzName="+clazzName+", e=" + e);
//            return;
//        }
        LogUtil.i("reflect", "clazzName="+clazzName+", iclazz=" + iclazz);
        Object createObject = null;
        try {
            // TODO: 处理单例的类
            // TODO: 处理静态函数
            // TODO: 避免重复创建new
            //根据class创建对象
            createObject = Reflect.on(iclazz).create().get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        LogUtil.i("reflect", "createObject=" + createObject+", method="+method+", bargs="+bargs);
        //执行对象的方法,result是返回值
        Object result = Reflect.on(createObject)
                .call(method, bargs).get();
        LogUtil.i("reflect", "result=" + result);
    }

    public static Field getFiled(Object object, String filedName) {
        Class<?> objectClass = object.getClass();
        while (!objectEquals(objectClass.getName(), Object.class.getName())) {
            try {
                Field field = objectClass.getDeclaredField(filedName);
                field.setAccessible(true);
                return field;
            } catch (NoSuchFieldException e) {
                objectClass = objectClass.getSuperclass();
            }
        }
        return null;
    }

    public static Object getFiledValue(Object object, String filedName) {
        Field field = getFiled(object, filedName);
        try {
            return field.get(object);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 往List最开始的地方插入一个值
     * @param firstObj
     * @param array
     * @return
     */
    public static Object insertElementAtFirst(Object firstObj, Object array) {
        Class<?> localClass = array.getClass().getComponentType();
        int len = Array.getLength(array) + 1;
        Object result = Array.newInstance(localClass, len);
        Array.set(result, 0, firstObj);
        for (int k = 1; k < len; ++k) {
            Array.set(result, k, Array.get(array, k - 1));
        }
        return result;
    }

    public static boolean objectEquals(Object a, Object b) {
        return (a == b) || (a != null && a.equals(b));
    }
}
