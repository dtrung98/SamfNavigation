package com.ldt.nav.sample.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ldt.nav.sample.R;
import com.ldt.navigation.NavigationFragment;

import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SamplePageTwo extends NavigationFragment {
    private static final String TAG = "SamplePageTwo";

    @OnClick(R.id.back_button)
    void back() {
        dismiss();
    }
    @OnClick(R.id.button)
    void goToSomeWhere() {
        presentFragment(new SamplePageThree());
    }

    @BindView(R.id.root)
    View mRoot;

    public static SamplePageTwo newInstance(int value) {
        SamplePageTwo fragment = new SamplePageTwo();
        fragment.p = value;
        return fragment;
    }

    @Nullable
    @Override
    protected View onCreateContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.sample_page,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this,view);
        mRoot.setBackgroundResource(R.color.FlatGreen);
    }

    int p = -1;
    @Override
    public int defaultTransition() {
        if(p==-1) {
            Random r = new Random();
            p = r.nextInt(39) + 1; //exclude NONE present style
        }
        return p;
    }
}
