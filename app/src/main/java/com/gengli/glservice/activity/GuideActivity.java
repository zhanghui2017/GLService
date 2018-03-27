package com.gengli.glservice.activity;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.Window;

import com.gengli.glservice.R;
import com.gengli.glservice.fragment.GuideFragment1;
import com.gengli.glservice.fragment.GuideFragment2;
import com.gengli.glservice.fragment.GuideFragment3;

public class GuideActivity extends FragmentActivity {

    private ViewPager viewPager;

    private ArrayList<Fragment> fragments;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_guide);


        fragments = new ArrayList<>();
        fragments.add(new GuideFragment1());
        fragments.add(new GuideFragment2());
        fragments.add(new GuideFragment3());

        viewPager = (ViewPager) findViewById(R.id.viewPager);

        viewPager.setAdapter(new MyViewPagerAdapter(getSupportFragmentManager()));

        viewPager.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
                // TODO Auto-generated method stub

            }
        });
        //设置首页存在状态为不存在
//		DatasUtil.changeMainActivityExitState(GuideActivity.this, false);

    }

    public class MyViewPagerAdapter extends FragmentPagerAdapter {
        public MyViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int arg0) {
            return fragments.get(arg0);
        }

        @Override

        public int getCount() {
            return fragments.size();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 锁定BACK键
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            return false;
        }
        return false;
    }

}

