package com.coocaa.remoteplatform.core.request;

import android.content.Context;
import android.text.TextUtils;

import com.tianci.user.api.SkyUserApi;

import java.util.HashMap;
import java.util.Map;

import swaiotos.sal.webservice.SALCommonHeader;

public class HomeHeader {

    private static SkyUserApi userApi;
    private static Map<String, String> headers;
    private static Context appContext;

    public static void init(Context context) {
        appContext = context;
    }

    public static synchronized Map<String, String> getHeader() {
        if (appContext == null)return new HashMap<>();
        if (userApi == null) {
            userApi = new SkyUserApi(appContext);
        }
        if (headers == null) {
            headers = initHeader(appContext);
        }
        return headers;
    }

    public static synchronized Map<String, String> getHeadersForTime(){
        if (appContext == null) return new HashMap<>();
        Map<String, String> headers = new HashMap<>(SALCommonHeader.getCommonHeaderMap(appContext));
        return headers;
    }

    private static Map<String, String> initHeader(Context context) {
        try {

            Map<String, String> headers = new HashMap<>(SALCommonHeader.getCommonHeaderMap(context, null,
                    new SALCommonHeader.SALHeaderValue(context) {
                        @Override
                        public Map<String, Object> getAccountInfo() {
                            return userApi.getAccoutInfo();
                        }
                    }));
//            if(CCLConnector.buildInfo != null && CCLConnector.buildInfo.isThirdPlatform()) {
//                compatHeader(headers);
//            }
            return headers;
        } catch (Exception e) {
            Map<String, String> headers = new HashMap<>();
            headers.put("11", "22");
            return headers;
        }
    }

    private static void compatHeader(Map<String, String> headers) {
        headers.put("cModel", "Q51");
        headers.put("cChip", "8H901");
        headers.put("cTcVersion", "800200925");
        headers.put("cUDID", "86035011");
        headers.put("source", "yinhe");
        if (TextUtils.isEmpty(headers.get("MAC")))
            headers.put("MAC", "001a9a8e7e6c");
    }
}
