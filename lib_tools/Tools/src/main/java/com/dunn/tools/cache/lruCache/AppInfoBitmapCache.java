package com.dunn.tools.cache.lruCache;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;
import android.widget.ImageView;

import com.dunn.tools.log.LogUtil;
import com.dunn.tools.thread.ThreadManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class AppInfoBitmapCache {
    private static final String TAG = "AppInfoBitmapCache$";
    private final LruCache<String, Bitmap> mMemoryCache;
    @SuppressLint("StaticFieldLeak")
    private static volatile AppInfoBitmapCache sInstance;
    private ExecutorService mExecutor;

    private AppInfoBitmapCache() {
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int mCacheSize = maxMemory / 8;

        mMemoryCache = new LruCache<String, Bitmap>(mCacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value == null ? 0 : value.getByteCount();
            }
        };
    }

    public synchronized static AppInfoBitmapCache getInstance() {
        if (sInstance == null) {
            synchronized (AppInfoBitmapCache.class) {
                if (sInstance == null) {
                    sInstance = new AppInfoBitmapCache();
                }
            }
        }
        return sInstance;
    }

    public void delete(Context mContext, String key) {
        mMemoryCache.remove(key);
        deleteBitmap(mContext, key);
    }

    public void clear(Context mContext) {
        mMemoryCache.evictAll();
        deleteAllMsgBitmap(mContext);
    }

    public void saveBitmap(Context mContext, String key, Bitmap bitmap, int w, int h) {
        if (bitmap == null) {
            return;
        }
        long time = System.currentTimeMillis();
        try {
            File file = new File(msgImgPath(mContext), key);
            File parentFile = file.getParentFile();
            if (parentFile != null && !parentFile.exists()) {
                boolean mkdirs = parentFile.mkdirs();
                //LogUtil.d("appinfo", TAG + "saveBitmap: mkdirs=" + mkdirs+",path=" + parentFile.getAbsolutePath());
            }
            Bitmap destBitmap = scaleBitmap(bitmap, w, h);
            LogUtil.d("appinfo", TAG + "saveBitmap: key=" + key + ", w=" + w + ", h=" + h);
            mMemoryCache.put(key, destBitmap);
            //saveBitmapToLocal(destBitmap, file);
        } catch (Exception e) {
            LogUtil.d("appinfo", TAG + "saveBitmap: e=" + e.getMessage());
        }
        //LogUtil.d("appinfo", TAG + "saveBitmap: time=" + (System.currentTimeMillis() - time));
    }

    public Bitmap load(Context mContext, String key) {
        if (key == null) return null;

        Bitmap bitmap = mMemoryCache.get(key);
        if (bitmap != null && !bitmap.isRecycled()) {
            return bitmap;
        }
//        Bitmap bitmapLocal = loadBitmapFile(mContext, key);
//        if (bitmapLocal != null && !bitmapLocal.isRecycled()) {
//            return bitmapLocal;
//        }

        return null;
    }

    public void loadBitmap(Context mContext, String key, ImageView view) {
        Bitmap bitmap = mMemoryCache.get(key);
        if (bitmap != null && !bitmap.isRecycled()) {
            view.setImageBitmap(bitmap);
            return;
        }
        if (mExecutor == null) {
            mExecutor = new ThreadPoolExecutor(2, 2,
                    10, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
        }
        mExecutor.submit(new BitmapTask(mContext, key, view));
    }

    private static String msgImgPath(Context context) {
        return context.getFilesDir() + "/appinfo_img/";
    }

    private Bitmap loadBitmapFile(Context mContext, String key) {
        Bitmap bitmap = loadBitmap(mContext, key);
        if (bitmap != null && !bitmap.isRecycled()) {
            Bitmap ret = mMemoryCache.put(key, bitmap);
        }
        return bitmap;
    }

    private static Bitmap scaleBitmap(Bitmap bitmap, int w, int h) {
        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();
        if (bitmapWidth > w || bitmapHeight > h) {
            float scale = Math.min((float) w / bitmapWidth, (float) h / bitmapHeight);
            bitmap = Bitmap.createScaledBitmap(bitmap, Math.max(1, (int) (scale * bitmapWidth)),
                    Math.max(1, (int) (scale * bitmapHeight)), true);
        }
        return bitmap;
    }

    private static void saveBitmapToLocal(Bitmap bitmap, File file) {
        if (bitmap == null) {
            return;
        }
        long time = System.currentTimeMillis();
        if (file.exists()) {
            boolean delete = file.delete();
            boolean create = false;
            try {
                create = file.createNewFile();
            } catch (IOException e) {
                LogUtil.d(TAG, "saveBitmapToLocal createNewFile e=" + e.getMessage());
            }
            LogUtil.d(TAG, "saveBitmapToLocal delete=" + delete + ",create=" + create);
        }
        int count = 0;
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            count = bitmap.getByteCount();
            ByteBuffer buffer = ByteBuffer.allocate(count);
            bitmap.copyPixelsToBuffer(buffer);
            int w = bitmap.getWidth();
            int h = bitmap.getHeight();
            byte[] bytes = new byte[4];
            bytes[0] = (byte) ((w >> 8) & 0xFF);
            bytes[1] = (byte) (w & 0xFF);
            bytes[2] = (byte) ((h >> 8) & 0xFF);
            bytes[3] = (byte) (h & 0xFF);
            out.write(bytes);
            out.write(buffer.array());
            out.getFD().sync();
        } catch (Exception e) {
            LogUtil.d(TAG, "saveBitmapToLocal e=" + e.getMessage());
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    LogUtil.e(TAG, "saveBitmapToLocal e2=" + e.getMessage());
                }
            }
        }
        LogUtil.d(TAG, "saveBitmapToLocal count=" + count +
                ",time=" + (System.currentTimeMillis() - time));
    }

    private static Bitmap loadBitmap(Context context, String key) {
        long time = System.currentTimeMillis();
        Bitmap bitmap = null;
        File file = new File(msgImgPath(context), key);
        int count = 0;
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            byte[] bytes = new byte[4];
            count = in.read(bytes);
            int width = bytes[0] << 8;
            width += bytes[1];
            int height = bytes[2] << 8;
            height += bytes[3];
            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            ByteBuffer byteBuffer = ByteBuffer.allocate(bitmap.getByteCount());
            count = in.read(byteBuffer.array());
            bitmap.copyPixelsFromBuffer(byteBuffer);
        } catch (Exception e) {
            LogUtil.e(TAG, "loadBitmap e=" + e.getMessage());
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    LogUtil.e(TAG, "loadBitmap e2=" + e.getMessage());
                }
            }
        }
        LogUtil.d(TAG, "loadBitmap count=" + count +
                ",time=" + (System.currentTimeMillis() - time));
        return bitmap;
    }

    private static void deleteBitmap(Context context, String key) {
        File file = new File(msgImgPath(context), key);
        if (!file.exists()) {
            return;
        }
        boolean deleted = file.delete();
    }

    private static void deleteAllMsgBitmap(Context context) {
        File file = new File(msgImgPath(context));
        if (file.exists()) {
            File[] files = file.listFiles();
            if (files == null || files.length <= 0) {
                return;
            }
            boolean deleted = false;
            for (File f : files) {
                if (f.isDirectory()) {
                    continue;
                }
                deleted = f.delete();
            }
        }
    }

    private class BitmapTask implements Runnable {
        String key;
        Context mContext;
        WeakReference<ImageView> viewRef;

        BitmapTask(Context context, String key, ImageView view) {
            this.key = key;
            this.mContext = context;
            this.viewRef = new WeakReference<>(view);
        }

        @Override
        public void run() {
            final Bitmap bitmap = loadBitmapFile(mContext, key);
            final ImageView view = viewRef.get();
            if (view != null && key.equals(view.getTag())) {
                ThreadManager.getInstance().uiThread(new Runnable() {
                    @Override
                    public void run() {
                        view.setImageBitmap(bitmap);
                    }
                });
            }
        }
    }
}
