package com.dunn.tools.sample;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.dunn.tools.log.LogUtil;
import com.dunn.tools.time.Ability.AbilityFactory;
import com.dunn.tools.time.Ability.IAbility;
import com.dunn.tools.time.TimeManager;
import com.dunn.tools.time.TimeTest;
import com.dunn.tools.time.TimeUtil;
import com.dunn.tools.time.bean.RemoteCommand;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

public class TimeActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView cmdTypeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cmdTypeView = findViewById(R.id.tv);
        findViewById(R.id.btn0).setOnClickListener(this);
        findViewById(R.id.btn1).setOnClickListener(this);
        findViewById(R.id.btn2).setOnClickListener(this);
        findViewById(R.id.btn3).setOnClickListener(this);
        findViewById(R.id.btn4).setOnClickListener(this);
        cmdType = 0;
        cmdTypeView.setText("关机");

        TimeUtil.getN_day_Pamams(this, 2, "test");
        Date mDate = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(mDate);
        TimeUtil.getHourBAPamams(this, cal.getTimeInMillis(), 3, "test");
        TimeUtil.getHourBPamams(this, cal.getTimeInMillis(), 3, "test");

        //TimeUtil.setRtc(TimeActivity.this);

        //test
        convertVolume(60,100);
    }

    private int convertVolume(int targetVolume, int maxValue) {
        LogUtil.i("time", "########### targetVolume=" + targetVolume+", maxValue="+maxValue);
        int target = (int) ((float)targetVolume / 100 * maxValue);
        LogUtil.i("time", "########### target=" + target);
        if (target > maxValue) target = maxValue;
        if (target < 0) target = 0;
        return target;
    }

    int cmdType = 0;
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn0:   //指令类型选择
                if(cmdType==0){
                    cmdType = 2;
                    cmdTypeView.setText("重启");
                }else if(cmdType==2){
                    cmdType = 3;
                    cmdTypeView.setText("音量");
                }else{
                    cmdType = 0;
                    cmdTypeView.setText("关机");
                }
                break;
            case R.id.btn1:  //一次性执行
                if(cmdType==0){  //定时关机
                    sendCommand(this, cmdType, TimeTest.jsonDelay_shutDown);
                }else if(cmdType==2) {  //定时重启
                    sendCommand(this, cmdType, TimeTest.jsonDelay_reboot);
                }else if(cmdType==3) {  //定时音量
                    sendCommand(this, cmdType, TimeTest.jsonDelay_volume);
                }
                break;
            case R.id.btn2:  //每天执行
                if(cmdType==0){  //定时关机
                    sendCommand(this, cmdType, TimeTest.jsonDay_shutDown);
                }else if(cmdType==2) {  //定时重启
                    sendCommand(this, cmdType, TimeTest.jsonDay_reboot);
                }else if(cmdType==3) {  //定时音量
                    sendCommand(this, cmdType, TimeTest.jsonDay_volume);
                }
                break;
            case R.id.btn3:  //每周执行
                if(cmdType==0){  //定时关机
                    sendCommand(this, cmdType, TimeTest.jsonWeek_shutDown);
                }else if(cmdType==2) {  //定时重启
                    sendCommand(this, cmdType, TimeTest.jsonWeek_reboot);
                }else if(cmdType==3) {  //定时音量
                    sendCommand(this, cmdType, TimeTest.jsonWeek_volume);
                }
                break;
            case R.id.btn4:  //每月执行
                if(cmdType==0){  //定时关机
                    sendCommand(this, cmdType, TimeTest.jsonMonth_shutDown);
                }else if(cmdType==2) {  //定时重启
                    sendCommand(this, cmdType, TimeTest.jsonMonth_reboot);
                }else if(cmdType==3) {  //定时音量
                    sendCommand(this, cmdType, TimeTest.jsonMonth_volume);
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void sendCommand(Context mContext, int cmdType, String json) {
        RemoteCommand command = null;
        try {
            command = new RemoteCommand();
            command.content = json;
            command.timestamp = System.currentTimeMillis();
            command.clientId = "com.coocaa.remoteplatform.baseability";
            command.cmdType = cmdType;
            command.msgOrigin = "push";
            if (command != null) {
                IAbility ability = AbilityFactory.getAbility(mContext, command.cmdType);
                if (ability == null) {
                    LogUtil.i("time", "no ability match command: " + command.toString());
                    return;
                }
                ability.handleMessage(command);
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.i("time", "e=" + e);
        }
    }
}
