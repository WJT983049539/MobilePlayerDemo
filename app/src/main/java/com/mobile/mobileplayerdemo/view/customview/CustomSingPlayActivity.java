package com.mobile.mobileplayerdemo.view.customview;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.mobile.mobileplayerdemo.R;
import com.shuyu.gsyvideoplayer.model.GSYVideoModel;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;
import com.shuyu.gsyvideoplayer.video.base.GSYBaseVideoPlayer;

public class CustomSingPlayActivity extends StandardGSYVideoPlayer {
    private TextView playstaus;
    private ImageView nextimage;
    private ImageView lastimage;
    private ImageView stop;
    public CustomSingPlayActivity(Context context, Boolean fullFlag) {
        super(context, fullFlag);
    }

    public CustomSingPlayActivity(Context context) {
        super(context);
    }

    public CustomSingPlayActivity(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void init(Context context) {
        super.init(context);
        playstaus=findViewById(R.id.playstaus);
        stop=findViewById(R.id.stop);
        nextimage=findViewById(R.id.nextimage);
        lastimage=findViewById(R.id.lastimage);

        setViewShowState(mStartButton, INVISIBLE);//先把开始按钮隐藏了

        nextimage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                playNext();
            }
        });
        lastimage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                playLast();
            }
        });

    }

    @Override
    public int getLayoutId() {
        return R.layout.customsingplayview;

    }

    @Override
    protected void hideAllWidget() {
        super.hideAllWidget();
    }
    public View getStatuView(){
        if(playstaus!=null){
            return playstaus;
        }
        return null;
    }
    public View getStopView(){
        if(stop!=null){
            return stop;
        }
        return null;
    }
    public void setViSIBLE(int viv){
        if(playstaus!=null){
            playstaus.setVisibility(viv);
        }
    }

    @Override
    protected void changeUiToNormal() {
        super.changeUiToNormal();

    }

    //准备播放
    @Override
    protected void changeUiToPreparingShow() {

        super.changeUiToPreparingShow();
    }
    //开始播放之前
    @Override
    protected void changeUiToPlayingShow()
    {

        super.changeUiToPlayingShow();
    }


    @Override
    protected void changeUiToPauseShow() {

        super.changeUiToPauseShow();
    }

    @Override
    protected void changeUiToPlayingBufferingShow() {

        super.changeUiToPlayingBufferingShow();
    }

    @Override
    protected void changeUiToCompleteShow()
    {
        super.changeUiToCompleteShow();
    }

    //加载出错
    @Override
    protected void changeUiToError() {

        super.changeUiToError();
    }

    @Override
    protected void changeUiToPrepareingClear() {

        super.changeUiToPrepareingClear();
    }

    @Override
    protected void changeUiToPlayingClear() {
        super.changeUiToPlayingClear();
    }

    @Override
    protected void changeUiToPauseClear()
    {
        super.changeUiToPauseClear();
    }

    @Override
    protected void changeUiToPlayingBufferingClear() {
        super.changeUiToPlayingBufferingClear();
    }

    @Override
    protected void changeUiToClear() {

        super.changeUiToClear();
    }

    @Override
    protected void changeUiToCompleteClear() {

        super.changeUiToCompleteClear();
    }


//    /**
//     * 播放下一集
//     *
//     * @return true表示还有下一集
//     */
//    public boolean playNext() {
//        if (mPlayPosition < (mUriList.size() - 1)) {
//            mPlayPosition += 1;
//            GSYVideoModel gsyVideoModel = mUriList.get(mPlayPosition);
//            mSaveChangeViewTIme = 0;
//            setUp(mUriList, mCache, mPlayPosition, null, mMapHeadData, false);
//
//            if (!TextUtils.isEmpty(gsyVideoModel.getTitle())) {
//                mTitleTextView.setText(gsyVideoModel.getTitle());
//            }
//            startPlayLogic();
//            return true;
//        }
//        return false;
//    }
//
//    /**
//     * 播放上一集
//     * @return
//     */
//    public boolean playLast() {
//        if (mPlayPosition > 0) {
//            mPlayPosition -= 1;
//            GSYVideoModel gsyVideoModel = mUriList.get(mPlayPosition);
//            mSaveChangeViewTIme = 0;
//            setUp(mUriList, mCache, mPlayPosition, null, mMapHeadData, false);
//            if (!TextUtils.isEmpty(gsyVideoModel.getTitle())) {
//                mTitleTextView.setText(gsyVideoModel.getTitle());
//            }
//            startPlayLogic();
//            return true;
//        }
//        return false;
//    }

    /**
     * 全屏时将对应处理参数逻辑赋给全屏播放器(全屏对象)
     *
     * @param context
     * @param actionBar
     * @param statusBar
     * @return
     */
    @Override
    public GSYBaseVideoPlayer startWindowFullscreen(Context context, boolean actionBar, boolean statusBar) {
//        return super.startWindowFullscreen(context, actionBar, statusBar);
        GSYBaseVideoPlayer gsyBaseVideoPlayer = super.startWindowFullscreen(context, actionBar, statusBar);
        final CustomSingPlayActivity multiSampleVideo = (CustomSingPlayActivity) gsyBaseVideoPlayer;
        multiSampleVideo.getStopView().setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                multiSampleVideo.getCurrentPlayer().release();
            }
        });
        return multiSampleVideo;
    }
    /**
     * 推出全屏时将对应处理参数逻辑返回给非播放器
     *
     * @param gsyVideoPlayer
     */
    @Override
    protected void resolveFullVideoShow(Context context, GSYBaseVideoPlayer gsyVideoPlayer, FrameLayout frameLayout) {
        super.resolveFullVideoShow(context, gsyVideoPlayer, frameLayout);
    }
}
