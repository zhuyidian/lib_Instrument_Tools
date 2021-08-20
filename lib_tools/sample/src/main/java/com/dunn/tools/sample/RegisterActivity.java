package com.dunn.tools.sample;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.dunn.tools.ActivityManager;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManager.getInstance().attach(this);
        setContentView(R.layout.activity_main);
        setTitle("RegisterActivity");
    }

    public void click(View view){
        // 不光要关闭自己还要关闭 LoginActivity 让其回到主页
        ActivityManager.getInstance().finish(this);
        ActivityManager.getInstance().finish(LoginActivity.class);
    }

    @Override
    public void onDestroy() {
        ActivityManager.getInstance().detach(this);
        super.onDestroy();
    }
}
