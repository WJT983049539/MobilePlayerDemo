package com.mobile.mobileplayerdemo.presenter;


import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.mobile.mobileplayerdemo.tools.LogUtils;
import com.mobile.mobileplayerdemo.view.activity.SingPlayerActivity;

import androidx.annotation.NonNull;

public class PlayHandler extends Handler {
    //drm)proc_status_net_error
    private final static int DRM_PROC_STATUS_NET_ERROR = 5;//网络错误.UDRM获取权限失败
    //drm)proc_status_system_error
    private final static int DRM_PROC_STATUS_SYSTEM_ERROR = 14;//系统错误.
    // UDRM获取权限失败
    //drm)proc_status_cer_error
    private final static int DRM_PROC_STATUS_CER_ERROR = 15;//证书错误.UDRM获取权限失败
    //drm)proc_status_register_error
    private final static int DRM_PROC_STATUS_REGISTER_ERROR = 16;//授权错误.UDRM获取权限失败
    private final static int DRM_PROC_STATUS_START = 18;//开始检测权限
    private final static int DRM_PROC_STATUS_AAA_FAILED = 19;//AAA 检测权限失败
    private final static int DRM_PROC_STATUS_CHECK_FAILED = 20;//检测权限失败,erroNO. = " + ret + ".\n
    private final static int DRM_PROC_STATUS_CHECK_SUCCESS = 21;//检测权限成功
    private static final int RE_PLAY_AFTER_ERROR = 22;
    private static final int UDRM_DECRYPT_FAILED = 23;
    private static final int LOG_MSG_CAT = 24;
    private static final int TIME_LIMIT = 500;
    private static final int TIME_FLAG = 5000;
    private SingPlayerActivity singPlayerActivity;
    public PlayHandler(SingPlayerActivity singPlayerActivity) {
        this.singPlayerActivity=singPlayerActivity;
    }
    @Override
    public void handleMessage(@NonNull Message msg) {
        super.handleMessage(msg);
        switch (msg.what){
            /**
             * urm的日志
             */
            case LOG_MSG_CAT:
                LogUtils.i("DRMplayer日志...");
                break;
            /**
             * udrm 解密失败
             */
            case UDRM_DECRYPT_FAILED:
                LogUtils.i("解密失败...");
                LogUtils.ToastShow(singPlayerActivity,"解密失败!");
                singPlayerActivity.checkPremissFail("解密视频失败");
                    break;
            case DRM_PROC_STATUS_START:
                LogUtils.i("开始检测权限...");
                singPlayerActivity.checkPremissFail("正在检测权限");
                break;
            case DRM_PROC_STATUS_AAA_FAILED:
                LogUtils.i(" AAA检测权限失败...");
                singPlayerActivity.checkPremissFail("AAA检测权限失败");
                LogUtils.ToastShow(singPlayerActivity,"AAA检测权限失败!");
                break;
            case DRM_PROC_STATUS_CHECK_FAILED:
                int ret = msg.arg1;
                LogUtils.i("检测权限失败,erroNO. = "+ret );
                singPlayerActivity.checkPremissFail("检测权限失败");
                LogUtils.ToastShow(singPlayerActivity,"检测权限失败!"+ret);
//                singPlayerActivity.zhuanhuanUrl();
                break;
            case DRM_PROC_STATUS_CHECK_SUCCESS:
                LogUtils.i(" 检测权限成功...");
                singPlayerActivity.zhuanhuanUrl();
                break;
            case DRM_PROC_STATUS_CER_ERROR:
                singPlayerActivity.checkPremissFail("证书错误,UDRM获取权限失败!");
                LogUtils.i(" 证书错误.\nUDRM获取权限失败.\n...");
                LogUtils.ToastShow(singPlayerActivity,"证书错误,UDRM获取权限失败!");
                break;
            case DRM_PROC_STATUS_NET_ERROR:
                //暂时用成功的方法
//                singPlayerActivity.zhuanhuanUrl();
//                singPlayerActivity.checkPremissFail(" 网络错误.\nUDRM获取权限失败");
                LogUtils.i(" 网络错误.\nUDRM获取权限失败.\n");
                singPlayerActivity.checkPremissFail("网络错误.\nUDRM获取权限失败");
//                Toast.makeText(singPlayerActivity,"网络错误,请检查网络配置",Toast.LENGTH_LONG).show();
                LogUtils.ToastShow(singPlayerActivity,"网络错误,请检查网络配置!");
                break;
            case DRM_PROC_STATUS_REGISTER_ERROR:
                singPlayerActivity.checkPremissFail("授权错误,UDRM获取权限失败");
                LogUtils.i("授权错误.\nUDRM获取权限失败.\n");
                LogUtils.ToastShow(singPlayerActivity,"授权错误,UDRM获取权限失败!");
                break;
            case DRM_PROC_STATUS_SYSTEM_ERROR:
                singPlayerActivity.checkPremissFail("系统错误,UDRM获取权限失败");
                LogUtils.ToastShow(singPlayerActivity,"系统错误,UDRM获取权限失败!");
                LogUtils.i("系统错误.\nUDRM获取权限失败.\n");
                break;
            /**
             * 转换url成功，把url放入播放器开始播放
             */
            case 0x0019:
                String url=msg.obj.toString();
                singPlayerActivity.checkPressionSuccess(url);
                break;

        }
    }
}
