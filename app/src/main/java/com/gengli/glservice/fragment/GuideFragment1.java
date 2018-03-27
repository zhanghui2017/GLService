package com.gengli.glservice.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gengli.glservice.R;

public class GuideFragment1 extends BaseFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.guide1_fragment, null);
		ViewGroup vGroup = (ViewGroup) view.getParent();
		if (vGroup != null) {
			vGroup.removeAllViewsInLayout();
		}
		return view;
	}
	
}
