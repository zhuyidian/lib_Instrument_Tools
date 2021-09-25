package com.dunn.tools.sample;

import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.dunn.tools.activitymanager.ActivityManager;

public class MainActivity extends AppCompatActivity {
    /*
     * ActivityManager使用：
       1，在activity的onCreate中  ActivityManager.getInstance().attach(this);
       2，在activity的onDestory中 ActivityManager.getInstance().detach(this);
       3，关闭自己 ActivityManager.getInstance().finish(this);
       4，关闭别人 ActivityManager.getInstance().finish(LoginActivity.class);
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManager.getInstance().attach(this);

        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onDestroy() {
        ActivityManager.getInstance().detach(this);
        super.onDestroy();
    }
}
