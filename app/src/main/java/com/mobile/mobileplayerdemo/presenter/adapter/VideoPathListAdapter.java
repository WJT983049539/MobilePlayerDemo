package com.mobile.mobileplayerdemo.presenter.adapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseItemDraggableAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.mobile.mobileplayerdemo.tools.ACache;
import com.mobile.mobileplayerdemo.R;
import com.mobile.mobileplayerdemo.tools.VideoTools;
import com.mobile.mobileplayerdemo.model.bean.playUrlbean;
import com.mobile.mobileplayerdemo.view.activity.SingPlayerActivity;
import com.mobile.mobileplayerdemo.view.customview.UrlEidtDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 主页视频列表适配器，适配器的作用是把一种想要的数据转换为你想要的格式
 */
public  class VideoPathListAdapter extends BaseItemDraggableAdapter<playUrlbean,BaseViewHolder> {
    private Context context;
    public VideoPathListAdapter(int layoutResId, @Nullable List<playUrlbean> data, Context context) {
        super(R.layout.main_item_vodeopath, data);
        this.context=context;
    }
    @Override
    protected void convert(@NonNull final BaseViewHolder helper, final playUrlbean item) {

        Log.i("video1", helper.getAdapterPosition()+"");
        helper.setText(R.id.item_path,item.getPlayurl());
        ImageView view = helper.getView(R.id.item_video_thumb);
        if(!isVideo(item.getPlayurl())){
            //如果是m3u8格式的那就拿不到帧截图
            Glide.with(context).load(R.drawable.video8).into(view);
        }else{
            new loadBitmapByAsyntack(view, item.getPlayurl(),context).execute(item.getContentId());
        }
        Button btnDelete=helper.getView(R.id.btnDelete);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ACache aCache=ACache.get(context);
                getData().remove(helper.getAdapterPosition());
               ArrayList<playUrlbean> playUrlbeans= (ArrayList<playUrlbean>) getData();
               aCache.put("Videolist",playUrlbeans);//把数据视频列表数据保存到缓存中
                notifyDataSetChanged();
            }
        });
        TextView textView=helper.getView(R.id.item_path);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, SingPlayerActivity.class);
                intent.putExtra("path",item);
                context.startActivity(intent);
            }
        });

        //编辑
        Button btn_update=helper.getView(R.id.btn_update);
        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final UrlEidtDialog urlEidtDialog=new UrlEidtDialog(context);
                urlEidtDialog.show();
                urlEidtDialog.setUrl(item.getPlayurl());
                urlEidtDialog.setContentId(item.getContentId());
                urlEidtDialog.setYesOnclickListener("确定", new UrlEidtDialog.onYesOnclickListener() {
                    @Override
                    public void onYesOnclick(String message, String message2) {
                        if(message!=null&&message2!=null){
                            playUrlbean playUrlbean=new playUrlbean();
                            playUrlbean.setPlayurl(message);
                            playUrlbean.setContentId(message2);
                            getData().set(helper.getAdapterPosition(), playUrlbean);
                            ACache aCache=ACache.get(context);
                            ArrayList<playUrlbean> playUrlbeans= (ArrayList<playUrlbean>) getData();
                            aCache.put("Videolist",playUrlbeans);//把数据视频列表数据保存到缓存中
                            notifyDataSetChanged();
                        }
                        urlEidtDialog.cancel();

                    }
                });
                urlEidtDialog.setNoOnclickListener("取消", new UrlEidtDialog.onNoOnclickListener() {
                    @Override
                    public void onNoClick() {
                        urlEidtDialog.cancel();
                        notifyDataSetChanged();
                    }
                });

            }
        });

    }

    public static Bitmap getNetVideoBitmap(String videoUrl,Context context) {
        Bitmap bitmap = null;
        if(VideoTools.isNetworkConnected(context)&&VideoTools.lastgeshi(videoUrl)){
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            try {
                //根据url获取缩略图
                retriever.setDataSource(videoUrl, new HashMap());
                //获得第一帧图片
                bitmap = retriever.getFrameAtTime();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } finally {
                retriever.release();
            }
        }else{
            return null;
        }
        return bitmap;
    }

    class loadBitmapByAsyntack extends AsyncTask<String,Void,Bitmap>{
        ImageView imageView;String path;Context context;
        loadBitmapByAsyntack(ImageView imageView, String path, Context context){
            this.imageView=imageView;
            this.path=path;
            this.context=context;
        }
        //线程前的操作
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        // 作用：接收线程任务执行结果、将执行结果显示到UI组
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
        if(bitmap!=null){
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.loding);
            requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
            requestOptions.error(R.drawable.video8);
            Glide.with(context).load(bitmap).apply(requestOptions).into(imageView);
        }else{
            Glide.with(context).load(R.drawable.video8).into(imageView);
        }
        }
        // 作用：在主线程 显示线程任务执行的进度
        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
        //作用：接收输入参数、执行任务中的耗时操作、返回 线程任务执行的结果
        @Override
        protected Bitmap doInBackground(String... strings) {
            Bitmap bitmap=getNetVideoBitmap(path,context);
            return bitmap;
        }
    }
    //根据url来判断是否是视频文件
    public boolean isVideo(String url)
    {
        //最后一个.的下标
        int x = url.lastIndexOf(".");
        //获取url的后缀，即链接的类型
        String videoType = url.substring(x);
        //所有类型视频:MP4、3GP、AVI、MKV、WMV、MPG、VOB、FLV、SWF、MOV。这里只是判断一部分
        //有的文件后缀大小写不唯一，所以比较时忽略大小写
        if(videoType.equalsIgnoreCase(".mp4") || videoType.equalsIgnoreCase(".3gp")
                || videoType.equalsIgnoreCase(".AVI") || videoType.equalsIgnoreCase(".WMV")
                || videoType.equalsIgnoreCase(".rmvb") || videoType.equalsIgnoreCase(".flv")){
            return true;
        }

        return false;
    }
}
