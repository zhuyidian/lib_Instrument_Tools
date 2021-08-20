package com.dunn.tools.sample;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

import com.dunn.tools.ActivityManager;


public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManager.getInstance().attach(this);
        setContentView(R.layout.activity_main);
        setTitle("LoginActivity");
    }

    public void click(View view){
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        ActivityManager.getInstance().detach(this);
        super.onDestroy();
    }
}
