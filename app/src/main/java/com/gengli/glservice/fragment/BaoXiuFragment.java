package com.gengli.glservice.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.gengli.glservice.R;
import com.gengli.glservice.util.DatasUtil;
import com.sobot.chat.SobotApi;
import com.sobot.chat.api.enumtype.SobotChatTitleDisplayMode;
import com.sobot.chat.api.model.Information;
import com.sobot.chat.utils.ToastUtil;
import com.sobot.chat.utils.ZhiChiConstant;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;

@ContentView(R.layout.fragment_baoxiu)
public class BaoXiuFragment extends BaseFragment {
    private Information info;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        info = new Information();
        regReceiver();
    }

    @Event(value = R.id.main_close_baoxiu_bt, type = View.OnClickListener.class)
    private void closeClick(View view) {

    }

    @Event(value = R.id.main_main_voice_bt)
    private void voiceClick(View view) {
        callPhone("037960118689");
    }


    @Event(value = R.id.main_main_service_bt)
    private void serviceClick(View view) {
        if (DatasUtil.isLogin(getActivity())) {
            String username = DatasUtil.getUserInfo(getActivity(), "username");
            String realName = DatasUtil.getUserInfo(getActivity(), "realName");
            String mobile = DatasUtil.getUserInfo(getActivity(), "mobile");
            info.setUseRobotVoice(false);//这个属性默认都是false。想使用需要付费。付费才可以设置为true。
            //启动参数设置开始
            info.setUid(username);
            info.setUname(username);
            info.setRealname(realName);
            info.setTel(mobile);
            String appkey = "f545d59e86c74c08af86f819b0edce93";
            if (!TextUtils.isEmpty(appkey)) {
                info.setAppkey(appkey);

                //设置标题显示模式
                SobotApi.setChatTitleDisplayMode(getContext(), SobotChatTitleDisplayMode.values()[0], "aaaaaaa");
                //设置是否开启消息提醒
                SobotApi.setNotificationFlag(getContext(), true, R.drawable.login_logo, R.drawable.login_logo);
                SobotApi.hideHistoryMsg(getActivity(), 0);
                SobotApi.setEvaluationCompletedExit(getContext(), false);


                SobotApi.startSobotChat(getContext(), info);
            } else {
                ToastUtil.showToast(getContext(), "AppKey 不能为空 ！！！");
            }
        }


    }

    public void callPhone(String phoneNum) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        Uri data = Uri.parse("tel:" + phoneNum);
        intent.setData(data);
        startActivity(intent);
    }


    //设置广播获取新收到的信息和未读消息数
    class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int noReadNum = intent.getIntExtra("noReadCount", 0);
            String content = intent.getStringExtra("content");
            //未读消息数
            //新消息内容
            com.sobot.chat.utils.LogUtils.i("新消息内容:" + content);
        }
    }

    private MyReceiver receiver;//广播

    private void regReceiver() {
        //注册广播获取新收到的信息和未读消息数
        if (receiver == null) {
            receiver = new MyReceiver();
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction(ZhiChiConstant.sobot_unreadCountBrocast);
        getActivity().registerReceiver(receiver, filter);
    }
}
