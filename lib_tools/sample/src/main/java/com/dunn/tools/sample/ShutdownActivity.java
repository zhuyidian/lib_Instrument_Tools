package com.dunn.tools.sample;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.dunn.tools.log.LogUtil;
import com.dunn.tools.shutdown.ShutdownUtil;
import com.dunn.tools.time.Ability.AbilityFactory;
import com.dunn.tools.time.Ability.IAbility;
import com.dunn.tools.time.TimeTest;
import com.dunn.tools.time.TimeUtil;
import com.dunn.tools.time.bean.RemoteCommand;

import java.util.Calendar;
import java.util.Date;

public class ShutdownActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shutdown);
    }

    @Override
    protected void onResume() {
        super.onResume();

        ShutdownUtil.rebootForPowerManager(this.getApplicationContext());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
