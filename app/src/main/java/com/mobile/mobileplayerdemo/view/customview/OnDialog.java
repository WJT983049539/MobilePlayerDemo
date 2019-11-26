package com.mobile.mobileplayerdemo.view.customview;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.SpannableString;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.mobile.mobileplayerdemo.R;

import androidx.annotation.NonNull;

/**
 * @auther wjt
 * @date 2019/5/14
 */
public class OnDialog extends Dialog

{
    private Button yes;//确定按钮
    private Button no;//取消按钮
    private TextView titleTV;//消息标题文本
    private EditText message;//ip
    private EditText message2;//operator
    private String titleStr;//从外界设置的title文本
    private String messageStr;//从外界设置的消息文本

    //确定文本和取消文本的显示的内容
    private String yesStr, noStr;
    private TextView versioncode;


    public OnDialog(@NonNull Context context) {
        super(context, R.style.MyDialog);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.on_dialog);
        //空白处不能取消动画
        setCanceledOnTouchOutside(true);

        //初始化界面控件
        initView();


    }


    /**
     * 初始化界面控件
     */
    private void initView() {
        versioncode=findViewById(R.id.versioncode);
    }

    public void SetVersion(String version){
        if(versioncode!=null){
            versioncode.setText(version);
        }
    }


}

