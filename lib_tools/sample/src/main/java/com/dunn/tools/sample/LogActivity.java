package com.dunn.tools.sample;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.dunn.tools.log.FileLogger;
import com.dunn.tools.log.LogUtil;
import com.dunn.tools.volume.VolumeUtil;

import java.io.File;

public class LogActivity extends AppCompatActivity{

    static {
        System.loadLibrary("native-log");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        File file = new File(Environment.getExternalStorageDirectory(), "test1.log");
        FileLogger fileLogger = new FileLogger(file, 20 * 1024 * 1024);
        long start = System.currentTimeMillis();
        for (int i=0;i<1000;i++){
            fileLogger.write("name:xxx0->");
        }
        long end = System.currentTimeMillis();
        LogUtil.i("log", "->"+(end - start));

        SharedPreferences sp = getSharedPreferences("name",MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        start = System.currentTimeMillis();
        for (int i=0;i<1000;i++){
            editor.putString("name","xxx0->");
            editor.commit();
        }
        end = System.currentTimeMillis();
        LogUtil.i("log", "->"+(end - start));
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
