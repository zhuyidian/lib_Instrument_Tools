package com.dunn.tools.threadsafety;

import android.util.Log;

import com.dunn.tools.log.LogUtil;
import com.dunn.tools.time.TimeUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ListUtil {
    private static volatile boolean stop = false;
    private static Lock lock = new ReentrantLock();
    private static List<String> arrayList = new ArrayList<>(8);

    public static void setStop(){
        stop = true;
        LogUtil.v("thread-safety", "setStop stop="+stop);
    }

    public static void setDataForSynchronized() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (arrayList) {
                    LogUtil.v("thread-safety", "arraylist set data start");
                    String name = "dunn-";
                    while(arrayList.size()<=1000){
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        arrayList.add(name+arrayList.size());
                    }
                    LogUtil.v("thread-safety", "arraylist set data end");
                }
            }
        }).start();
    }

    public static void getDataForSynchronized() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                LogUtil.v("thread-safety", "arraylist get data start");
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //synchronized (arrayList) {   //需要用线程同步去读,否则会发生ConcurrentModificationException crash
                Iterator<String> iterator = arrayList.iterator();
                while(iterator.hasNext()){
                    String name = iterator.next();
                    LogUtil.v("thread-safety", "arraylist get data .... name=" + name);
                }
                //}
                LogUtil.v("thread-safety", "arraylist get data end");
            }
        }).start();
    }

    public static void setDataForLock() {
        stop = false;
        arrayList.clear();
        new Thread(new Runnable() {
            @Override
            public void run() {
                LogUtil.v("thread-safety", "arraylist set data ########start#######");

                lock.lock();
                String name = "dunn-";
                while(stop==false){
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    arrayList.add(name + TimeUtil.long2string(System.currentTimeMillis()));
                }
                lock.unlock();

                LogUtil.v("thread-safety", "arraylist set data ########end########");
            }
        }).start();
    }

    public static void getDataForLock() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                LogUtil.v("thread-safety", "arraylist get data @@@@@@start@@@@@@");

                while (true) {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    LogUtil.v("thread-safety", "arraylist get data .... tryLock=" + lock.tryLock());
                    lock.lock();
                    for (int i=0; i<3; i++) {
                        String name = arrayList.get(i);
                        LogUtil.v("thread-safety", "arraylist get data .... name=" + name);
                    }
                    lock.unlock();
                }

                //LogUtil.v("thread-safety", "arraylist get data @@@@@@end@@@@@@");
            }
        }).start();
    }
}
