package com.mobile.mobileplayerdemo.view.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.callback.ItemDragAndSwipeCallback;
import com.mobile.mobileplayerdemo.tools.ACache;
import com.mobile.mobileplayerdemo.view.customview.CustomEidtDialog;
import com.mobile.mobileplayerdemo.tools.LogUtils;
import com.mobile.mobileplayerdemo.R;
import com.mobile.mobileplayerdemo.presenter.adapter.VideoPathListAdapter;
import com.mobile.mobileplayerdemo.model.bean.playUrlbean;
import com.mobile.mobileplayerdemo.view.customview.OnDialog;
import com.mobile.mobileplayerdemo.view.customview.WrningEidtDialog;
import com.qw.soul.permission.SoulPermission;
import com.qw.soul.permission.bean.Permission;
import com.qw.soul.permission.bean.Permissions;
import com.qw.soul.permission.callbcak.CheckRequestPermissionsListener;
import com.qw.soul.permission.callbcak.GoAppDetailCallBack;
import com.shuyu.gsyvideoplayer.player.IjkPlayerManager;
import com.shuyu.gsyvideoplayer.player.PlayerFactory;
import com.shuyu.gsyvideoplayer.player.SystemPlayerManager;
import com.unitend.udrm.util.UDRM;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private SwipeRefreshLayout swipeRefreshLayout;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private VideoPathListAdapter videoPathListAdapter;
    private ArrayList<playUrlbean>list;
    private SharedPreferences preferences = null;
    private SharedPreferences.Editor editor;
    private ACache aCache;
    private TextView point;
    private final String drminfo = "METHOD=SAMPLE-AES,VDECFORMAT=h264-f,KEYFORMATVERSIONS=1,KEYFORMAT=chinadrm,OPERATOR=voole,MIMETYPE=application/vnd.unitend.drm,IV=000102030405060708090A0B0C0D0E0F,CONTENTID=TC_00001,IP=10.110.95.118,PORT=8080,URI=udrm://10.110.95.118:443/udrmservice/services/UdrmSysWS.UdrmSysWSHttpSoap12Endpoint/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (getSupportActionBar()!=null) {
            getSupportActionBar().hide();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        list=new ArrayList<playUrlbean>();
        initView();
    }
    private void initView() {
        point=findViewById(R.id.point);
        String savePath = getFilesDir() + "";
        copyJsonFromAsset(MainActivity.this, "msg.json", savePath, "msg.json");
        preferences = getSharedPreferences("DrmUrl", Context.MODE_PRIVATE);
        editor = preferences.edit();
        initData();
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            requestPremiss();
        };

        swipeRefreshLayout=findViewById(R.id.swipeRefreshLayout);
        recyclerView=findViewById(R.id.main_recycler);

        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration divider = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(divider);//添加分割线
        videoPathListAdapter=new VideoPathListAdapter(R.layout.main_item_vodeopath,list,MainActivity.this);
        ItemDragAndSwipeCallback itemDragAndSwipeCallback = new ItemDragAndSwipeCallback(videoPathListAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemDragAndSwipeCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
//        // 开启滑动删除
//        videoPathListAdapter.enableSwipeItem();

        recyclerView.setAdapter(  videoPathListAdapter);

        /*
        列表点击事件
         */
        videoPathListAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

//                //列表播放
//               Intent intent=new Intent(MainActivity.this,PlayVideoActvity.class);
//                if(list.size()!=0){
//                    intent.putExtra("position",position);
//                }
//                startActivity(intent);

                /**
                 * 这是单个视频的时候
                 */
                Intent intent=new Intent(MainActivity.this, SingPlayerActivity.class);
                if(list.size()!=0){
                    intent.putExtra("path",list.get(position));
                }
                startActivity(intent);

            }


        });

        //上拉刷新，加载更多
        videoPathListAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                recyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //模拟数据
//                        mData = new ArrayList(data);
                        //更新数据
//                        mAdapter.setNewData(mData);
                        //刷新完成取消刷新动画
                        //刷新完成重新开启加载更多
                        videoPathListAdapter.loadMoreEnd();
//                        videoPathListAdapter.setEnableLoadMore(true);
                    }
                }, 1000);
            }
        },recyclerView);
        //下拉刷新
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //刷新的时候禁止加载更多
                videoPathListAdapter.setEnableLoadMore(false);
                recyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //模拟数据
                        //更新数据
//                        VideoPathBean videoPathBean=new VideoPathBean();
//                        videoPathBean.setPath("http://devimages.apple.com.edgekey.net/streaming/examples/bipbop_4x3/bipbop_4x3_variant.m3u8");
//                        list.add(0,videoPathBean);
//                        videoPathListAdapter.setNewData(list);
////                        videoPathListAdapter.notifyDataSetChanged();
//                        //刷新完成取消刷新动画
                        swipeRefreshLayout.setRefreshing(false);
                        //刷新完成重新开启加载更多
                        videoPathListAdapter.setEnableLoadMore(true);
                    }
                }, 1000);
            }
        });
        //设置menu
        toolbar=findViewById(R.id.main_toolsbar);
        setSupportActionBar(toolbar);
//        toolbar.setOverflowIcon(getResources().getDrawable(R.drawable.menu2));
        toolbar.inflateMenu(R.menu.toolbar_menu);
        // TODO: 2019/11/15  
        toolbar.findViewById(R.id.group).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            //点击转为表格列表
            }
        });
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.action_add:
                        //添加视频url
                        final CustomEidtDialog customEidtDialog=new CustomEidtDialog(MainActivity.this);
                        customEidtDialog.setNoOnclickListener("取消",new CustomEidtDialog.onNoOnclickListener() {
                            @Override
                            public void onNoClick() {
                                customEidtDialog.cancel();
                            }
                        });
                        customEidtDialog.setYesOnclickListener("确定", new CustomEidtDialog.onYesOnclickListener() {
                            @Override
                            public void onYesOnclick(String message, String message2) {

//                                    String reg = "(mp4|flv|avi|rm|rmvb|wmv|m3u8)";
//                                    Pattern p = Pattern.compile(reg);
//                                    boolean boo = p.matcher("url" ).find();
                                    if(message.equals("")||message2.equals("")||message==null&&message2==null){
                                        Toast.makeText(MainActivity.this,"节目地址或节目Id不能为空！请重新输入",Toast.LENGTH_LONG).show();
                                    }else{
                                    boolean boo= isVideo(message);
                                    if(boo){
                                         playUrlbean videoPathBean=new playUrlbean();
                                        videoPathBean.setPlayurl(message);
                                        videoPathBean.setContentId(message2);
                                        list.add(0,videoPathBean);
                                        aCache.put("Videolist",list);//把数据视频列表数据保存到缓存中
                                        videoPathListAdapter.notifyDataSetChanged();
                                        point.setVisibility(View.GONE);
                                        customEidtDialog.cancel();
                                    }else{
                                        Toast.makeText(MainActivity.this,"不是一个正确的视频地址,请重新输入",Toast.LENGTH_LONG).show();
                                    }
                                };
                            }
                        });
                        customEidtDialog.show();
                        break;
                        //设置页面
                    case R.id.action_set:
                            Intent intent=new Intent(MainActivity.this,SetActivity.class);
                            startActivity(intent);
                        break;
                    /**
                     * 关于
                     */
                    case R.id.guanyu:
                        OnDialog onDialog=new OnDialog(MainActivity.this);
                        UDRM mUDRM=new UDRM(MainActivity.this);
                        String version=mUDRM.getUdrmVersion();
                        onDialog.show();
                        onDialog.SetVersion(version);
                        break;
                }
                return false;
            }
        });
    }

    /**
     * 请求权限
     */
    private void requestPremiss() {

        SoulPermission.getInstance().checkAndRequestPermissions(
                Permissions.build(
                        Manifest.permission.INTERNET,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                ),
                //if you want do noting or no need all the callbacks you may use SimplePermissionsAdapter instead
                new CheckRequestPermissionsListener() {
                    @Override
                    public void onAllPermissionOk(Permission[] allPermissions) {
                        Toast.makeText(MainActivity.this, allPermissions.length + "权限获取成功", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionDenied(final Permission[] refusedPermissions) {
                        Toast.makeText(MainActivity.this, refusedPermissions[0].toString() +
                                " 权限获取失败", Toast.LENGTH_SHORT).show();

                        new AlertDialog.Builder(MainActivity.this).setTitle("提示").setMessage("如果你拒绝了权限,应用中的一些功能将不能正常使用")
                                .setPositiveButton("授予权限", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //用户点击以后
                                        boolean ff=PanduanIsProhibitedPermissionDenied(refusedPermissions);
                                        if(!ff){
                                            SoulPermission.getInstance().goApplicationSettings(new GoAppDetailCallBack() {
                                                @Override
                                                public void onBackFromAppDetail(Intent data) {
                                                    Log.i("test","这里是在设置也手动获取到权限以后返回，回调");
                                                }
                                            });
                                        }else{
                                            requestPremiss();
                                        }
                                    }
                                }).create().show();
                    }
                });
    }

    private void initData() {
//        PlayerFactory.setPlayManager(SystemPlayerManager.class);//默认原生播放器
        PlayerFactory.setPlayManager(IjkPlayerManager.class);//ijk模式
        LogUtils.i("初始化数据");
        aCache=ACache.get(this);
        ArrayList<playUrlbean> arrayList= (ArrayList<playUrlbean>) aCache.getAsObject("Videolist");
        if(arrayList==null||arrayList.size()==0){
            LogUtils.i("缓存数据为空");
            //没有缓存，是空的
//            list=new ArrayList<playUrlbean>();
//            playUrlbean videoPathBean2=new playUrlbean();
//            videoPathBean2.setPlayurl("http://10.2.40.100:10080/vod/demo2/enc/demo2.m3u8");
//            videoPathBean2.setContentId("20191025");
//            list.add(videoPathBean2);
//            playUrlbean videoPathBean3=new playUrlbean();
//            videoPathBean3.setPlayurl("http://10.2.40.100:10080/vod/demo2/demo2.m3u8");
//            videoPathBean3.setContentId("20191025");
//            list.add(videoPathBean3);
//            //使列表不重复
//            list=new ArrayList<playUrlbean>(new HashSet<>(list));
            point.setVisibility(View.VISIBLE);
//            aCache.put("Videolist",list);

        }else {
            list=new ArrayList<playUrlbean>(new HashSet<>(arrayList));
//            list=arrayList;
            point.setVisibility(View.GONE);
        }
    }



    private Boolean PanduanIsProhibitedPermissionDenied(Permission[] refusedPermissions) {
        boolean flag=true;
        for(int i=0;i<refusedPermissions.length;i++){
            if(!refusedPermissions[i].shouldRationale()){
                flag=false;
                return flag;
            }
        }
        return flag;

    }



    //重写onCreateOptionsMenu 使menu显示出来

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu,menu);
        return true;
    }
//把assets里面的固定msg转到sd目录下
public void copyJsonFromAsset(Context context, String ASSETNAME,
                              String savePath, String saveName) {
    String fileName = savePath + "/" + saveName;
    File dir = new File(savePath);
    if (!dir.exists())
        dir.mkdir();
    if (!(new File(fileName)).exists()) {
        InputStream is;
        try {
            is = context.getResources().getAssets().open(ASSETNAME);
            FileOutputStream fos = new FileOutputStream(fileName);
            byte[] buffer = new byte[1024];
            int count = 0;
            while ((count = is.read(buffer)) > 0) {
                fos.write(buffer, 0, count);

            }
            fos.close();
            is.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}



    //根据url来判断是否是视频文件
    public boolean isVideo(String url)
    {
        Pattern p = Pattern.compile("\\s*|\t|\r|\n");
        Matcher m = p.matcher(url);
        url = m.replaceAll("");
        url=url.trim();
        //最后一个.的下标
        int x = url.lastIndexOf(".");
        if(x==-1){
            return false;
        }
            //获取url的后缀，即链接的类型
        String videoType = url.substring(x);
            //所有类型视频:MP4、3GP、AVI、MKV、WMV、MPG、VOB、FLV、SWF、MOV。这里只是判断一部分
        //有的文件后缀大小写不唯一，所以比较时忽略大小写
        if(videoType.equalsIgnoreCase(".mp4") || videoType.equalsIgnoreCase(".3gp")
                || videoType.equalsIgnoreCase(".AVI") || videoType.equalsIgnoreCase(".WMV")
                || videoType.equalsIgnoreCase(".rmvb") || videoType.equalsIgnoreCase(".flv")
                || videoType.equalsIgnoreCase(".m3u8") )
            return true;
        return false;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        aCache=ACache.get(this);
        ArrayList<playUrlbean> arrayList= (ArrayList<playUrlbean>) aCache.getAsObject("Videolist");
        list.clear();
        if(arrayList==null){
            ArrayList<playUrlbean> arrayList2=new ArrayList<playUrlbean>();
            list.addAll(arrayList2);
        }else {
            list.addAll(arrayList);
        }

        if(videoPathListAdapter!=null){
            videoPathListAdapter.notifyDataSetChanged();

        }
        if(list.size()==0){
            point.setVisibility(View.VISIBLE);
        }else {
            point.setVisibility(View.GONE);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:

                final WrningEidtDialog wrningEidtDialog = new WrningEidtDialog(this);
            wrningEidtDialog.show();
            wrningEidtDialog.setMEssage("是否退出播放器?");
            wrningEidtDialog.setYesOnclickListener("确定", new WrningEidtDialog.onYesOnclickListener() {
                @Override
                public void onYesOnclick() {
                    wrningEidtDialog.cancel();
                    MainActivity.this.finish();
                }

            });
            wrningEidtDialog.setNoOnclickListener("取消", new WrningEidtDialog.onNoOnclickListener() {
                @Override
                public void onNoClick() {
                    wrningEidtDialog.cancel();
                }
            });

            return false;   //这里由于break会退出，所以我们自己要处理掉 不返回上一层
        }
        return super.onKeyDown(keyCode, event);

    }
}
