package com.coocaa.remoteplatform.baseability.common;

import android.os.Environment;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.coocaa.remoteplatform.sdk.RemotePlatform;
import com.skyworth.framework.utils.LogUtil;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * @ClassName: FileUtils
 * @Author: XuZeXiao
 * @CreateDate: 4/6/21 4:16 PM
 * @Description:
 */
public class FileUtils {

    private static final String TAG = FileUtils.class.getSimpleName();
    private static FileUtils mFileUtils = null;
    private final OkHttpClient mOkHttpClient;

    public static FileUtils get() {
        if (mFileUtils == null) {
            synchronized (FileUtils.class) {
                if (mFileUtils == null) {
                    mFileUtils = new FileUtils();
                }
            }
        }
        return mFileUtils;
    }

    public FileUtils() {
        this.mOkHttpClient = new OkHttpClient();
    }

    public interface IFileUpLoadCallback {
        void onProgress();

        void onResult(boolean isSuccess, String msg);
    }

    public interface IFileDownloadCallback {
        void onProgress(int process);

        void onResult(boolean isSuccess, String msg);
    }

    /**
     * 文件上传
     *
     * @param serverAddress 服务器地址
     * @param filePath      本地存储路径
     * @param callback
     * @param <T>
     */
    public <T> void uploadFile(String serverAddress, String filePath, IFileUpLoadCallback callback) {
        this.uploadFile(serverAddress, null, null, -10000, filePath, callback);
    }

    /**
     * 上传文件,从设备端传送文件到远端
     *
     * @param actionUrl 接口地址
     * @param filePath  本地文件地址
     */
    public <T> void uploadFile(String actionUrl, String userId, String md5, int cmdType, String filePath, final IFileUpLoadCallback callBack) {
        Log.d(TAG, "uploadFile...actionUrl: " + actionUrl + " userId: " + userId + " md5: " + md5 + " cmdType: " + cmdType + " filePath: " + filePath + " call: " + callBack);
        //补全请求地址
        String requestUrl = actionUrl;
        //创建File
        File file = new File(filePath);
        if (file.exists()) {
            if (file.length() == 0) {
                try {
                    throw new Exception(file.getAbsolutePath() + "大小为0，无法上传");
                } catch (Exception e) {

                }
            }
        } else {
            try {
                throw new FileNotFoundException(file.getAbsolutePath() + ",设备不存在该文件，无法上传");
            } catch (Exception e) {

            }
        }

        //创建RequestBody
        String activeId = RemotePlatform.getInstance().getAttachInfo().getActiveId();
        String deviceId = RemotePlatform.getInstance().getAttachInfo().getDeviceId();

        final MediaType mediaType = MediaType.parse("application/octet-stream");
        final RequestBody fileBody = RequestBody.create(mediaType, file);
        RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart("file", file.getName(), fileBody).addFormDataPart("deviceId", deviceId).addFormDataPart("activeId", activeId).addFormDataPart("userId", userId).addFormDataPart("md5", md5).addFormDataPart("cmdType", cmdType + "").build();
        //创建Request
        final Request request = new Request.Builder().url(requestUrl).post(body).build();
        if (mOkHttpClient == null) {
            try {
                throw new Exception("okhttp not init,try FileUtils.get().upLoadFile");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        final Call call = mOkHttpClient.newBuilder().writeTimeout(50, TimeUnit.SECONDS).build().newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "onFailure: " + e.toString());
                callBack.onResult(false, e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String msg = response.body().string();
                Log.e(TAG, "response ----->" + msg);
                if (response.isSuccessful()) {
                    callBack.onResult(true, msg);
                } else {
                    callBack.onResult(false, response.body().string());
                }
            }
        });
    }


    /**
     * 下载文件，从远端下载文件到设备端
     *
     * @param downloadUrl      下载连接
     * @param savePath         储存下载文件的SDCard目录
     * @param fileName         文件名
     * @param downloadCallback 下载监听
     */
    public void downloadFile(String downloadUrl, final String savePath, final String fileName, final IFileDownloadCallback downloadCallback) {
        LogUtil.d(TAG, "download...downloadUrl: " + downloadUrl + " savePath: " + savePath + " fileName: " + fileName + " downloadCallback: " + downloadCallback);
        HttpUrl.Builder urlBuilder = HttpUrl.parse(downloadUrl).newBuilder();
        urlBuilder.addQueryParameter("activeId", RemotePlatform.getInstance().getAttachInfo().getActiveId());
        Request request = new Request.Builder().url(urlBuilder.build()).build();
        if (mOkHttpClient == null) {
            try {
                throw new Exception("okhttp not init,try FileUtils.get().downloadFile");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Call httpcall = mOkHttpClient.newCall(request);
        httpcall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                downloadCallback.onResult(false, "下载失败");
                LogUtil.d(TAG, "download failed.");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody responseBody = null;
                BufferedInputStream bis = null;
                FileOutputStream fos = null;
                try {
                    if (call.isCanceled()) {
                        downloadCallback.onResult(false, "下载已取消");
                        LogUtil.d(TAG, "download cancel.");
                        return;
                    }
                    if (response.isSuccessful()) {
                        responseBody = response.body();
                        long total = responseBody.contentLength();
                        bis = new BufferedInputStream(responseBody.byteStream());
                        File file = new File(savePath, fileName);
                        fos = new FileOutputStream(file);
                        byte[] bytes = new byte[1024 * 8];
                        int len;
                        long current = 0;
                        while ((len = bis.read(bytes)) != -1) {
                            fos.write(bytes, 0, len);
                            fos.flush();
                            current += len;
                            //计算进度
                            int progress = (int) (100 * current / total);
                            downloadCallback.onProgress(progress);
                        }
                    } else {
                        downloadCallback.onResult(true, "下载失败");
                        LogUtil.d(TAG, "download error");
                    }
                } catch (Exception e) {
                    //sendFailResultCallback(e);
                } finally {
                    if (fos != null) {
                        fos.close();
                    }
                    if (bis != null) {
                        bis.close();
                    }
                    if (null != responseBody) {
                        responseBody.close();
                    }
                }
            }
        });
    }

    /**
     * @param saveDir
     * @return
     * @throws IOException 判断下载目录是否存在
     */
    private String isExistDir(String saveDir) throws IOException {
        // 下载位置
        File downloadFile = new File(Environment.getExternalStorageDirectory(), saveDir);
        if (!downloadFile.mkdirs()) {
            downloadFile.createNewFile();
        }
        String savePath = downloadFile.getAbsolutePath();
        return savePath;
    }

    /**
     * @param url
     * @return 从下载连接中解析出文件名
     */
    @NonNull
    private String getNameFromUrl(String url) {
        return url.substring(url.lastIndexOf("/") + 1);
    }

    /**
     * 获取文件md5值
     *
     * @param path 文件路径
     * @return md5 md5字符串
     */
    public static String getFileMd5(String path) {
        Log.e(TAG, "");
        File file = new File(path);
        if (!file.exists()) {
            LogUtil.e(TAG, "File is not exist,getFileMd5 failed.");
            return null;
        }
        BigInteger bi = null;
        try {
            byte[] buffer = new byte[8192];
            int len = 0;
            MessageDigest md = MessageDigest.getInstance("MD5");
            File f = new File(path);
            FileInputStream fis = new FileInputStream(f);
            while ((len = fis.read(buffer)) != -1) {
                md.update(buffer, 0, len);
            }
            fis.close();
            byte[] b = md.digest();
            bi = new BigInteger(1, b);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bi.toString(16);
    }

    /**
     * 删除文件
     *
     * @param filePath 文件路径
     * @return 是否成功删除
     */
    public static boolean deleteFile(String filePath) {
        Log.d(TAG, "deleteFile: start del file " + filePath);
        File file = new File(filePath);
        if (file.isFile() && file.exists()) {
            boolean delFileSucc = file.delete();
            Log.d(TAG, "del single file " + filePath + " ret = " + delFileSucc);
            return delFileSucc;
        } else {
            Log.d(TAG, "file " + filePath + "is not exists");
            return true;
        }
    }

    /**
     * 重命名文件
     *
     * @param oldPath 旧文件路径
     * @param newPath 新文件路径
     * @return 是否成功重命名
     */
    public static boolean renameFile(String oldPath, String newPath) {
        if (!oldPath.equals(newPath)) {
            File oldFile = new File(oldPath);
            File newFile = new File(newPath);
            if (oldFile.renameTo(newFile)) {
                Log.d(TAG, "renameFile success.oldPath:" + oldPath + "  newPath:" + newPath);
                return true;
            } else {
                Log.d(TAG, "renameFile fail.oldPath:" + oldPath + "  newPath:" + newPath);
                return false;
            }
        } else {
            Log.d(TAG, "renameFile fail.");
            return false;
        }
    }

    /**
     * 将图片转换成Base64编码的字符串
     */
    public static String imageToBase64(String path) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        InputStream is = null;
        byte[] data = null;
        String result = null;
        try {
            is = new FileInputStream(path);
            //创建一个字符流大小的数组。
            data = new byte[is.available()];
            //写入数组
            is.read(data);
            //用默认的编码格式进行编码
            result = Base64.encodeToString(data, Base64.NO_CLOSE);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return result;
    }
}
