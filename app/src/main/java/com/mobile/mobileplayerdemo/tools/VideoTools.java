package com.mobile.mobileplayerdemo.tools;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.storage.StorageManager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

public class VideoTools {
    public static int dp2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {

                //这种方法也可以
                //return mNetworkInfo .getState()== NetworkInfo.State.CONNECTED

                return mNetworkInfo.isAvailable();

            }
        }
        return false;
    }

    /**
     * 不是m3u8格式的就返回true;
     * @param url
     * @return
     */
   public static boolean lastgeshi(String url) {
        boolean flag=false;
        String geshi=url.substring(url.length()-4,url.length());
        if(geshi.equals("m3u8")){
            flag=false;
        }else{
            flag=true;
        }
        return flag;
    }
    /**
     * 获取外置SD卡路径
     * String[0]为内置卡
     * String[1]为外置卡
     */
    public static String[] getExtSDCardPath(Activity activity) {
        StorageManager storageManager = (StorageManager) activity.getSystemService(Context.STORAGE_SERVICE);
        try {
            Class<?>[] paramClasses = {};
            Method getVolumePathsMethod = StorageManager.class.getMethod("getVolumePaths", paramClasses);
            getVolumePathsMethod.setAccessible(true);
            Object[] params = {};
            Object invoke = getVolumePathsMethod.invoke(storageManager, params);
            return (String[])invoke;
        } catch (NoSuchMethodException e1) {
            e1.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }
}
