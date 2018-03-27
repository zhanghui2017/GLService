package com.gengli.glservice.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.gengli.glservice.R;

public class UpdatePopu extends PopupWindow {
    private View conentView;
    private TextView bt1;
    private TextView bt2;
    private TextView pop_update_version_name;
    private TextView pop_update_content_text;


    public UpdatePopu(final Activity context, View.OnClickListener listener) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        conentView = inflater.inflate(R.layout.popup_update, null);

        bt1 = (TextView) conentView.findViewById(R.id.pop_update_bt1);
        bt2 = (TextView) conentView.findViewById(R.id.pop_update_bt2);
        bt1.setOnClickListener(listener);
        bt2.setOnClickListener(listener);
        pop_update_content_text = (TextView) conentView.findViewById(R.id.pop_update_content_text);
        pop_update_version_name = (TextView) conentView.findViewById(R.id.pop_update_version_name);

        this.setContentView(conentView);
        //设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        //设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        //设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        //设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.AnimBottom);
        //实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        //设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
    }

    public void setVersionText(String str) {
        pop_update_version_name.setText(str);
    }

    public void setContentText(String str) {
        pop_update_content_text.setText(str);
    }

    /**
     * 显示popupWindow
     *
     * @param parent
     */
    public void showPopupWindow(View parent) {
        if (!this.isShowing()) {
            this.showAtLocation(parent, Gravity.CENTER, 0, 0);
        } else {
            this.dismiss();
        }
    }


}



