package com.coocaa.remoteplatform.core.clientmanager;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * @ClassName: ClientManager
 * @Author: XuZeXiao
 * @CreateDate: 3/9/21 3:22 PM
 * @Description:
 */
public class ClientManager implements IClientManager {
    private static final String TAG = "ClientManager";
    private final HashMap<String, Client> mClients = new LinkedHashMap<>();
    private ClientFinder mClientFinder = null;
    private PackageStateReceiver mPackageReceiver = null;
    private Context mContext = null;

    public ClientManager(ClientFinder clientFinder, Context context) {
        mClientFinder = clientFinder;
        mContext = context;
        registerReceiver();
    }

    private void registerReceiver() {
        Log.i(TAG, "register PackageStateReceiver: ");
        mPackageReceiver = new PackageStateReceiver(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_REPLACED);
        intentFilter.addDataScheme("package");
        mContext.registerReceiver(mPackageReceiver, intentFilter);
    }

    @Override
    public void findClients() {
        Log.i(TAG, "findClients: ");
        synchronized (mClients) {
            mClients.clear();
            mClients.putAll(mClientFinder.findAll(mContext));
        }
    }

    @Override
    public Client getClient(String id) {
        synchronized (mClients) {
            return mClients.get(id);
        }
    }

    @Override
    public void destroy() {
        Log.i(TAG, "destroy: ");
        mContext.unregisterReceiver(mPackageReceiver);
    }
}
