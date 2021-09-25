package com.dunn.tools.performance;

import com.dunn.tools.performance.runtime.RunTimeHandler;
import com.dunn.tools.performance.runtime.RunTimePerformance;
import com.dunn.tools.performance.runtime.TestTarget;
import com.dunn.tools.performance.time.TimeHandler;
import com.dunn.tools.performance.time.TimePerformance;

public class PerformanceUtil {
    public void test(){
        //performanceanalysis测试
        try{
            com.dunn.tools.performance.time.TestTarget testTarget = (com.dunn.tools.performance.time.TestTarget) TimeHandler.newInstance(new TimePerformance());
            testTarget.arrayList();
            testTarget.linkedList();
        }catch (Exception e){
            e.printStackTrace();
        }

        try{
            TestTarget testTarget = (TestTarget) RunTimeHandler.newInstance(new RunTimePerformance());
            testTarget.createArray();
            testTarget.createHashMap();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
