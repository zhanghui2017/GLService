package com.gengli.glservice.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.gengli.glservice.R;
import com.gengli.glservice.activity.HelpSearchActivuty;
import com.gengli.glservice.adapter.ArticleAdapter;
import com.gengli.glservice.bean.Article;
import com.gengli.glservice.http.ServicePort;
import com.gengli.glservice.http.XUtilHttp;
import com.gengli.glservice.util.LogUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.Event;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HelpFragment extends BaseFragment implements View.OnClickListener {
    private Button help_zhinan_bt;
    private Button help_zixun_bt;
    private GuideFragment guideFragment;
    private ConsultFragment consultFragment;
    private FragmentManager fragmentManager;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_help, container, false);
        init(view);
        return view;
    }


    private void init(View view) {
        help_zhinan_bt = (Button) view.findViewById(R.id.help_zhinan_bt);
        help_zixun_bt = (Button) view.findViewById(R.id.help_zixun_bt);
        help_zhinan_bt.setSelected(true);
        help_zhinan_bt.setTextColor(Color.WHITE);
        help_zixun_bt.setSelected(false);
        help_zixun_bt.setTextColor(0xff3983D5);
        help_zhinan_bt.setOnClickListener(this);
        help_zixun_bt.setOnClickListener(this);
        fragmentManager = getActivity().getSupportFragmentManager();
        //设置默认
        changeFrame(0);
    }

    @Event(value = R.id.help_search_bt, type = View.OnClickListener.class)
    private void searchClick(View view) {
        startActivity(new Intent(getActivity(), HelpSearchActivuty.class));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.help_zhinan_bt:
                changeFrame(0);
                help_zhinan_bt.setSelected(true);
                help_zhinan_bt.setTextColor(Color.WHITE);
                help_zixun_bt.setSelected(false);
                help_zixun_bt.setTextColor(0xff3983D5);
                break;
            case R.id.help_zixun_bt:
                changeFrame(1);
                help_zixun_bt.setSelected(true);
                help_zixun_bt.setTextColor(Color.WHITE);
                help_zhinan_bt.setSelected(false);
                help_zhinan_bt.setTextColor(0xff3983D5);
                break;

        }
    }


    private void changeFrame(int index) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        hideAllFrame(transaction);
        switch (index) {
            case 0:
                if (guideFragment == null) {
                    guideFragment = new GuideFragment();
                    transaction.add(R.id.help_frame, guideFragment);
                } else {
                    transaction.show(guideFragment);
                }
                break;
            case 1:
                if (consultFragment == null) {
                    consultFragment = new ConsultFragment();
                    transaction.add(R.id.help_frame, consultFragment);
                } else {
                    transaction.show(consultFragment);
                }
                break;
            default:
                break;
        }
        transaction.commit();
    }

    private void hideAllFrame(FragmentTransaction transaction) {
        if (guideFragment != null) {
            transaction.hide(guideFragment);
        }
        if (consultFragment != null) {
            transaction.hide(consultFragment);
        }

    }

}
