package com.dunn.tools.Performance.time;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by dunn on 2018/6/28.
 */

public class TimePerformance implements TestTarget {
    private List array = new ArrayList<>();
    private List link = new LinkedList<>();

    public TimePerformance(){
        for(int i=0;i<10000;i++){
            array.add(new Integer(i));
            link.add(new Integer(i));
        }
    }

    @Override
    public void arrayList() {
        for(int i=0;i<10000;i++){
            array.get(i);
        }
    }

    @Override
    public void linkedList() {
        for(int i=0;i<10000;i++){
            link.get(i);
        }
    }
}
