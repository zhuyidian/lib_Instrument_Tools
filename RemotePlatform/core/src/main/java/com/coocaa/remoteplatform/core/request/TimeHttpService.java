package com.coocaa.remoteplatform.core.request;

import com.coocaa.remoteplatform.core.time.bean.request.TimeRequestDataInfo;
import com.coocaa.remoteplatform.core.time.bean.request.TimeRequestInfo;

import java.util.List;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface TimeHttpService {
    @POST("login")
    @FormUrlEncoded
    HttpResult<String> getToken(@Field("appCode") String code, @Field("appSecret") String appSecret);

    @GET("getControlByDeviceId")
    HttpResult<List<TimeRequestInfo>> getTimeData(@Query("deviceId") String deviceId);

    @GET("getControlInfoByDeviceId")
    HttpResult<List<TimeRequestDataInfo>> getTimeDataInfo(@Query("deviceId") String deviceId);
}
