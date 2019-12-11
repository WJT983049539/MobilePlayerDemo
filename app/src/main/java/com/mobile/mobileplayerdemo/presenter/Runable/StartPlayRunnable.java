package com.mobile.mobileplayerdemo.presenter.Runable;

import android.os.Handler;
import android.os.Message;

import com.mobile.mobileplayerdemo.tools.LogUtils;
import com.mobile.mobileplayerdemo.view.activity.SingPlayerActivity;
import com.unitend.udrm.util.UDRM;

/**
 * 这个runnable是为了转换成代理url
 */
public class StartPlayRunnable implements Runnable{
    private SingPlayerActivity singPlayerActivity;
    private UDRM mUDRM;
    private String path;
    private Handler playhandler;


    public StartPlayRunnable(SingPlayerActivity singPlayerActivity, UDRM mUDRM, String path, Handler playhandler) {
        this.singPlayerActivity=singPlayerActivity;
        this.mUDRM=mUDRM;
        this.path=path;
        this.playhandler=playhandler;
    }

    @Override
    public void run() {
        LogUtils.i("开始转化代理");
        String getUrl = mUDRM.startPlayerAgent(path);
        LogUtils.i("转化代理成功");
        Message message=new Message();
        message.what=0x0019;
        message.obj=getUrl;
        playhandler.sendMessage(message);
    }
}
