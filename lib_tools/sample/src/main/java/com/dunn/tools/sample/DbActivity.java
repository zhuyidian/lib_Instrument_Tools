package com.dunn.tools.sample;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;

import com.dunn.tools.db.AppDataHelper;
import com.dunn.tools.db.AppDatabase;
import com.dunn.tools.log.LogUtil;
import com.dunn.tools.screen.ScreenUtils;

import java.util.List;
import java.util.Map;

public class DbActivity extends Activity implements View.OnClickListener {
    private AppDatabase dbUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_db);

        dbUtil = new AppDatabase(this);
        //数据库测试
        AppDataHelper.L().updateAppinfo(dbUtil, "packageName", "className",
                "label", 1, null);
    }

    @Override
    public void onClick(View v) {

    }

    private Drawable getDbBmp() {
        //HomeIOThread.execute(new Runnable() {
        //@Override
        //public void run() {
        //测试数据库
        List<Map<String, Object>> list = AppDataHelper.L().getAppinfo(dbUtil);
        for (Map<String, Object> map : list) {
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                if (entry.getKey().equals("blob")) {
                    Drawable able = new BitmapDrawable((Bitmap) entry.getValue());
                    return able;
                }
            }
        }
        //}
        //});
        return null;
    }
}
