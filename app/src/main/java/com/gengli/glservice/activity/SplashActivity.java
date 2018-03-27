package com.gengli.glservice.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;

import com.gengli.glservice.util.DatasUtil;

public class SplashActivity extends BaseActivity {

    private SharedPreferences preferences;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferences = getSharedPreferences("GL_guide", MODE_PRIVATE);
        final int count = preferences.getInt("count", 0);

        handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                if(count == 0){
                    Intent intent = new Intent();
                    intent.setClass(SplashActivity.this, GuideActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
//                if (DatasUtil.isLogin(SplashActivity.this)) {
//                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
//                    startActivity(intent);
//                    finish();
//                } else {
//                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
//                    startActivity(intent);
//                    finish();
//                }
            }
        }, 2000);
        updateCount();
    }

    public void updateCount() {
        preferences = getSharedPreferences("GL_guide", MODE_PRIVATE);
        int count = preferences.getInt("count", 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("count", ++count);
        editor.commit();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            return false;
        }
        return false;
    }
}