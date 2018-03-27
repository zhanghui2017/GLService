package com.gengli.glservice.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.gengli.glservice.R;
import com.gengli.glservice.util.LogUtils;
import com.gengli.glservice.util.PhotoBitmapUtil;
import com.gengli.glservice.util.SystemMsgUtil;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

@ContentView(R.layout.activity_system)
public class SystemActivity extends BaseActivity {

    @ViewInject(R.id.system_version_id)
    private TextView system_version_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String versionName = SystemMsgUtil.getVersionName(this);
        system_version_id.setText("耿力售后 V" + versionName);

    }

    @Event(value = R.id.system_back_bt, type = View.OnClickListener.class)
    private void backClick(View view) {
        finish();
    }


    @Event(value = R.id.system_item_1)
    private void item1Click(View view) {
        LogUtils.showLogD("关于耿力");
        startActivity(new Intent(this, AboutActivity.class).putExtra("web_type", 1));
    }

    @Event(value = R.id.system_item_2)
    private void item2Click(View view) {
        startActivity(new Intent(this, FeedbackActivity.class));
    }

    @Event(value = R.id.system_item_3)
    private void item3Click(View view) {
        PhotoBitmapUtil.DelFilePhoto(this);
        LogUtils.showLogD("清除缓存");
        LogUtils.showCenterToast(this, "缓存清除成功");
    }

    @Event(value = R.id.system_item_4)
    private void item4Click(View view) {
        LogUtils.showLogD("用户协议及隐私政策");
        startActivity(new Intent(this, AboutActivity.class).putExtra("web_type", 2));
    }

}
