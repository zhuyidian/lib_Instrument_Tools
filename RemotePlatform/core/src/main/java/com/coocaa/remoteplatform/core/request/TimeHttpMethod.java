package com.coocaa.remoteplatform.core.request;

import android.content.Context;
import android.util.Log;

import com.coocaa.remoteplatform.core.common.RemoteCommand;
import com.coocaa.remoteplatform.core.connection.Utils;
import com.coocaa.remoteplatform.core.connection.push.PushCommand;
import com.coocaa.remoteplatform.core.service.Main;
import com.remoteplatform.commom.thread.HomeIOThread;

import java.util.List;
import java.util.Map;

import static com.remoteplatform.commom.Constant.COMMAND_SOURCE_PUSH;

public class TimeHttpMethod extends HttpMethod<TimeHttpService> {
    private static final String WEATHER_RELEASE_URL_TEST = "http://172.20.151.183:5002/weather/";
    private static final String WEATHER_RELEASE_URL = "http://tq.skysrt.com/weather/";
    private static final String TIME_RELEASE_URL = "https://fusionmedia.skysrt.com/ccdevice/control/";
    private static final String TIME_RELEASE_URL_TEST = "https://beta-fusionmedia.skysrt.com/ccdevice/control/";
    public static final String APP_CODE = "639bae9ac6b3e1a84cebb7b403297b79";
    public static final String SALT = "99754106633f94d350db34d548d6091a";
    private Context mContext;

    public TimeHttpMethod(Context context) {
        mContext = context;
    }

    /**
     * 单独请求自己放在子线程
     *
     * @return
     */
    public String getToken() {
        HttpResult<String> mTokenRet = null;
        try {
            mTokenRet = getService().getToken(APP_CODE, SALT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map(mTokenRet);
    }

    public void getWeatherData(final Map<String, String> map, final HttpCallback<WeatherInfo> callback) {
        HomeIOThread.execute(new Runnable() {
            @Override
            public void run() {
                String mToken = "Bearer " + getToken();
                HttpResult<WeatherInfo> mWeatherData = null;
                try {
                    mWeatherData = getService().getWeatherData(mToken,map);
                } catch (Exception e) {
                    callback.error(e);
                    e.printStackTrace();
                }
                WeatherInfo data = map(mWeatherData);
                callback.callback(data);
            }
        });
    }

    public void getTimeData(final Context context, final String deviceId) {
        Log.v("time-api","get time data deviceId="+deviceId);
        HomeIOThread.execute(new Runnable() {
            @Override
            public void run() {
                HttpResult<List<TimeRequestInfo>> mTimeData = null;
                try {
                    mTimeData = getService().getTimeData(deviceId);
                } catch (Exception e) {
                    Log.v("time-api","get time data e="+e);
                    e.printStackTrace();
                }
                List<TimeRequestInfo> timeInfo = map(mTimeData);
                Log.v("time-api","code="+mTimeData.code+", msg="+mTimeData.msg);
                Log.v("time-api","timeInfo.size="+timeInfo.size());
                for(TimeRequestInfo info : timeInfo){
                    String json = TimeRequestAnalysis.parseContent(info);
                    Log.v("time-api","json="+json);
                    json = "{\"planType\":0,\"switch\":0,\"time\":[\"21:50:30\"],\"volume\":50}";

                    RemoteCommand command = null;
                    try {
                        //String jsonContent = "{\"content\":" + json +"}";
                        //Log.v("time-api","jsonContent="+jsonContent);
                        //jsonContent = "{\"content\":{\"planType\":2,\"switch\":3,\"time\":[\"21:50:30\"],\"volume\":50}}";
                        command = new RemoteCommand();
                        command.content = json;
                        command.timestamp = System.currentTimeMillis();
                        command.clientId = "com.coocaa.remoteplatform.baseability";
                        command.cmdType = TimeRequestAnalysis.convertSwtich(/*info.command*/0);
                        command.msgOrigin = COMMAND_SOURCE_PUSH;
                        if(command!=null){
                            Main.getInstance(context).getPushChannel().onReceiveMessage(command);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public String getBaseUrl() {
        return TIME_RELEASE_URL_TEST;
    }

    @Override
    public int getTimeOut() {
        return 10;
    }

    @Override
    public Class getServiceClazz() {
        return TimeHttpService.class;
    }

    @Override
    public Map<String, String> getHeaders() {
        Log.v("time-api","header="+HomeHeader.getHeadersForTime());
        return HomeHeader.getHeadersForTime();
    }

}
