package com.dunn.tools.Performance;

import com.dunn.tools.Performance.runtime.RunTimeHandler;
import com.dunn.tools.Performance.runtime.RunTimePerformance;
import com.dunn.tools.Performance.runtime.TestTarget;
import com.dunn.tools.Performance.time.TimeHandler;
import com.dunn.tools.Performance.time.TimePerformance;

public class PerformanceUtil {
    public void test(){
        //performanceanalysis测试
        try{
            com.dunn.tools.Performance.time.TestTarget testTarget = (com.dunn.tools.Performance.time.TestTarget) TimeHandler.newInstance(new TimePerformance());
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
