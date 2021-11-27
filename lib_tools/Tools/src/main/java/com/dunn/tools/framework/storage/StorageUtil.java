package com.dunn.tools.framework.storage;

import android.app.usage.StorageStatsManager;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.text.format.Formatter;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;

public class StorageUtil {

    public final static String TAG = "coocaa_storage";

    public static StorageEntity queryWithStorageManager(Context context) {

        StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
        int version = Build.VERSION.SDK_INT;
        StorageEntity storageEntity = new StorageEntity(context);
        if (version < Build.VERSION_CODES.M) {//小于6.0，只能查到共享卷即除系统外的内存大小
            try {
                Method getVolumeList = StorageManager.class.getDeclaredMethod("getVolumeList");
                StorageVolume[] volumeList = (StorageVolume[]) getVolumeList.invoke(storageManager);
                long totalSize = 0, availableSize = 0;
                if (volumeList != null) {
                    Method getPathFile = null;
                    for (StorageVolume volume : volumeList) {
                        if (getPathFile == null) {
                            getPathFile = volume.getClass().getDeclaredMethod("getPathFile");
                        }
                        File file = (File) getPathFile.invoke(volume);
                        totalSize += file.getTotalSpace();
                        availableSize += file.getUsableSpace();
                    }
                    storageEntity.totalSize = totalSize;
                    storageEntity.freeSize = availableSize;
                    storageEntity.usedSize = totalSize - availableSize;
                    storageEntity.appSpaceSize = storageEntity.usedSize;
                }
                Log.d(TAG, "totalSize = " + Formatter.formatFileSize(context,totalSize) + " ,availableSize = " + Formatter.formatFileSize(context,availableSize));
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        } else {

            try {
                Method getVolumes = StorageManager.class.getDeclaredMethod("getVolumes");//6.0
                List<Object> getVolumeInfo = (List<Object>) getVolumes.invoke(storageManager);
                long total = 0L, used = 0L, systemSize = 0L;
                for (Object obj : getVolumeInfo) {
                    Log.i(TAG, " volumes:" + obj);
                    Field getType = obj.getClass().getField("type");
                    int type = getType.getInt(obj);
                    Log.d(TAG, "type: " + type);
                    if (type == 1) {//TYPE_PRIVATE

                        long totalSize = 0L;

                        //获取内置内存总大小
                        if (version >= Build.VERSION_CODES.O) {//8.0
                            Method getFsUuid = obj.getClass().getDeclaredMethod("getFsUuid");
                            String fsUuid = (String) getFsUuid.invoke(obj);
                            totalSize = getTotalSize(context, fsUuid);//8.0 以后使用
                        } else if (version >= Build.VERSION_CODES.N_MR1) {//7.1.1
                            Method getPrimaryStorageSize = StorageManager.class.getMethod("getPrimaryStorageSize");//5.0 6.0 7.0没有
                            totalSize = (long) getPrimaryStorageSize.invoke(storageManager);
                        }

                        Method isMountedReadable = obj.getClass().getDeclaredMethod("isMountedReadable");
                        boolean readable = (boolean) isMountedReadable.invoke(obj);
                        if (readable) {
                            Method file = obj.getClass().getDeclaredMethod("getPath");
                            File f = (File) file.invoke(obj);

                            if (totalSize == 0) {
                                totalSize = f.getTotalSpace();
                            }
                            systemSize = totalSize - f.getTotalSpace();
                            used += totalSize - f.getFreeSpace();
                            total += totalSize;
                        }

                    }
//                    else if (type == 0) {//TYPE_PUBLIC
//                        //外置存储
//                        Method isMountedReadable = obj.getClass().getDeclaredMethod("isMountedReadable");
//                        boolean readable = (boolean) isMountedReadable.invoke(obj);
//                        if (readable) {
//                            Method file = obj.getClass().getDeclaredMethod("getPath");
//                            File f = (File) file.invoke(obj);
//                            used += f.getTotalSpace() - f.getFreeSpace();
//                            total += f.getTotalSpace();
//                        }
//                    } else if (type == 2) {//TYPE_EMULATED//模拟存储
//
//                    }
                }

                storageEntity.totalSize = total;
                storageEntity.usedSize = used;
                storageEntity.freeSize = total - used;
                storageEntity.systemSize = systemSize;
                storageEntity.appSpaceSize = used - systemSize;

                Log.e(TAG, " total:"+Formatter.formatFileSize(context, total));
                Log.e(TAG, " used:"+Formatter.formatFileSize(context, used));
                Log.e(TAG, " freeSize:"+Formatter.formatFileSize(context, storageEntity.freeSize));
                Log.e(TAG, " storageEntity.systemSize:"+Formatter.formatFileSize(context, storageEntity.systemSize));
                Log.e(TAG, " storageEntity.appSpaceSize:"+Formatter.formatFileSize(context, storageEntity.appSpaceSize));
            } catch (SecurityException e) {
                Log.e(TAG, " no permission.PACKAGE_USAGE_STATS");
            } catch (Exception e) {
                e.printStackTrace();
                storageEntity = queryWithStatFs(context);
            }
        }
        return storageEntity;
    }

    /**
     * 此方法不会把系统空间算进来的
     */
    public static StorageEntity queryWithStatFs(Context context) {
        StatFs statFs = new StatFs(Environment.getExternalStorageDirectory().getPath());
        //存储块
        long blockCount = statFs.getBlockCount();
        //块大小
        long blockSize = statFs.getBlockSize();
        //可用块数量
        long availableCount = statFs.getAvailableBlocks();
        //剩余块数量，注：这个包含保留块（including reserved blocks）即应用无法使用的空间
        long freeBlocks = statFs.getFreeBlocks();

        StorageEntity storageEntity = new StorageEntity(context);
        storageEntity.totalSize = blockSize * blockCount;
        storageEntity.freeSize = blockSize * freeBlocks;
        storageEntity.usedSize = storageEntity.totalSize - storageEntity.freeSize;
        storageEntity.appSpaceSize = storageEntity.usedSize;

        return storageEntity;
    }


    /**
     * API 26 android O
     * 获取总共容量大小，包括系统大小
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private static long getTotalSize(Context context, String fsUuid) {
        try {
            UUID id;
            if (fsUuid == null) {
                id = StorageManager.UUID_DEFAULT;
            } else {
                id = UUID.fromString(fsUuid);
            }
            StorageStatsManager stats = context.getSystemService(StorageStatsManager.class);
            return stats.getTotalBytes(id);
        } catch (NoSuchFieldError | NoClassDefFoundError | NullPointerException | IOException e) {
            e.printStackTrace();
            return -1;
        }
    }


    public static class StorageEntity {
        private Context context;

        private long totalSize;//存储总大小

        private long usedSize;//已经使用的存储大小

        private long freeSize;//剩余可用的存储大小

        private long systemSize;//系统占用大小

        private long appSpaceSize;//应用占用大小

        public StorageEntity(Context context) {
            this.context = context;
        }

        public String getUsedSize() {
            return Formatter.formatFileSize(context,usedSize);
        }

        public String getFreeSize() {
            return Formatter.formatFileSize(context,freeSize);
        }

        public String getSystemSize() {
            if (systemSize <= 0) {
                return "未知";
            }
            return Formatter.formatFileSize(context,systemSize);
        }

        public String getAppSpaceSize() {
            return Formatter.formatFileSize(context,appSpaceSize);
        }

        public String getTotalSize() {
            return Formatter.formatFileSize(context,totalSize);
        }

        @Override
        public String toString() {
            return "StorageEntity{" +
                    "totalSize=" + getTotalSize() +
                    ", usedSize=" + getUsedSize() +
                    ", freeSize=" + getFreeSize() +
                    ", systemSize=" + getSystemSize() +
                    ", appSpaceSize=" + getAppSpaceSize() +
                    '}';
        }
    }
}
