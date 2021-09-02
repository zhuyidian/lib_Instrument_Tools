package com.dunn.tools.sample;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.dunn.tools.shutdown.ShutdownUtil;
import com.dunn.tools.threadsafety.ListUtil;

public class ThreadSafetyActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_threadsafety);
        findViewById(R.id.btn).setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        ListUtil.setDataForLock();
        ListUtil.getDataForLock();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn:
                ListUtil.setStop();
                break;
        }
    }
}
