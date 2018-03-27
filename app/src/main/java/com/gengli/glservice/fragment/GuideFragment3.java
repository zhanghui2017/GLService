package com.gengli.glservice.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gengli.glservice.R;
import com.gengli.glservice.activity.LoginActivity;
import com.gengli.glservice.activity.MainActivity;

public class GuideFragment3 extends BaseFragment {

    private TextView login_now_bt, user_now_button;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View view = inflater.inflate(R.layout.guide3_fragment, null);
        ViewGroup vGroup = (ViewGroup) view.getParent();
        if (vGroup != null) {
            vGroup.removeAllViewsInLayout();
        }

        login_now_bt = (TextView) view.findViewById(R.id.login_now_bt);
        user_now_button = (TextView) view.findViewById(R.id.user_now_button);

        login_now_bt.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();

            }
        });

        user_now_button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
                getActivity().finish();

            }
        });
        return view;
    }

}
