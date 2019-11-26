//package com.mobile.mobileplayerdemo.view.activity;
//
//import android.content.Context;
//import android.os.Bundle;
//import android.os.Environment;
//import android.util.Log;
//import android.view.View;
//
//import com.mobile.mobileplayerdemo.tools.ACache;
//import com.mobile.mobileplayerdemo.MobileVideoView;
//import com.mobile.mobileplayerdemo.R;
//import com.mobile.mobileplayerdemo.model.bean.playUrlbean;
//import com.shuyu.gsyvideoplayer.GSYBaseActivityDetail;
//import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder;
//import com.shuyu.gsyvideoplayer.listener.LockClickListener;
//import com.shuyu.gsyvideoplayer.model.GSYVideoModel;
//import com.shuyu.gsyvideoplayer.player.PlayerFactory;
//
//import com.shuyu.gsyvideoplayer.player.SystemPlayerManager;
//import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer;
//import com.unitend.udrm.util.UDRM;
//
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.ByteArrayOutputStream;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//import androidx.annotation.Nullable;
//
//
///**
// * 视频播放器
// */
//public class PlayVideoActvity  extends GSYBaseActivityDetail<com.mobile.mobileplayerdemo.MobileVideoView> {
//
//    private String playUrl = "http://10.110.95.117:8080/vp5e/z.m3u8";//播放地址
//    private String sdPath = Environment.getExternalStorageDirectory().toString();//sd卡地址
//    //认证方式
//    private String authenticationInfo = "{\"nns_user_id\":\"57a81c20247d26c5e9d171c7fb9de45d\",\"nns_device_id\":\"HMDAA01604150089\",\"nns_video_id\":\"c0b181a45b3a8d387b838acff1042081\",\"nns_video_type\":\"1\",\"nns_version\":\"1.3.7.STB.WSYPT.STD.HMD01.Release\",\"nns_data\":\"\"}";
//
//    private String contentID = null;
//    private String userName = "UDRM";//用户名
//    private String password = "UDRM";//密码
//    private String drmUrl;//数字加密链接
//    private String macAddress;//mac地址
//    private String strOperator;//操作员
//    private UDRM mUDRM;//安全播放器对象
//    private MobileVideoView MobileVideoView;
//    private ACache aCache;
//
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_layout_playvideo);
//        PlayerFactory.setPlayManager(SystemPlayerManager.class);
//        MobileVideoView=findViewById(R.id.video_player);
//
//
//        initDrmPlayer();
//
//        initVideo();
//        int position=getIntent().getIntExtra("position",0);
//        ArrayList<playUrlbean> arrayList= (ArrayList<playUrlbean>) aCache.getAsObject("Videolist");
//        List<GSYVideoModel> urls = new ArrayList<>();
//
//        urls.add(new GSYVideoModel("http://220.113.14.11:800/cmcc/demo2.m3u8", "标题6"));
//        MobileVideoView.setUp(urls, true, 0);
//
////        //增加封面
////        ImageView imageView = new ImageView(this);
////        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
////        imageView.setImageResource(R.mipmap.xxx1);
////        detailPlayer.setThumbImageView(imageView);
//
//        resolveNormalVideoUI();
//        MobileVideoView.setIsTouchWiget(true);
//        //关闭自动旋转
//        MobileVideoView.setRotateViewAuto(false);
//        MobileVideoView.setLockLand(true);//一全屏就横屏锁定
//        MobileVideoView.setNeedLockFull(true);//全屏锁屏
//        MobileVideoView.setShowFullAnimation(false);
//        MobileVideoView.setAutoFullWithSize(true);
//        MobileVideoView.setVideoAllCallBack(this);
//        //设置返回按键功能
//        MobileVideoView.getBackButton().setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onBackPressed();
//            }
//        });
//        MobileVideoView.setLockClickListener(new LockClickListener() {
//            @Override
//            public void onClick(View view, boolean lock) {
//                if (orientationUtils != null) {
//                    //配合下方的onConfigurationChanged
//                    orientationUtils.setEnable(!lock);
//                }
//            }
//        });
//        //下一个
////        next.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                ((MobileVideoView) orientationUtils.getCurrentPlayer()).playNext();
////            }
////        });
//    }
//
//    /**
//     * 初始化
//     */
//    private void initDrmPlayer() {
//        String savePath = getFilesDir() + "";
//        copyJsonFromAsset(PlayVideoActvity.this, "msg.json", savePath, "msg.json");
//        /*
//         * 实例化对象
//         */
//        mUDRM = new UDRM(this);
//
//
//    }
//
//    /**
//     * 把assert中的json copy到制定目录下
//     * * @param context
//     * @param ASSETNAME
//     * @param savePath
//     * @param saveName
//     */
//    public void copyJsonFromAsset(Context context, String ASSETNAME, String savePath, String saveName) {
//        String fileName = savePath + "/" + saveName;
//        File dir = new File(savePath);
//        if (!dir.exists())
//            dir.mkdir();
//        if (!(new File(fileName)).exists()) {
//            InputStream is;
//            try {
//                is = context.getResources().getAssets().open(ASSETNAME);
//                FileOutputStream fos = new FileOutputStream(fileName);
//                byte[] buffer = new byte[1024];
//                int count = 0;
//                while ((count = is.read(buffer)) > 0) {
//                    fos.write(buffer, 0, count);
//                }
//                fos.close();
//                is.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//        }
//    }
//
//
//
//
//
//
//    private void resolveNormalVideoUI() {
//        //增加title
//        MobileVideoView.getTitleTextView().setVisibility(View.VISIBLE);
//        MobileVideoView.getBackButton().setVisibility(View.VISIBLE);
//    }
//    @Override
//    public MobileVideoView getGSYVideoPlayer() {
//         return MobileVideoView;
//    }
//
//    @Override
//    public GSYVideoOptionBuilder getGSYVideoOptionBuilder() {
//        return null;
//    }
//
//    @Override
//    public void clickForFullScreen() {
//
//    }
//
//    @Override
//    public boolean getDetailOrientationRotateAuto() {
//        return false;
//    }
//
//    @Override
//    public void onEnterFullscreen(String url, Object... objects) {
//        super.onEnterFullscreen(url, objects);
//        //隐藏调全屏对象的返回按键
//        GSYVideoPlayer gsyVideoPlayer = (GSYVideoPlayer) objects[1];
//        gsyVideoPlayer.getBackButton().setVisibility(View.VISIBLE);
//    }
//    private GSYVideoPlayer getCurPlay() {
//        if (MobileVideoView.getFullWindowPlayer() != null) {
//            return MobileVideoView.getFullWindowPlayer();
//        }
//        return MobileVideoView;
//    }
//
//    //从本地拿到想要的数据
//    private String getMessage(String msgName) {
//        String msg = null;
//        String programStr = getPlayUrl(sdPath, "msg.json");
//        JSONObject jsonObj;
//        try {
//            jsonObj = new JSONObject(programStr);
//            if (jsonObj.has(msgName))
//                msg = jsonObj.getString(msgName);
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        return msg;
//    }
//
//    /**
//     * 从本地assets中得到里面的字符串
//     * @param path
//     * @param fileName
//     * @return
//     */
//    public static String getPlayUrl(String path, String fileName) {
//        String url = null;
//
//        String pathLast = path + File.separator + fileName;
//        FileInputStream inStream = null;
//        try {
//            inStream = new FileInputStream(pathLast);
//        } catch (FileNotFoundException e1) {
//            // TODO Auto-generated catch block
//            e1.printStackTrace();
//        }
//        ByteArrayOutputStream stream = new ByteArrayOutputStream();
//        byte[] buffer = new byte[2048];
//        int length = -1;
//        if (inStream != null) {
//            try {
//                while ((length = inStream.read(buffer)) != -1) {
//                    stream.write(buffer, 0, length);
//                }
//
//                url = stream.toString();
//                stream.close();
//                inStream.close();
//            } catch (IOException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//
//        }
//        url = replaceBlank(url);
//
//        return url;
//    }
//
//    /**
//     * 格式化字符串，去掉中文双引号
//     * @param str
//     * @return
//     */
//    private static String replaceBlank(String str) {
//        String des = "";
//        if (str != null && !str.equals("")) {
//            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
//            Matcher m = p.matcher(str);
//            des = m.replaceAll("");
//        } else {
//        }
//        return des;
//    }
//
//
//}
