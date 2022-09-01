package com.dunn.instrument.tools.db;

import android.graphics.Bitmap;

import com.dunn.instrument.tools.log.LogUtil;

import java.util.List;
import java.util.Map;

public class AppDataHelper {
    private static final String TAG = "AppInfoManager$";

    private static class Holder {
        private static AppDataHelper helper = new AppDataHelper();
    }

    public static AppDataHelper L() {
        return Holder.helper;
    }


    public void updateAppinfo(AppDatabase dbUtil, String packageName, String className, String label, int sort, Bitmap bmp) {
        LogUtil.i("db", TAG + "updateAppinfo:----db----packageName=" + packageName + ", className=" + className + ", label=" + label + ", sort=" + sort + ", bmp=" + bmp);
        dbUtil.open();
        dbUtil.setData(packageName, className, label, sort, bmp);
        dbUtil.close();
    }

    public List<Map<String, Object>> getAppinfo(AppDatabase dbUtil) {
        dbUtil.open();
        List<Map<String, Object>> list = dbUtil.getData();
        for (Map<String, Object> map : list) {
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                LogUtil.i("db", TAG + "getAppinfo:----db----key=" + entry.getKey() + ", value=" + entry.getValue());
            }
        }
        dbUtil.close();

        return list;
    }
}
