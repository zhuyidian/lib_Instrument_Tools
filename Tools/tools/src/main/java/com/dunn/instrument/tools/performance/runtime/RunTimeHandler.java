package com.dunn.instrument.tools.performance.runtime;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by dunn on 2018/6/28.
 */

public class RunTimeHandler implements InvocationHandler{
    private Object obj;

    public RunTimeHandler(Object obj){
        this.obj = obj;
    }

    /*
//    参数obj：需要代理的对象
//    返回值：代理对象
//    注：创建Handler对象传递的参数为需要代理的对象
     */
    public static Object newInstance(Object obj){
        Object proxyObj = Proxy.newProxyInstance(obj.getClass().getClassLoader(),obj.getClass().getInterfaces(),new RunTimeHandler(obj));
        return proxyObj;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result;
        try{
            System.out.println("runtime handler invoke : start method="+method.getName());
            long start = Memory.used();
            result = method.invoke(obj,args);
            long end = Memory.used();
            System.out.println("runtime handler invoke : end method="+method.getName()+", use="+(end-start)+"bytes");
        }catch (InvocationTargetException e){
            throw e.getTargetException();
        }catch (Exception e){
            throw new RuntimeException("runtime exception:"+e.getMessage());
        }finally {
            //System.out.println("handler invoke : method="+method.getName());
        }
        return result;
    }
}
