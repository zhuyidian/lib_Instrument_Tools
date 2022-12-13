package com.dunn.instrument.tools.sample;

import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;


import com.dunn.instrument.tools.framework.system.SystemUtil;
import com.dunn.instrument.tools.log.LogUtil;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String version = SystemUtil.getSystemVersions();
        LogUtil.i(TAG,"onResume: version="+version);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
