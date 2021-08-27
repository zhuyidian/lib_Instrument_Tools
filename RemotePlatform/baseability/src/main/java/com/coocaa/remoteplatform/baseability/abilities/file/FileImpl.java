package com.coocaa.remoteplatform.baseability.abilities.file;

import android.util.Log;

import com.coocaa.remoteplatform.baseability.abilities.AbsAbility;
import com.coocaa.remoteplatform.baseability.common.FileUtils;
import com.coocaa.remoteplatform.core.common.RemoteCommand;
import com.google.gson.JsonObject;
import com.remoteplatform.commom.Constant;
import com.skyworth.framework.utils.LogUtil;

import static com.coocaa.remoteplatform.baseability.common.FileUtils.getFileMd5;
import static com.remoteplatform.commom.Utils.valueFromManifest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * @ClassName: FileImpl
 * @Author: XuZeXiao
 * @CreateDate: 4/6/21 5:35 PM
 * @Description:
 */
public class FileImpl extends AbsAbility {

    public static final String TAG = FileImpl.class.getSimpleName();
    //上传文件到设备
    public static final int CMD_FILE_UPLOAD_TO_DEVICE = 5;
    //从设备下载文件
    public static final int CMD_FILE_DOWNLOAD_FROM_DEVICE = 12;

    private static RemoteCommand mCommand = null;
    private static String mUploadFilePath = null;

    @Override
    public void handleMessage(RemoteCommand command) {
        //指令类型---5:下载文件；6：下载升级配置文件
        mCommand = command;
        int cmdType = command.cmdType;
        String userId = command.userId;
        Log.e(TAG, "handleMessage----cmdType: " + cmdType);
        try {
            JSONObject obj = new JSONObject(command.content);
            int downType = obj.optInt("downType");
            String md5 = obj.optString("md5");
            String downloadUrl = obj.optString("url");
            String storageFilePath = obj.optString("storageFilePath");
            String storageFileName = obj.optString("storageFileName");

            String uploadFilePath = obj.optString("path");
            mUploadFilePath = uploadFilePath;

            String uploadAddress = Constant.getFileAddress(mContext);

            LogUtil.d(TAG, "handleMessage-----downType: " + downType);
            LogUtil.d(TAG, "handleMessage-----md5: " + md5);
            LogUtil.d(TAG, "handleMessage-----downloadUrl: " + downloadUrl);
            LogUtil.d(TAG, "handleMessage-----storageFilePath: " + storageFilePath);
            LogUtil.d(TAG, "handleMessage-----storageFileName: " + storageFileName);
            LogUtil.d(TAG, "handleMessage-----command.content: " + command.content);
            LogUtil.d(TAG, "handleMessage-----uploadFilePath: " + uploadFilePath);
            LogUtil.d(TAG, "handleMessage-----uploadAddress: " + uploadAddress);
            LogUtil.d(TAG, "handleMessage-----userId: " + userId);
            switch (cmdType) {
                case CMD_FILE_UPLOAD_TO_DEVICE:
                    upload(downloadUrl, storageFilePath, storageFileName, mDownloadCallback);
                    break;
                case CMD_FILE_DOWNLOAD_FROM_DEVICE:
                    handleFileDownloadFromDevice(uploadAddress, userId, uploadFilePath, cmdType, uploadFilePath, mUploadcallback);
                    break;
                default:
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void handleFileDownloadFromDevice(String uploadAddress, String userId, String uploadFilePath, int cmdType, String filePath, FileUtils.IFileUpLoadCallback mUploadcallback) {
        if (new File(uploadFilePath).exists()) {
            download(uploadAddress, userId, getFileMd5(uploadFilePath), cmdType, uploadFilePath, mUploadcallback);
        } else {
            Log.w(TAG, "handleFileDownloadFromDevice: file not found: " + uploadAddress + uploadFilePath);
        }
    }

    private FileUtils.IFileUpLoadCallback mUploadcallback = new FileUtils.IFileUpLoadCallback() {

        @Override
        public void onProgress() {
            LogUtil.d(TAG, "mUploadcallback...onProgress");
            if (mCommand != null) {
                mCommand.replyProcessing(mContext).reply();
            }
        }

        @Override
        public void onResult(boolean isSuccess, String msg) {
            LogUtil.d(TAG, "mUploadcallback...isSuccess" + isSuccess + " msg: " + msg);
            String md5 = getFileMd5(mUploadFilePath);
            String fileName = new File(mUploadFilePath).getName();
            JsonObject returnValue = new JsonObject();
            returnValue.addProperty("md5", md5);
            returnValue.addProperty("fileName", fileName);
            if (mCommand != null) {
                if (isSuccess) {
                    mCommand.replyFinish(mContext).withContent(returnValue.toString()).reply();
                } else {
                    mCommand.replyError(mContext).reply();
                }
            }
        }

    };

    private FileUtils.IFileDownloadCallback mDownloadCallback = new FileUtils.IFileDownloadCallback() {

        @Override
        public void onProgress(int process) {
            LogUtil.d(TAG, "onProgress...process: " + process);
            if (mCommand != null) {
                mCommand.replyProcessing(mContext).reply();
            }
        }

        @Override
        public void onResult(boolean isSuccess, String msg) {
            LogUtil.d(TAG, "IFileDownloadCallback..onResult isSuccess: " + isSuccess + " msg: " + msg);
            if (mCommand != null) {
                if (isSuccess) {
                    mCommand.replyFinish(mContext).reply();
                } else {
                    mCommand.replyError(mContext).reply();
                }
            }
        }
    };

    @Override
    public String getName() {
        return null;
    }

    /**
     * 从web服务器上传文件到设备
     *
     * @param url             服务器地址
     * @param storageFilePath web服务器上传到设备上的文件的存放地址
     * @param callback        回调
     */
    private void upload(String url, String storageFilePath, String storageFileName, FileUtils.IFileDownloadCallback callback) {
        if (!url.startsWith("http") || !url.startsWith("https")) {
            url = "http://" + url;
        }
        FileUtils.get().downloadFile(url, storageFilePath, storageFileName, callback);
    }

    /**
     * 从设备下载文件到web服务器
     *
     * @param url            服务器地址
     * @param sourceFilePath 文件在设备上的绝对路径
     * @param callback       回调
     */
    private void download(String url, String userId, String md5, int cmdType, String sourceFilePath, FileUtils.IFileUpLoadCallback callback) {

        FileUtils.get().uploadFile(url, userId, md5, cmdType, sourceFilePath, callback);
    }
}
