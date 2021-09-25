package com.dunn.tools.performance.runtime;

/**
 * Created by Administrator on 2018/6/28.
 */

public class Memory {
    public static long used(){
        long total = Runtime.getRuntime().totalMemory();
        long free = Runtime.getRuntime().freeMemory();
        return total-free;
    }
}
