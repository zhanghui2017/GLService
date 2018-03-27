package com.gengli.glservice.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.gengli.glservice.R;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;

@ContentView(R.layout.activity_customer)
public class CustomerActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Event(value = R.id.customer_back_bt, type = View.OnClickListener.class)
    private void backClick(View view) {
        finish();
    }
}
