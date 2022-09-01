package com.dunn.instrument.tools.sample;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;


import com.dunn.instrument.tools.file.FileUtil;
import com.dunn.instrument.tools.reflect.ReflectUtil;
import com.dunn.instrument.tools.reflect.sohot.SoHotFix;
import com.dunn.instrument.tools.time.TimeUtil;

import java.io.File;
import java.io.IOException;

public class ReflectActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_reflect);
        reflectExec();
    }

    public void reflectExec(){
        Object[] args = new Object[1];
        args[0] = 1;
        ReflectUtil.exec(TimeUtil.class.getName(),"getDayHoursList",args);
    }

    public void soFix() {
        // 从服务器下载 so ，比对 so 的版本
        // 现在下好了，在我的手机里面 /so/libmain.so

        // 先调用 sdk 方法动态加载或者修复
        File mainSoPath = new File(Environment.getExternalStorageDirectory(),"so/libmain.so");

        //必须要拷贝文件到应用自己的目录，否则会报错
        File libSoPath = new File(getDir("lib", Context.MODE_PRIVATE),"so");
        if(!libSoPath.exists()){
            libSoPath.mkdirs();
        }
        File dst = new File(libSoPath,"libmain.so");
        try {
            FileUtil.copyFile(mainSoPath,dst);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            SoHotFix soHotFix = new SoHotFix(this);
            soHotFix.injectLoadPath(libSoPath.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
