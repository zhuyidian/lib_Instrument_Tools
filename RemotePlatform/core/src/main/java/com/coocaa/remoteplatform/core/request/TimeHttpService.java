package com.coocaa.remoteplatform.core.request;

import java.util.List;
import java.util.Map;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface TimeHttpService {
    @POST("login")
    @FormUrlEncoded
    HttpResult<String> getToken(@Field("appCode") String code, @Field("appSecret") String appSecret);

    @GET("getControlByDeviceId")
    HttpResult<List<TimeRequestInfo>> getTimeData(@Query("deviceId") String deviceId);
}
