package com.dunn.tools.performance.time;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by dunn on 2018/6/28.
 */

public class TimeHandler implements InvocationHandler{
    private Object obj;

    public TimeHandler(Object obj){
        this.obj = obj;
    }

    /*
    参数obj：需要代理的对象
    返回值：代理对象
    注：创建Handler对象传递的参数为需要代理的对象
     */
    public static Object newInstance(Object obj){
        Object proxyObj = Proxy.newProxyInstance(obj.getClass().getClassLoader(),obj.getClass().getInterfaces(),new TimeHandler(obj));
        return proxyObj;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result;
        try{
            System.out.println("time handler invoke : start method="+method.getName());
            long startTime = System.currentTimeMillis();
            result = method.invoke(obj,args);
            long endTime = System.currentTimeMillis();
            System.out.println("time handler invoke : end method="+method.getName()+", time="+(endTime-startTime)+"ms");
        }catch (InvocationTargetException e){
            throw e.getTargetException();
        }catch (Exception e){
            throw new RuntimeException("time exception:"+e.getMessage());
        }finally {
            //System.out.println("handler invoke : method="+method.getName());
        }
        return result;
    }
}
