package com.coocaa.remoteplatform.core.http;

import android.content.Context;

import com.coocaa.remoteplatform.core.BuildConfig;
import com.remoteplatform.commom.Constant;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @ClassName: HttpService
 * @Author: XuZeXiao
 * @CreateDate: 2021/5/13 11:12
 * @Description:
 */
public class HttpService {
    private Retrofit retrofit = null;
    private IHttpService httpService = null;
    private static HttpService instance = null;

    public static HttpService getInstance(Context context) {
        if (instance == null) {
            synchronized (HttpService.class) {
                if (instance == null) {
                    instance = new HttpService(context);
                }
            }
        }
        return instance;
    }

    public HttpService(Context context) {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(loggingInterceptor).build();
        retrofit = new Retrofit.Builder().client(client).addConverterFactory(GsonConverterFactory.create()).baseUrl(Constant.getHttpAddress(context)).build();
        httpService = retrofit.create(IHttpService.class);
    }

    public void reportPushResult(String msgId, String deviceId, String mac, int status, String result, String data, Callback<Response<HttpResponseData>> callback) {
        httpService.reportPushResult(new PushResultData(msgId, deviceId, mac, status, result, data)).enqueue(callback);
    }
}
