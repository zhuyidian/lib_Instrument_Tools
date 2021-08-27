package com.coocaa.remoteplatform.core.clientmanager;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import com.coocaa.remoteplatform.core.common.Constant;

import java.util.HashMap;
import java.util.List;


/**
 * @ClassName: SDKFinder
 * @Author: XuZeXiao
 * @CreateDate: 3/9/21 3:20 PM
 * @Description:
 */
public class ClientFinder {
    private static final String TAG = "ClientFinder";

    public HashMap<String, Client> findAll(Context context) {
        HashMap<String, Client> result = new HashMap<>();
        Intent i = new Intent(Constant.CLIENT_SERVICE_ACTION);
        List<ResolveInfo> serviceInfo = context.getPackageManager().queryIntentServices(i, PackageManager.GET_META_DATA);
        if (serviceInfo == null || serviceInfo.isEmpty()) {
            Log.w(TAG, "findAll: remote client not found");
            return result;
        }
        for (ResolveInfo resolveInfo : serviceInfo) {
            Client client = getClientFromInfo(resolveInfo);
            if (client == null) {
                continue;
            }
            Log.i(TAG, "findAll: find: " + client.toString());
            result.put(client.id, client);
        }
        return result;
    }

    private Client getClientFromInfo(ResolveInfo resolveInfo) {
        if (resolveInfo == null) {
            return null;
        }
        ServiceInfo service = resolveInfo.serviceInfo;
        if (service == null) {
            return null;
        }
        String id = "";
        int version = 0;

        if (service.metaData != null) {
            id = service.metaData.getString("id", "");
            version = service.metaData.getInt("version", 0);
        }
        if (TextUtils.isEmpty(id)) {
            Log.i(TAG, "findAll: id not define in meta data: " + service.name);
            return null;
        }
        return new Client(id, service.packageName, service.name, version);
    }
}
