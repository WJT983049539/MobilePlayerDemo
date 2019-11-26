package com.mobile.mobileplayerdemo.presenter.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.mobile.mobileplayerdemo.R;
import com.mobile.mobileplayerdemo.model.bean.SetInfoBean;
import com.mobile.mobileplayerdemo.tools.LogUtils;
import com.mobile.mobileplayerdemo.view.activity.SetActivity;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SetListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<SetInfoBean> list;
    private SetActivity setActivity;
    private static final int TYPE_NORMAR = 1;
    private static final int TYPE_SWITH= 2;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    public SetListAdapter(List<SetInfoBean> list, SetActivity setActivity) {
        this.list=list;
        this.setActivity=setActivity;
        //专门保存设置信息
        sharedPreferences= setActivity.getSharedPreferences("playerSetInfo", Context.MODE_PRIVATE);
        editor=sharedPreferences.edit();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if(viewType==TYPE_NORMAR){
           return new Holder(LayoutInflater.from(setActivity).inflate(R.layout.set_item_layout,parent,false));
        }else if(viewType==TYPE_SWITH){
            return new Holderswitch(LayoutInflater.from(setActivity).inflate(R.layout.set_item_switch_layout,parent,false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(getItemViewType(position)==TYPE_NORMAR){
            setHolder((Holder)holder,position);
        }else if(getItemViewType(position)==TYPE_SWITH){
            setSwitchHolder((Holderswitch)holder,position);
        }
    }

    private void setSwitchHolder(Holderswitch holder, int position) {
        holder.switch_text.setText(list.get(position).getSetInfo());

    }

    @Override
    public int getItemViewType(int position) {
        if(list.get(position).getType()==1){
            return TYPE_NORMAR;
        }else if(list.get(position).getType()==2){
            return TYPE_SWITH;
        }
        return 0;
    }


    //第一种
    private void setHolder(Holder holder, int position) {
        holder.onbind(position);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface OnItemclicklisten {
       void onItemclickListen(int postion);
    }
    public OnItemclicklisten onItemClisten;
    public void setOnItemClisten( OnItemclicklisten onItemClistens){
        onItemClisten=onItemClistens;
    }
    public class Holder extends RecyclerView.ViewHolder {
        TextView textView;
        public Holder(@NonNull View itemView) {
            super(itemView);
            textView=itemView.findViewById(R.id.set_item_tx);
        }

        public void onbind(final int position) {
            textView.setText(list.get(position).getSetInfo());
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(onItemClisten!=null){
                        onItemClisten.onItemclickListen(position);
                    }
                }
            });
        }
    }

    private class Holderswitch extends RecyclerView.ViewHolder {

        private TextView switch_text;
        private Switch switchx;
        public Holderswitch(@NonNull View itemView) {
            super(itemView);
            switch_text=itemView.findViewById(R.id.switch_text);
            switchx=itemView.findViewById(R.id.switchx);
            Boolean drmswitch=sharedPreferences.getBoolean("DrmSwitch",true);
            switchx.setChecked(drmswitch);
            switchx.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    editor.putBoolean("DrmSwitch",isChecked);//存到共享参数
                    editor.commit();
                    LogUtils.i("设置解密服务开关为:"+isChecked);
                }
            });
        }
    }
}
