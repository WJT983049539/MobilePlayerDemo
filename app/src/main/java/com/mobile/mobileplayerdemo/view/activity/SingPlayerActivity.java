package com.mobile.mobileplayerdemo.view.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mobile.mobileplayerdemo.tools.LogUtils;
import com.mobile.mobileplayerdemo.R;
import com.mobile.mobileplayerdemo.model.bean.playUrlbean;
import com.mobile.mobileplayerdemo.presenter.PlayHandler;
import com.mobile.mobileplayerdemo.presenter.Runable.CheckPremissionRunnable;
import com.mobile.mobileplayerdemo.presenter.Runable.StartPlayRunnable;
import com.mobile.mobileplayerdemo.view.customview.CustomSingPlayActivity;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.listener.VideoAllCallBack;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;
import com.unitend.udrm.util.OnUDRMListener;
import com.unitend.udrm.util.UDRM;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SingPlayerActivity extends AppCompatActivity {
    /**
     * 默认的drmservice ip地址
     */
    private final String drminfo = "METHOD=SAMPLE-AES,VDECFORMAT=h264-f,KEYFORMATVERSIONS=1,KEYFORMAT=chinadrm,OPERATOR=voole,MIMETYPE=application/vnd.unitend.drm,IV=000102030405060708090A0B0C0D0E0F,CONTENTID=TC_00001,IP=10.110.95.118,PORT=8080,URI=udrm://10.110.95.118:443/udrmservice/services/UdrmSysWS.UdrmSysWSHttpSoap12Endpoint/";
    private static final int UDRM_DECRYPT_FAILED = 23;
    private static final int LOG_MSG_CAT = 24;
    private SharedPreferences preferences;
    private  playUrlbean playUrlbean;
    private SharedPreferences.Editor editor = null;
    private CustomSingPlayActivity sing_player;
    private boolean isPlay;
    private boolean isPause;
    private OrientationUtils orientationUtils;

    private String path;
    private String contentID;
    private UDRM mUDRM;//安全播放器对象
    private Handler playhandler;
    private String username="";
    private String passwd="";
    private String drmUrl;//拼接后的代理服务器地址
    private String operator;//运营商
    private String authenticationinfo;
    private playUrlbean pp;

    /***
     * udrm监听事件
     */
    private OnUDRMListener onUDRMListener = new OnUDRMListener() {
        @Override
        public void onInfoListener(int uniqueId, int type, Object message) {
            if(type == -9999){
                Message msg = new Message();
                msg.obj = message;
                msg.what = LOG_MSG_CAT;
                playhandler.sendMessage(msg);
            }
        }

        @Override
        public void onErrorListener(int uniqueId, int errorNO, String message) {
                Message msg = new Message();
                msg.arg1 = errorNO;
                msg.obj = message;
                msg.what = UDRM_DECRYPT_FAILED;
                playhandler.sendMessage(msg);
        }

        @Override
        public void onEventListener(int uniqueId, int eventNO, String message) {

        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //全屏显示
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (getSupportActionBar()!=null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.sing_video);
        initView();
        playhandler= new PlayHandler(this);
        pp= (com.mobile.mobileplayerdemo.model.bean.playUrlbean) getIntent().getSerializableExtra("path");//得到播放路径
        path=pp.getPlayurl();
        Pattern p = Pattern.compile("\\s*|\t|\r|\n");
        Matcher m = p.matcher(path);
        path = m.replaceAll("").trim();
        contentID=pp.getContentId();
        SharedPreferences  sharedPreferences= getSharedPreferences("playerSetInfo", Context.MODE_PRIVATE);
        //查看解密服务是否打开
        boolean falg=sharedPreferences.getBoolean("DrmSwitch",true);
        LogUtils.i("开关为:"+falg);

        if(falg){
            mUDRM=new UDRM(this);
            String version = mUDRM.getUdrmVersion();//获取版本号
            /**
             * 判断播放权限
             */
            judgePermiss();
        }else{
            //否则直接播放不用解密
            checkPressionSuccess(path);
        };
    }


    private void judgePermiss() {
        TextView textView= (TextView) sing_player.getStatuView();
        if(textView!=null){
            textView.setText("开始检测权限...");
        };
//        mUDRM.setAgentType(1);//不知道这是啥玩意
        preferences = getSharedPreferences("DrmUrl", Context.MODE_PRIVATE);
        editor = preferences.edit();
        //得到DRM服务器地址，这个地址应该在外面早早地设置
        String DrmServiceUrl = preferences.getString("drmUrl", "");
        operator = preferences.getString("operator", "");
        if(DrmServiceUrl.equals("")||operator.equals("")){
            Toast.makeText(this,"代理服务器或者运营商设置有误",Toast.LENGTH_LONG).show();
        }

        //格式化地址
        DrmServiceUrl = DrmServiceUrl.replaceAll("：", ":");
        DrmServiceUrl = DrmServiceUrl.replaceAll("。", ".");
        if (!DrmServiceUrl.contains(":")) {
            DrmServiceUrl = DrmServiceUrl + ":443";
        }
        /**
         * 这些东西哪里出来的？固定的?·
         */
        drmUrl = getDrmUrl();//得到代理服务器地址drmUrl = "METHOD=SAMPLE-AES,VDECFORMAT=h264-f,KEYFORMATVERSIONS=1,KEYFORMAT=chinadrm,OPERATOR=unitend,MIMETYPE=application/vnd.unitend.drm,IV=000102030405060708090A0B0C0D0E0F,CONTENTID=20191025,URI=https://192.168.113.40:443/udrmrsa/udrmGetLicense"
        String locmacAddress=LogUtils.getMac(SingPlayerActivity.this);
        String macAddress= "18-99-F5-BF-81-46";//这个mac地址未来需要获取
        String  deviceId  = "deviceTest";
        String userId="5787989";
        String auth = "***********";
        String AAA= "deviceId="+deviceId+"$$userId="+userId+"$$auth="+auth;//格式不变
        String udrmip =DrmServiceUrl;
        if(!TextUtils.isEmpty(deviceId) && !TextUtils.isEmpty(macAddress)&& !TextUtils.isEmpty(AAA) && !TextUtils.isEmpty(contentID)){
            mUDRM.UDRMSetDrmInfo(udrmip, macAddress,AAA,contentID,onUDRMListener);
            Log.i("test", "UDRMSetDrmInfo :" + " udrmip : "+ udrmip +",macAddress = "+ macAddress +" ,AAA = "+ AAA + ",contentID = "+contentID);
        }else{
            Toast.makeText(SingPlayerActivity.this, "获取权限时参数短缺", Toast.LENGTH_SHORT).show();
            Log.i("test", "UDRMSetDrmInfo :" + " udrmip : "+ udrmip +",macAddress = "+ macAddress +" ,AAA = "+ AAA + ",contentID = "+contentID);
        }
//        //启动检测权限
        /**
         * mUDRM :解密播放器实例
         * playhandler:handler
         * drmUrl:服务器地址
         * username:用户名
         * passwed:密码
         * authenticationinfo：鉴权地址
         * macAddress：mac 地址
         * contentID：内容id
         * operator:运营商
         *
         */
        CheckPremissionRunnable checkPremissionRunnable=new CheckPremissionRunnable(mUDRM,playhandler,drmUrl,username,passwd,authenticationinfo,macAddress,contentID,operator);
        Thread thread=new Thread(checkPremissionRunnable);
        thread.start();

    }

    private void initView() {
        sing_player = (CustomSingPlayActivity) findViewById(R.id.sing_player);
//        sing_player.setLooping(true);//一直循环
        sing_player.getBackButton().setVisibility(View.VISIBLE);
        //外部辅助的旋转，帮助全屏
        orientationUtils = new OrientationUtils(this, sing_player);
        //设置返回按键功能
        sing_player.getBackButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
    @Override
    protected void onPause() {
        sing_player.getCurrentPlayer().onVideoPause();
        super.onPause();
        isPause = true;
    }

    @Override
    protected void onResume() {
        sing_player.getCurrentPlayer().onVideoResume(false);
        super.onResume();
        isPause = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isPlay) {
            if(mUDRM!=null){
                mUDRM.stopPlayerAgent();
            }
            sing_player.getCurrentPlayer().release();
            GSYVideoManager.releaseAllVideos();//释放所有
        }
        if (orientationUtils != null)
            orientationUtils.releaseListener();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //如果旋转了就全屏
        if (isPlay && !isPause) {
            sing_player.onConfigurationChanged(this, newConfig, orientationUtils, true, true);
        }
    }
    private String getDrmUrl() {
        String drmInfo = null;
        String str = preferences.getString("drmUrl", "");
        operator = preferences.getString("operator", "");
        if (str.equals("") || str == null)
            drmInfo = drminfo;
        else {
            if (!("".equals(path)) || path != null) {
                if (!(operator.equals("")) || operator != null) {
                    if (!"".equals(contentID) && null != contentID) {
                        drmInfo = "METHOD=SAMPLE-AES,VDECFORMAT=h264-f,KEYFORMATVERSIONS=1,KEYFORMAT=chinadrm,OPERATOR="
                                + operator
                                + ",MIMETYPE=application/vnd.unitend.drm,IV=000102030405060708090A0B0C0D0E0F,CONTENTID="
                                + contentID
                                + ",URI="
                                + str;
                    } else {
                        drmInfo = "METHOD=SAMPLE-AES,VDECFORMAT=h264-f,KEYFORMATVERSIONS=1,KEYFORMAT=chinadrm,OPERATOR="
                                + operator
                                + ",MIMETYPE=application/vnd.unitend.drm,IV=000102030405060708090A0B0C0D0E0F,CONTENTID=20160223,"
                                + ",URI="
                                + str;
                    }
                }
            }

        }
        return drmInfo;
    }
    /**
     * 检测权限失败
     */
    public void checkPremissFail(String msg){
        TextView textView= (TextView) sing_player.getStatuView();
        if(textView!=null){
            textView.setText(msg);
            Resources resource = (Resources) getBaseContext().getResources();
            ColorStateList csl = (ColorStateList) resource.getColorStateList(R.color.red);
            textView.setTextColor(csl);
        };
    }
    /**
     * 检测权限成功，开始转url进行播放
     *
     */
    public void zhuanhuanUrl(){
        TextView textView= (TextView) sing_player.getStatuView();
        if(textView!=null){
            textView.setText("检测权限成功,开始转换代理url...");
            Resources resource = (Resources) getBaseContext().getResources();
            ColorStateList csl = (ColorStateList) resource.getColorStateList(R.color.wrning);
            textView.setTextColor(csl);
        };
        /*
         * 启动播放代理
         */
        LogUtils.i("准备播放的url为 "+path);
        StartPlayRunnable startPlayRunnable=new StartPlayRunnable(this,mUDRM,path,playhandler);
        Thread thread=new Thread(startPlayRunnable);
        thread.start();
    }

    /**
     * 检测权限成功
     */
    public void checkPressionSuccess(String url) {
        //转换url代理成功然后播放
        TextView textView= (TextView) sing_player.getStatuView();
        if(textView!=null){
            textView.setText("转换代理成功");
        };
        sing_player.setViSIBLE(View.GONE);
        //增加封面
        ImageView imageView = new ImageView(this);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setImageResource(R.mipmap.ic_launcher);
//        sing_player.setThumbImageView(imageView);
        sing_player.setStartAfterPrepared(true);
        sing_player.setAutoFullWithSize(false);
        sing_player.setShowFullAnimation(false);
        sing_player.setNeedLockFull(true);
        sing_player.setRotateViewAuto(false);
        sing_player.setLockLand(false);
        //设置返回键

        sing_player.setNeedShowWifiTip(false);
        sing_player.setUp(url,true,"");
        orientationUtils.setEnable(false);
//        mUDRM.stopPlayerAgent();//先把以前的关闭，考虑把这部关闭

        //增加title
        sing_player.getTitleTextView().setVisibility(View.GONE);
        sing_player.getFullscreenButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //直接横屏
                orientationUtils.resolveByClick();
                //第一个true是否需要隐藏actionbar，第二个true是否需要隐藏statusbar
                sing_player.startWindowFullscreen(SingPlayerActivity.this, true, true);
            }
        });
        //停止事件
        if(sing_player.getStopView()!=null){
            sing_player.getStopView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sing_player.getCurrentPlayer().release();//
//                     if(mUDRM!=null){
//                         mUDRM.stopPlayerAgent();
//                     }
                }
            });
        }
//        sing_player.getStartButton().setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(sing_player!=null){
//                   int aa= sing_player.getCurrentState();
//                   LogUtils.i(aa+"");
//                }
//            }
//        });
        sing_player.startPlayLogic();//立即开始播放
        sing_player.setVideoAllCallBack(new VideoAllCallBack() {
            @Override
            public void onStartPrepared(String url, Object... objects) {
                LogUtils.i("onStartPrepared");
                TextView textView= (TextView) sing_player.getStatuView();
                if(textView!=null){
                    textView.setText("准备播放");
                };
            }

            @Override
            public void onPrepared(String url, Object... objects) {
                LogUtils.i("onPrepared");
                //开始播放了才能旋转和全屏
                        orientationUtils.setEnable(true);
                        isPlay = true;

            }

            @Override
            public void onClickStartIcon(String url, Object... objects) {
                LogUtils.i("onClickStartIcon");
            }

            @Override
            public void onClickStartError(String url, Object... objects) {
                LogUtils.i("onClickStartError");
            }

            @Override
            public void onClickStop(String url, Object... objects) {
                LogUtils.i("onClickStop");
            }

            @Override
            public void onClickStopFullscreen(String url, Object... objects) {
                LogUtils.i("onClickStopFullscreen");
            }

            @Override
            public void onClickResume(String url, Object... objects) {
                LogUtils.i("onClickResume");
            }

            @Override
            public void onClickResumeFullscreen(String url, Object... objects) {
                LogUtils.i("onClickResumeFullscreen");
            }

            @Override
            public void onClickSeekbar(String url, Object... objects) {
                LogUtils.i("onClickSeekbar");
            }

            @Override
            public void onClickSeekbarFullscreen(String url, Object... objects) {
                LogUtils.i("onClickSeekbarFullscreen");
            }

            @Override
            public void onAutoComplete(String url, Object... objects) {
                LogUtils.i("onAutoComplete");
            }

            @Override
            public void onEnterFullscreen(String url, Object... objects) {
                LogUtils.i("onEnterFullscreen");
            }

            @Override
            public void onQuitFullscreen(String url, Object... objects) {
                LogUtils.i("onQuitFullscreen");
                if (orientationUtils != null) {
                            orientationUtils.backToProtVideo();
                        }
            }

            @Override
            public void onQuitSmallWidget(String url, Object... objects) {
                LogUtils.i("onQuitSmallWidget");
            }

            @Override
            public void onEnterSmallWidget(String url, Object... objects) {
                LogUtils.i("onEnterSmallWidget");
            }

            @Override
            public void onTouchScreenSeekVolume(String url, Object... objects) {
                LogUtils.i("onTouchScreenSeekVolume");
            }

            @Override
            public void onTouchScreenSeekPosition(String url, Object... objects) {
                LogUtils.i("onTouchScreenSeekPosition");
            }

            @Override
            public void onTouchScreenSeekLight(String url, Object... objects) {
                LogUtils.i("onTouchScreenSeekLight");
            }
            @Override
            public void onPlayError(String url, Object... objects) {
                LogUtils.i("onPlayError");
                LogUtils.ToastShow(SingPlayerActivity.this,"播放失败,请检查播放链接或网络状况!");
//                Toast.makeText(SingPlayerActivity.this,"播放失败,请检查播放链接",Toast.LENGTH_LONG).show();
                //到这里了
                SingPlayerActivity.this.finish();//关闭本窗口
            }

            @Override
            public void onClickStartThumb(String url, Object... objects) {
                LogUtils.i("onClickStartThumb");
            }

            @Override
            public void onClickBlank(String url, Object... objects) {
                LogUtils.i("onClickBlank");
            }

            @Override
            public void onClickBlankFullscreen(String url, Object... objects) {
                LogUtils.i("onClickBlankFullscreen");
            }
        });
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            sing_player.onVideoPause();
        }

        return super.onKeyDown(keyCode, event);
    }


}
