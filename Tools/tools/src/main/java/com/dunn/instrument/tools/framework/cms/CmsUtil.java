package com.dunn.instrument.tools.framework.cms;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

/**
 * Author:Administrator
 * Date:2021/10/27 14:41
 * Description:CmsUtil
 */
public class CmsUtil {
    public static final int NET_ETHERNET = 1;
    public static final int NET_WIFI = 2;
    public static final int NET_NOCONNECT = 0;

    public static boolean isNetConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if (info != null && info.isAvailable() && info.isConnected()) {
            return true;
        } else
            return false;
    }

    public static int isNetworkAvailable(Context context) {
        return isNetworkAvailable((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
    }

    public static int isNetworkAvailable(ConnectivityManager connectivityManager) {
        if(connectivityManager == null)
            return NET_NOCONNECT;
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if (info != null && info.isAvailable() && info.isConnected()) {
            int type = info.getType(); // wifi==1
            if (type == 1) {
                return NET_WIFI;
            } else {
                return NET_ETHERNET;
            }
        }
        return NET_NOCONNECT;
    }
}
