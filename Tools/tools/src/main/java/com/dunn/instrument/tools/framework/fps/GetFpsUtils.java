package com.dunn.instrument.tools.framework.fps;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;

public class GetFpsUtils {
    //清空之前采样的数据，防止统计重复的时间
    private static String clearCommand = "dumpsys SurfaceFlinger --latency-clear";//""adb shell dumpsys SurfaceFlinger --latency-clear";
    private static long fpsVsncTime = 16670000; //16.67ms(单位纳秒)
    private static long betweenFrameExceptionInterval = 500000000; //500ms(单位纳秒)两帧异常时间

    private static int stuckSum;

    private static float lastSS;

    public static int getStuckSum2() {
        return stuckSum;
    }

    public static void clearFrameData(String packageName) {
        try {
            clearCommand = "dumpsys gfxinfo " + packageName + " reset";
            Runtime.getRuntime().exec(clearCommand);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static double[] getInfo(String packageName) {
        long start = System.currentTimeMillis();
        Log.i("GetFpsUtils", " start :" + start);
        String gfxCMD = "dumpsys gfxinfo " + packageName + " framestats";
//        Log.i("GetFpsUtils", " gfxCMD :" + gfxCMD);
        double[] info = new double[5];
        try {
            Process process = Runtime.getRuntime().exec(gfxCMD);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            boolean flag = false;
            long maxDurationFrameTime = 0;
            int totalFrames = 0;
            double lostFrameRate = 0;
            double fps = 0;
            int jumpingFrames = 0;
            long fpsRecordStartTime = 0;// 每秒帧率开始时间
            long fpsRecordEndTime = 0;//
            boolean isFisrtFrame = true;//第一帧
            float SS = 0;//流畅分
            long lastDurationFrameTime = 0;
            long lastFrameStartTime = 0;
            long invalidBetweenFrameInterval = 0;
//            int num = 0;
//            long totalTime = 0;
//            long avgTime = 0;
//            int tempStuckSum = 0;
            while ((line = reader.readLine()) != null) {
//                Log.i("GetFpsUtils", " line :" + line);
                if (line.length() > 0) {
                    if (line.contains("FrameCompleted")) {
                        flag = true;
                        continue;
                    }

                    if (line.contains("View hierarchy")) {
                        break;
                    }

                    if (flag) {
                        String[] times = line.trim().split(",");
                        String frameFlag = times[0];
                        if (!"0".equals(frameFlag)) {
                            continue;
                        }
                        String startFrameTime = times[1];
                        String completedFrameTime = times[13];
                        if (isFisrtFrame) {
                            fpsRecordStartTime = Long.parseLong(startFrameTime);
                            isFisrtFrame = false;
                        }
                        //计算一帧所花费的时间
                        long onceTime = Long.parseLong(completedFrameTime) - Long.parseLong(startFrameTime);
                        totalFrames += 1; //统计总帧数
                        Log.i("GetFpsUtils", " ,onceTime:" + onceTime + ",completedFrameTime:" + completedFrameTime + ",startFrameTime:" + startFrameTime + ",totalFrames:" + totalFrames);
                        if (onceTime > fpsVsncTime) {//以Android定义的60FPS为标准
                            jumpingFrames += 1; // 统计跳帧jank数
                        }

                        if (lastFrameStartTime != 0) {
                            long betweenFrameInterval = Long.parseLong(startFrameTime) - lastFrameStartTime;
                            if (betweenFrameInterval<0||betweenFrameInterval > betweenFrameExceptionInterval) {
                                continue;
                            }
                            if (betweenFrameInterval > fpsVsncTime * 3 && onceTime <= fpsVsncTime && lastDurationFrameTime <= fpsVsncTime) {
                                betweenFrameInterval = betweenFrameInterval - fpsVsncTime;
                                invalidBetweenFrameInterval += betweenFrameInterval;
                            }
                        }

                        if (onceTime - maxDurationFrameTime > 0) {//最大帧耗时
                            maxDurationFrameTime = Math.max(onceTime,fpsVsncTime);
                        }


                        //详细计算卡顿方法：和视觉感受会有争议（保留做对比）
//                        if (num < 3) {
//                            totalTime = totalTime + onceTime;
//                            num++;
//                        } else if (num == 3) {
//                            avgTime = totalTime / 3;
//                            num = 0;
//                            totalTime = 0;
//                        }
//                        Log.i("GetFpsUtils", " ，avgTime:" + avgTime + ",num:" + num + ",totalTime:" + totalTime);
//                        if (avgTime > 0) {
//                            if (onceTime > 2 * avgTime && onceTime > 100000000) {
//                                Log.i("GetFpsUtils---1", "卡了一次" + onceTime);
//                                tempStuckSum++;
//                            }
//                            avgTime = 0;
//                        }

                        fpsRecordEndTime = Long.parseLong(completedFrameTime);
                        lastDurationFrameTime = onceTime;
                        lastFrameStartTime = Long.parseLong(startFrameTime);


                    }
                }
            }
            reader.close();
            float duration = (fpsRecordEndTime - fpsRecordStartTime - invalidBetweenFrameInterval) / 1000000000.0F;
            Log.i("GetFpsUtils---1", " end :" + (System.currentTimeMillis() - start) + ",invalidBetweenFrameInterval:" + invalidBetweenFrameInterval + "---" + ((fpsRecordEndTime - fpsRecordStartTime)) + "---" + duration + " ,maxDurationFrameTime:" + maxDurationFrameTime + ",totalFrames:" + totalFrames);
            if (totalFrames > 0) {
                fps = Math.min(Math.round(totalFrames / duration),60);
                lostFrameRate = toFloat(jumpingFrames, totalFrames);
                info[0] = fps;
                info[1] = jumpingFrames;//Double.valueOf(lostFrameRate);//float) Commons.streamDouble(lostFrameRate * 100);
                SS = (float) (fps / 60.0 * 60 + toFloat(fpsVsncTime, maxDurationFrameTime) * 20 + (1 - lostFrameRate) * 20);
                info[2] = SS;
                info[3] = totalFrames;
                info[4] = maxDurationFrameTime;
                //对比用
                if (fps > 0&&lastSS > 0) {
                    float tempSS = (float) (lastSS - (lastSS * 0.5));
//                        if (tempStuckSum > 1 ) {
//                            stuckSum2++;
//                        }
                    if ((SS < tempSS || (lastSS < 24 && SS < 24)) && maxDurationFrameTime > 80000000) {
                        stuckSum++;
                    }
                }
                lastSS = SS;
                Log.i("GetFpsUtils---1", " fps :" + fps + ",stuckSum:" + stuckSum  + " ,jumpingFrames:" + jumpingFrames + ",lostFrameRate:" + lostFrameRate + " ,SS:" + SS);
            } else {
                Log.i("GetFpsUtils---1","【ERROR】无FPS信息");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        clearFrameData(packageName);
        return info;
    }

    public static Double toFloat(long numerator, long denominator) {
        DecimalFormat df = new DecimalFormat("0.00");//设置保留位数
        return Double.valueOf(df.format((float) numerator / denominator));
    }
}
