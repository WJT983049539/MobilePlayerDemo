package com.mobile.mobileplayerdemo;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.shuyu.gsyvideoplayer.model.GSYVideoModel;
import com.shuyu.gsyvideoplayer.video.ListGSYVideoPlayer;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;
import com.shuyu.gsyvideoplayer.video.base.GSYBaseVideoPlayer;
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import moe.codeest.enviews.ENDownloadView;

/**
 * 根据GSYvideoPlayer写的自定义播放器
 */
public class MobileVideoView extends StandardGSYVideoPlayer {
    //在执行下一集的时候调用
    public interface OnnextPlaylistener{
        void onNext(int position);
    }

    protected List<GSYVideoModel> mUriList = new ArrayList<>();
    protected int mPlayPosition;
     ImageView nextimage;
     ImageView lastimage;

    /**
     * 1.5.0开始加入，如果需要不同布局区分功能，需要重载
     */
    public MobileVideoView(Context context, Boolean fullFlag) {
        super(context, fullFlag);
    }

    public MobileVideoView(Context context) {
        super(context);
    }

    public MobileVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    //这个必须配置最上面的构造才能生效
    @Override
    public int getLayoutId() {
        return R.layout.video_layout_standard2;
    }

    @Override
    protected void init(Context context) {
        super.init(context);
        lastimage=findViewById(R.id.lastimage);
        nextimage = (ImageView) findViewById(R.id.nextimage);
        if (mThumbImageViewLayout != null &&
                (mCurrentState == -1 || mCurrentState == CURRENT_STATE_NORMAL || mCurrentState == CURRENT_STATE_ERROR)) {
            nextimage.setVisibility(VISIBLE);
            lastimage.setVisibility(VISIBLE);
            nextimage.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    playNext();
                }
            });
            lastimage.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    playLast();
                }
            });
        }
    }


    /**
     * 设置播放URL
     *
     * @param url           播放url
     * @param position      需要播放的位置
     * @param cacheWithPlay 是否边播边缓存
     * @return
     */
    public boolean setUp(List<GSYVideoModel> url, boolean cacheWithPlay, int position) {
        return setUp(url, cacheWithPlay, position, null, new HashMap<String, String>());
    }

    /**
     * 设置播放URL
     *
     * @param url           播放url
     * @param cacheWithPlay 是否边播边缓存
     * @param position      需要播放的位置
     * @param cachePath     缓存路径，如果是M3U8或者HLS，请设置为false
     * @return
     */
    public boolean setUp(List<GSYVideoModel> url, boolean cacheWithPlay, int position, File cachePath) {
        return setUp(url, cacheWithPlay, position, cachePath, new HashMap<String, String>());
    }

    /**
     * 设置播放URL
     *
     * @param url           播放url
     * @param cacheWithPlay 是否边播边缓存
     * @param position      需要播放的位置
     * @param cachePath     缓存路径，如果是M3U8或者HLS，请设置为false
     * @param mapHeadData   http header
     * @return
     */
    public boolean setUp(List<GSYVideoModel> url, boolean cacheWithPlay, int position, File cachePath, Map<String, String> mapHeadData) {
        return setUp(url, cacheWithPlay, position, cachePath, mapHeadData, true);
    }

    /**
     * 设置播放URL
     *
     * @param url           播放url
     * @param cacheWithPlay 是否边播边缓存
     * @param position      需要播放的位置
     * @param cachePath     缓存路径，如果是M3U8或者HLS，请设置为false
     * @param mapHeadData   http header
     * @param changeState   切换的时候释放surface
     * @return
     */
    protected boolean setUp(List<GSYVideoModel> url, boolean cacheWithPlay, int position, File cachePath, Map<String, String> mapHeadData, boolean changeState) {
        mUriList = url;
        mPlayPosition = position;
        mMapHeadData = mapHeadData;
        GSYVideoModel gsyVideoModel = url.get(position);
        boolean set = setUp(gsyVideoModel.getUrl(), cacheWithPlay, cachePath, gsyVideoModel.getTitle(), changeState);
        if (!TextUtils.isEmpty(gsyVideoModel.getTitle())) {
            mTitleTextView.setText(gsyVideoModel.getTitle());
        }
        return set;
    }


    @Override
    protected void cloneParams(GSYBaseVideoPlayer from, GSYBaseVideoPlayer to) {
        super.cloneParams(from, to);
        MobileVideoView sf = (MobileVideoView) from;
        MobileVideoView st = (MobileVideoView) to;
        st.mPlayPosition = sf.mPlayPosition;
        st.mUriList = sf.mUriList;
    }

    @Override
    public GSYBaseVideoPlayer startWindowFullscreen(Context context, boolean actionBar, boolean statusBar) {
        GSYBaseVideoPlayer gsyBaseVideoPlayer = super.startWindowFullscreen(context, actionBar, statusBar);
        if (gsyBaseVideoPlayer != null) {
            MobileVideoView listGSYVideoPlayer = (MobileVideoView) gsyBaseVideoPlayer;
            GSYVideoModel gsyVideoModel = mUriList.get(mPlayPosition);
            if (!TextUtils.isEmpty(gsyVideoModel.getTitle())) {
                listGSYVideoPlayer.mTitleTextView.setText(gsyVideoModel.getTitle());
            }
        }
        return gsyBaseVideoPlayer;
    }

    @Override
    protected void resolveNormalVideoShow(View oldF, ViewGroup vp, GSYVideoPlayer gsyVideoPlayer) {
        if (gsyVideoPlayer != null) {
            ListGSYVideoPlayer listGSYVideoPlayer = (ListGSYVideoPlayer) gsyVideoPlayer;
            GSYVideoModel gsyVideoModel = mUriList.get(mPlayPosition);
            if (!TextUtils.isEmpty(gsyVideoModel.getTitle())) {
                mTitleTextView.setText(gsyVideoModel.getTitle());
            }
        }
        super.resolveNormalVideoShow(oldF, vp, gsyVideoPlayer);
    }

    @Override
    public void onCompletion() {
        releaseNetWorkState();
        if (mPlayPosition < (mUriList.size())) {
            return;
        }
        super.onCompletion();
    }

    @Override
    public void onAutoCompletion() {
        if (playNext()) {
            return;
        }
        super.onAutoCompletion();
    }


    /**
     * 开始状态视频播放，prepare时不执行  addTextureView();
     */
    @Override
    protected void prepareVideo() {
        super.prepareVideo();
        if (mHadPlay && mPlayPosition < (mUriList.size())) {
            setViewShowState(mLoadingProgressBar, VISIBLE);
            if (mLoadingProgressBar instanceof ENDownloadView) {
                ((ENDownloadView) mLoadingProgressBar).start();
            }
        }
    }


    @Override
    public void onPrepared() {
        super.onPrepared();
    }





    /**
     * 这使点击暂停之外全部控件隐藏的方法（暂停状态下）
     */
    @Override
    protected void changeUiToPauseClear() {
        super.changeUiToClear();
        setViewShowState(lastimage,GONE);
        setViewShowState(nextimage,GONE);
    }

    /**
     * 这使点击播放按钮之外全部控件隐藏的方法（播放状态下）
     */
    @Override
    protected void changeUiToPlayingClear() {
        super.changeUiToPlayingClear();
        setViewShowState(lastimage,GONE);
        setViewShowState(nextimage,GONE);
    }

    @Override
    protected void changeUiToNormal() {
        super.changeUiToNormal();
        //如果是正在播放或者列表不是最后一个
        if (mHadPlay && mPlayPosition < (mUriList.size())) {
            setViewShowState(mThumbImageViewLayout, GONE);
            setViewShowState(mTopContainer, INVISIBLE);
            setViewShowState(mBottomContainer, INVISIBLE);
            setViewShowState(mStartButton, GONE);
            setViewShowState(mLoadingProgressBar, VISIBLE);
            setViewShowState(mBottomProgressBar, INVISIBLE);
            setViewShowState(mLockScreen, GONE);
            setViewShowState(lastimage,GONE);
            setViewShowState(nextimage,GONE);

            if (mLoadingProgressBar instanceof ENDownloadView) {
                ((ENDownloadView) mLoadingProgressBar).start();
            }
        }
    }



    @Override
    protected void changeUiToPlayingShow() {
        super.changeUiToPlayingShow();
//        setViewShowState(lastimage,VISIBLE);
//        setViewShowState(nextimage,VISIBLE);
    }

    @Override
    protected void changeUiToPauseShow() {
        super.changeUiToPauseShow();
        setViewShowState(lastimage,VISIBLE);
        setViewShowState(nextimage,VISIBLE);
    }

    @Override
    protected void lockTouchLogic() {
        super.lockTouchLogic();
        if (!mLockCurScreen) {
            if (mOrientationUtils != null)
                mOrientationUtils.setEnable(isRotateViewAuto());
        } else {
            setViewShowState(lastimage,GONE);
            setViewShowState(nextimage,GONE);
            if (mOrientationUtils != null)
                mOrientationUtils.setEnable(false);
        }

    }

    /**
     * 播放下一集
     *
     * @return true表示还有下一集
     */
    public boolean playNext() {
        if (mPlayPosition < (mUriList.size() - 1)) {
            mPlayPosition += 1;
            GSYVideoModel gsyVideoModel = mUriList.get(mPlayPosition);
            mSaveChangeViewTIme = 0;
            setUp(mUriList, mCache, mPlayPosition, null, mMapHeadData, false);
            if (!TextUtils.isEmpty(gsyVideoModel.getTitle())) {
                mTitleTextView.setText(gsyVideoModel.getTitle());
            }
            startPlayLogic();
            return true;
        }
        return false;
    }

    /**
     * 播放上一集
     * @return
     */
    public boolean playLast() {
        if (mPlayPosition > 0) {
            mPlayPosition -= 1;
            GSYVideoModel gsyVideoModel = mUriList.get(mPlayPosition);
            mSaveChangeViewTIme = 0;
            setUp(mUriList, mCache, mPlayPosition, null, mMapHeadData, false);
            if (!TextUtils.isEmpty(gsyVideoModel.getTitle())) {
                mTitleTextView.setText(gsyVideoModel.getTitle());
            }
            startPlayLogic();
            return true;
        }
        return false;
    }
    @Override
    public int getEnlargeImageRes() {
        return R.drawable.custom_enlarge;
    }

    @Override
    public int getShrinkImageRes() {
        return R.drawable.custom_shrink;
    }

    @Override
    protected void hideAllWidget() {
        super.hideAllWidget();
        setViewShowState(lastimage,GONE);
        setViewShowState(nextimage,GONE);
    }
}
