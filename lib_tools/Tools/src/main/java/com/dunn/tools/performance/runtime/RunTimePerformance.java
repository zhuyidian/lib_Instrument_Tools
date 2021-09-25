package com.dunn.tools.performance.runtime;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by dunn on 2018/6/28.
 */

public class RunTimePerformance implements TestTarget {
    private ArrayList array = null;
    private HashMap map = null;

    public RunTimePerformance(){

    }

    @Override
    public void createArray() {
        array = new ArrayList(1000);
    }

    @Override
    public void createHashMap() {
        map = new HashMap(1000);
    }
}
