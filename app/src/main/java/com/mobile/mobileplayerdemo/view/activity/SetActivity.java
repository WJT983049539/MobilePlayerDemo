package com.mobile.mobileplayerdemo.view.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.mobile.mobileplayerdemo.tools.ACache;
import com.mobile.mobileplayerdemo.tools.GlobalToast;
import com.mobile.mobileplayerdemo.tools.LogUtils;
import com.mobile.mobileplayerdemo.R;
import com.mobile.mobileplayerdemo.presenter.adapter.SetListAdapter;
import com.mobile.mobileplayerdemo.model.bean.SetInfoBean;
import com.mobile.mobileplayerdemo.model.bean.playUrlbean;
import com.mobile.mobileplayerdemo.view.customview.OnDialog;
import com.mobile.mobileplayerdemo.view.customview.ServiceEidtDialog;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.unitend.udrm.util.UDRM;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 设置页面
 */
public class SetActivity extends AppCompatActivity {
    private ImageView set_back;
    private RecyclerView set_re;
    private List<SetInfoBean> list=new ArrayList<SetInfoBean>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_acticity_set);
        set_back=findViewById(R.id.set_back);
        set_re=findViewById(R.id.set_re);
        set_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetActivity.this.finish();
            }
        });

        RecyclerView.LayoutManager manager=new LinearLayoutManager(this);
        set_re.setLayoutManager(manager);
//        DividerItemDecoration divider = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
//        set_re.addItemDecoration(divider);//添加分割线
        initdate();
        SetListAdapter setListAdapter=new SetListAdapter(list,this);
        set_re.setAdapter(setListAdapter);
        /**
         * 列表点击事件
         */
        setListAdapter.setOnItemClisten(new SetListAdapter.OnItemclicklisten() {
            @Override
            public void onItemclickListen(int postion) {
                if(list.size()!=0){
                    if(list.get(postion).getSetInfo().equals("清除历史记录")){
                        ACache aCache=ACache.get(SetActivity.this);
                        ArrayList<playUrlbean>videolist=new ArrayList<playUrlbean>();
                        aCache.put("Videolist",videolist);
                        GlobalToast.show("清除历史记录成功",Toast.LENGTH_SHORT);
                    }else if(list.get(postion).getSetInfo().equals("DRMservice设置")){
                        LogUtils.i("进入DRMservice设置");
                        //添加视频
                        final ServiceEidtDialog customEidtDialog=new ServiceEidtDialog(SetActivity.this);
                        customEidtDialog.setYesOnclickListener("确定", new ServiceEidtDialog.onYesOnclickListener() {
                            @Override
                            public void onYesOnclick(String message, String message2) {
                                /**
                                 * 把drm service ip 和operator 保存到共享参数里面
                                 */
                                SharedPreferences preferences = getSharedPreferences("DrmUrl", Context.MODE_PRIVATE);
                                SharedPreferences.Editor edit= preferences.edit();
                                if (message != null && !"".equals(message)) {
                                    if (message.endsWith(":443")) {
                                        message = message.substring(0, message.indexOf(":443"));
                                    }
                                } else {
                                    String ip = getMessage("IP");
                                    if (ip != null && !ip.equals("")) {
                                        if (ip.endsWith(":443")) {
                                            String ipTmp = ip.substring(0, ip.indexOf(":443"));
                                            ip = ipTmp;
                                        }
                                    }
                                    message=ip;
                                }
                                if ((!("").equals(message2)) && (message2 != null)) {
                                    edit.putString("operator", message2);
                                    edit.commit();
                                } else {
                                    message2 = getMessage("operator");
                                    if (message2 != null && !message2.equals("")){
                                        edit.putString("operator", message2);
                                        edit.commit();
                                    }

                                }

                                edit.putString("drmUrl", message);
                                edit.commit();
                                customEidtDialog.cancel();
                            }
                        });
                        customEidtDialog.setNoOnclickListener("取消", new ServiceEidtDialog.onNoOnclickListener() {
                            @Override
                            public void onNoClick() {
                                customEidtDialog.cancel();
                            }
                        });

                        customEidtDialog.show();

                    }
                   else if(list.get(postion).getSetInfo().equals("清除缓存")){
                        GSYVideoManager.instance().clearAllDefaultCache(SetActivity.this);
                        GlobalToast.show("清除视频缓存成功",Toast.LENGTH_SHORT);
                        //清除所有的缓存

                    }
                }
            }
        });
    }

    private void initdate() {
        list.clear();
        SetInfoBean setInfoBean=new SetInfoBean();
        setInfoBean.setSetInfo("DRMservice设置");
        setInfoBean.setType(1);
        list.add(setInfoBean);

        SetInfoBean setInfoBean2=new SetInfoBean();
        setInfoBean2.setSetInfo("DRM服务开关");
        setInfoBean2.setType(2);
        list.add(setInfoBean2);

        SetInfoBean setInfoBean3=new SetInfoBean();
        setInfoBean3.setSetInfo("清除历史记录");
        setInfoBean3.setType(1);
        list.add(setInfoBean3);
        SetInfoBean setInfoBean4=new SetInfoBean();
        setInfoBean4.setSetInfo("清除缓存");
        setInfoBean4.setType(1);
        list.add(setInfoBean4);





    }
    private String getMessage(String msgName) {
        String msg = null;
         String sdPath = getFilesDir() + "";
        String programStr = getPlayUrl(sdPath, "msg.json");
        JSONObject jsonObj;
        try {
            jsonObj = new JSONObject(programStr);
            if (jsonObj.has(msgName))
                msg = jsonObj.getString(msgName);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return msg;
    }

    public static String getPlayUrl(String path, String fileName) {
        String url = null;

        String pathLast = path + File.separator + fileName;
        FileInputStream inStream = null;
        try {
            inStream = new FileInputStream(pathLast);
        } catch (FileNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        byte[] buffer = new byte[2048];
        int length = -1;
        if (inStream != null) {
            try {
                while ((length = inStream.read(buffer)) != -1) {
                    stream.write(buffer, 0, length);
                }

                url = stream.toString();
                stream.close();
                inStream.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
        url = replaceBlank(url);

        return url;
    }
    private static String replaceBlank(String str) {
        String des = "";
        if (str != null && !str.equals("")) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            des = m.replaceAll("");
            //Log.i(TAG, "replaceBlank des  : " + des);
        }
        return des;
    }
}
