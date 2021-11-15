package com.dunn.tools.framework.resource;

import android.content.Context;
import android.content.res.AssetManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Author:Administrator
 * Date:2021/10/27 14:41
 * Description:CmsUtil
 */
public class ResourceUtil {
    public static Uri getResourceURI(Context context, int resID) {
        String uri = new StringBuilder("res://").append(context.getPackageName()).append("/").append(resID).toString();
        return Uri.parse(uri);
    }

    public static Uri getAndroidResUri(Context context, int resID) {
        String uri = new StringBuilder("android.resource://").append(context.getPackageName()).append("/").append(resID).toString();
        return Uri.parse(uri);
    }

    public static String getFrescoResourceUrl(Context context, int resId) {
        return new StringBuilder("res://").append(context.getPackageName()).append("/").append(resId).toString().toString();
    }

    public static String readAssetFile(Context context, String assetFile) {
        StringBuilder sb = new StringBuilder();
        AssetManager assetManager = context.getAssets();
        try {
            InputStream is = assetManager.open(assetFile, AssetManager.ACCESS_BUFFER);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public static String readRawText(Context context, int resId) {
        StringBuilder message = new StringBuilder();
        BufferedReader bufReader = null;
        InputStreamReader isr = null;
        try {
            InputStream is = context.getResources().openRawResource(resId);
            isr = new InputStreamReader(is);
            bufReader = new BufferedReader(isr);

            String line = null;
            while ((line = bufReader.readLine()) != null) {
                message.append(line.replace("\\n", "\n"));
            }
            bufReader.close();
            isr.close();
            is.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();

        }
        return message.toString();
    }
}
