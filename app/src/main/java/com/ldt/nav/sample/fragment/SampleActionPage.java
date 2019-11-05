package com.ldt.nav.sample.fragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.ldt.nav.sample.R;
import com.ldt.navigation.action.BaseFragment;

public class SampleActionPage extends BaseFragment {
    @Override
    public View createView(Context context) {
        return LayoutInflater.from(context).inflate(R.layout.sample_page,null,false);
    }
}
