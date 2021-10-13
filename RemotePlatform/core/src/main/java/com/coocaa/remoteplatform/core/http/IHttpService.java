package com.coocaa.remoteplatform.core.http;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * @ClassName: IHttpService
 * @Author: XuZeXiao
 * @CreateDate: 2021/5/13 11:13
 * @Description:
 */
public interface IHttpService {
    @Headers("Content-Type: application/json")
    @POST("notice/status")
    Call<Response<HttpResponseData>> reportPushResult(@Body PushResultData body);
}
