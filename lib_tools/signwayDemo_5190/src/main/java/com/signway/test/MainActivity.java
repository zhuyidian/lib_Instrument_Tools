package com.signway.test;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.util.Log;
import android.content.res.Resources;
import android.os.signwaymanager.SignwayManager;
import android.widget.Toast;

public class MainActivity extends Activity {
	Button testBtn;
    SignwayManager mSignwayManager;
    private String[] arrayItem;
    Resources res;
    private boolean ishide = false;
    //int mode = 2;
    int ortation = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        res = getBaseContext().getResources();
        arrayItem = res.getStringArray(R.array.dpi_items);
        //mSignwayManager = (SignwayManager) getBaseContext().getSystemService(Context.SIGNWAY_SERVICE);
        mSignwayManager = SignwayManager.getInstance(this);
    }

    public void onTestClick(View view) {
        try {
            switch (view.getId()) {
                case R.id.hide_bar:
                    ishide = !ishide;
                    mSignwayManager.hideStatusBar(ishide);
                    break;
                case R.id.api_version:
                    String version = mSignwayManager.getApiVersion();
                    Log.i("SignWay", "version = " + version);
                    break;
                case R.id.android_model:
                    String modle = mSignwayManager.getAndroidModel();
                    Log.i("SignWay", "modle = " + modle);
                    break;
                case R.id.android_version:
                    String android = mSignwayManager.getAndroidVersion();
                    Log.i("SignWay", "android = " + android);
                    break;
                case R.id.firmware_version:
                    String firmware = mSignwayManager.getFirmwareVersion();
                    Log.i("SignWay", "firmware = " + firmware);
                    break;
                case R.id.kernel_version:
                    String kernel = mSignwayManager.getKernelVersion();
                    Log.i("SignWay", "kernel = " + kernel);
                    break;
                case R.id.firmware_date:
                    String date = mSignwayManager.getFirmwareDate();
                    Log.i("SignWay", "date = " + date);
                    break;
                case R.id.android_display:
                    String display = mSignwayManager.getAndroidDisplay();
                    Log.i("SignWay", "display = " + display);
                    break;
                case R.id.running_size:
                    String runSize = mSignwayManager.getRunningMemory();
                    Log.i("SignWay", "runSize = " + runSize);
                    break;
                case R.id.storage_size:

                    String interSize = mSignwayManager.getInternalStorageMemory();
                    Log.i("SignWay", "interSize = " + interSize);
                    break;
                case R.id.screen_dpi:
                    String screendpi = mSignwayManager.getScreenDisplay();
//                String thedps = arrayItem[screendpi];
                    Log.i("SignWay", "screendpi === " + screendpi);
//				Toast.makeText(MainActivity.this, "暂时无法获取", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.set_screen_dpi:
                    mSignwayManager.setScreenDisplay("1920x1080@60");
//                Toast.makeText(MainActivity.this, "暂时无法获取", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.off_hdmi:
                    mSignwayManager.turnOffHDMI();
                    break;
                case R.id.on_hdmi:
                    mSignwayManager.turnOnHDMI();
                    break;
                case R.id.off_backlight:
                    mSignwayManager.turnOffLvds();
                    break;
                case R.id.on_backlight:
                    mSignwayManager.turnOnLvds();
                    break;
                case R.id.shot_screen:
                    mSignwayManager.takeScreenshot("/data/data/test.png", 0);
                    break;
                case R.id.shut_down:
                    mSignwayManager.shutDown();
                    break;
                case R.id.reboot:
                    mSignwayManager.reboot();
                    break;
                case R.id.set_rotation:
                    ortation = ortation % 4;
                    mSignwayManager.setRotationScreen(180 * ortation);
                    //Toast.makeText(MainActivity.this, "后重启生效", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.get_rotation:
                    int rotation = mSignwayManager.getRotationScreen();
                    Log.i("SignWay", "rotation = " + rotation);
                    //Toast.makeText(MainActivity.this, "后重启生效", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.root:
                    mSignwayManager.execSuCmd("reboot");
                    break;
                case R.id.usb_path:
                    String usbPath = mSignwayManager.getUSBStoragePath(0);
                    Log.i("SignWay", "usbPath = " + usbPath);
                    break;
                case R.id.uart_path:
                    String uartPath = mSignwayManager.getUartPath("uart1");
                    Log.i("SignWay", "ttyACM0 = " + uartPath);
                    uartPath = mSignwayManager.getUartPath("uart3");
                    Log.i("SignWay", "ttyACM3 = " + uartPath);
//            	Toast.makeText(MainActivity.this, "暂时无法获取", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.sdcard_path:
                    String sdcardPath = mSignwayManager.getSDcardPath();
                    Log.i("SignWay", "sdcardPath = " + sdcardPath);
                    break;
                case R.id.install_apk:
                    mSignwayManager.silentInstallApk("/mnt/sdcard/demo.apk", "com.test");
                    //Log.i("SignWay", "silent = " + silent);
                    break;
                case R.id.uninstall_apk://静默卸载apk
                    mSignwayManager.silentUninstallApk("com.bjw.ComAssistant");
                    break;
                case R.id.killbackgroundapk://静默卸载apk
                    mSignwayManager.killbackgroundapk("com.bjw.ComAssistant");
                    break;
                case R.id.upgrade_system:
                    mSignwayManager.upgradeSystem("/mnt/sdcard");
//            	mSignwayManager.upgradeSystem("/data/media/0");
                    break;
                case R.id.getcurrentnettype:
                    int netType = mSignwayManager.getCurrentNetType();
                    Log.i("SignWay", "netType = " + netType);
                    break;
                case R.id.getCurrentNetDBM://获取当前网络信号强度
                    int dbm = 0;
                    dbm = mSignwayManager.getCurrentNetDBM(0);//参数：0：获取wifi信号强度   1：获取4G信号强度
                    Log.i("SignWay", "dbm ==== " + dbm);
                    break;
                case R.id.get_eth_mac:
                    String ethMac = mSignwayManager.getEthMacAddress();
                    Log.i("SignWay", "ethMac = " + ethMac);
                    break;
                case R.id.get_eth_mode:
                    String ethMode = mSignwayManager.getEthMode();
                    Log.i("SignWay", "ethMode = " + ethMode);
                    break;
                case R.id.set_eth_static:
                    String iPaddr = "192.168.0.121";
                    String gateWay = "192.168.0.1";
                    String mask = "255.255.255.0";
                    String dns1 = "8.8.8.8";
                    String dns2 = "0.0.0.0";
                    mSignwayManager.setStaticEthIPAddress(iPaddr, gateWay, mask, dns1, dns2);
                    break;
//            case R.id.get_eth_static_ip:
//                String staticIp = mSignwayManager.getStaticEthIPAddress();
//                Log.i("SignWay", "staticIp = " + staticIp);
//                break;
                case R.id.get_eth_dhcp_ip:
                    String dhcpIp = mSignwayManager.getEthIpAddress();
                    Log.i("SignWay", "dhcpIp = " + dhcpIp);
                    break;
                case R.id.get_system_time:
                    int[] systemTiem = new int[10];
                    systemTiem = mSignwayManager.getSystemTime();
                    for (int i = 0; i < 5; i++) {
                        Log.i("SignWay", "systemTiem[" + i + "] = " + systemTiem[i]);
                    }
                    break;
                case R.id.set_system_time:
                    mSignwayManager.setTime(2019, 1, 1, 8, 5);
                    break;
                case R.id.set_power_on_time:
                    for (int i = 1; i < 6; i++) {
                        mSignwayManager.setPowerOnTime(i, 2018, 1, 1, 7 + i - 1, 5);
                    }
                    break;
                case R.id.set_power_off_time:
                    for (int i = 1; i < 6; i++) {
                        mSignwayManager.setPowerOffTime(i, 2018, 1, 1, 7 + i - 1, 0);
                    }
                    break;
                case R.id.get_power_on_time:
                    String[] powerOnTime = new String[5];
                    powerOnTime = mSignwayManager.getPowerOnTime();
                    for (int i = 0; i < 5; i++) {
                        Log.i("SignWay", "powerOnTime[" + i + "] = " + powerOnTime[i]);
                    }
                    break;
                case R.id.get_power_off_time:
                    String[] powerOffTime = new String[5];
                    powerOnTime = mSignwayManager.getPowerOffTime();
                    for (int i = 0; i < 5; i++) {
                        Log.i("SignWay", "powerOnTime[" + i + "] = " + powerOnTime[i]);
                    }
                    break;
//            case R.id.video_switch:
//                int mode = 0;
//                Log.i("SignWay", "mode = " + mode);
//                mSignwayManager.videoSwitch(mode);
//                Toast.makeText(MainActivity.this, "暂时无法获取", Toast.LENGTH_SHORT).show();
//                break;
//            case R.id.get_hdmi_status:
//                int status = mSignwayManager.getHdmiinStatus();
//                for(int i = 1;i < 6;i++) {
//                    mSignwayManager.setPowerOnTime(i, 0, 0, 0, 0, 0);
//                    mSignwayManager.setPowerOffTime(i, 0, 0, 0, 0, 0);
//                }
//                Log.i("SignWay", "Hdmi status  = " + status);
//                Toast.makeText(MainActivity.this, "暂时无法获取", Toast.LENGTH_SHORT).show();
//                break;
//            case R.id.get_screen_number:
//                int num = mSignwayManager.getScreenNumber();
//                Log.i("SignWay", "Screen num  = " + num);
//            	Toast.makeText(MainActivity.this, "暂时无法获取", Toast.LENGTH_SHORT).show();
//                break;
                case R.id.setDeepStandby:
                    mSignwayManager.setDeepStandby();
//                Toast.makeText(MainActivity.this, "暂时无法获取", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.setDeepStandbyWakeTime:
                    int time = 5;
                    mSignwayManager.setDeepStandbyWakeTime(time * 60);
//				Toast.makeText(MainActivity.this, "暂时无法获取", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.reqMcuVersion:
                    String McuVer = mSignwayManager.reqMcuVersion();
                    Log.i("SignWay", "McuVersion  = " + McuVer);
//				Toast.makeText(MainActivity.this, "暂时无法获取", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.setMcuWatchDog:
                    boolean enable = false;
                    mSignwayManager.setMcuWatchDog(enable);
//				Toast.makeText(MainActivity.this, "暂时无法获取", Toast.LENGTH_SHORT).show();
                    break;

            }
        }catch (Exception e){
            e.printStackTrace();
            Log.i("SignWay", "e= " + e);
        }
    }

    public void sleep(int time) {
        // new Thread(new Runnable() {
        //   @Override
        // public void run() {
        try {
            Thread.sleep(time * 1000);
        } catch (InterruptedException e){
            e.printStackTrace();
        }
        //}
        //}).start();
    }
}
