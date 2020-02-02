package com.ldt.nav.sample.sampleaction;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ldt.nav.sample.R;
import com.ldt.navigation.NavigationFragment;
import com.ldt.navigation.action.BaseFragment;

import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SamplePageThree extends BaseFragment {
    private static final String TAG = "SamplePageThree";

    @OnClick(R.id.back_button)
    void back() {
        finishFragment();
    }
    @OnClick(R.id.button)
    void goToSomeWhere() {
        presentFragment(new SamplePage());
    }
    @BindView(R.id.root)
    View mRoot;

    @Nullable
    @Override
        public View createView(Context context) {
            View view = LayoutInflater.from(context).inflate(R.layout.sample_page, null, false);
            ButterKnife.bind(this, view);
            mRoot.setBackgroundResource(R.color.FlatOrange);
            return view;
    }
}
