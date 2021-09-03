package com.remoteplatform.commom;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import java.security.MessageDigest;
import java.util.Map;

import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.DisposableSubscriber;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;
import ua.naiksoftware.stomp.dto.LifecycleEvent;
import ua.naiksoftware.stomp.dto.StompMessage;

/**
 * @ClassName: Utils
 * @Author: XuZeXiao
 * @CreateDate: 4/7/21 3:54 PM
 * @Description:
 */
public class SocketUtils {
    public static final String PARAMS_ID = "id";
    public static final String PARAMS_ACTIVE_ID = "activeId";
    public static final String PARAMS_SOURCE = "source";
    public static final String PARAMS_DEVICE_ID = "deviceId";
    public static final String PARAMS_MAC = "mac";
    public static final String PARAMS_TOKEN = "token";
    public static final String[] TOKEN_KEYS = new String[]{PARAMS_ACTIVE_ID, PARAMS_DEVICE_ID, PARAMS_MAC};
    public static final String PARAMS_CONNECT_TYPE = "connectType";
    public static final String CONNECT_TYPE_TERMINAL_DEVICE = "1";


    public static String createSocketAddress(Context context, String base, Map<String, String> params) {
        Uri.Builder builder = Uri.parse(base).buildUpon();
        builder.appendPath("stompServer");
        builder.appendPath("websocket");
        for (Map.Entry<String, String> item : params.entrySet()) {
            builder.appendQueryParameter(item.getKey(), item.getValue());
        }
        builder.appendQueryParameter(PARAMS_TOKEN, createToken(params, Constant.getSocketSalt(context)).toLowerCase());
        return builder.build().toString();
    }


    public static String createToken(Map<String, String> params, String salt) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String tokenKey : TOKEN_KEYS) {
            stringBuilder.append(params.get(tokenKey));
        }
        stringBuilder.append(salt);
        return SocketUtils.md5s(stringBuilder.toString());
    }

    public static String md5s(String s) {
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        try {
            byte[] btInput = s.getBytes();
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
