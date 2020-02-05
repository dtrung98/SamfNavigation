package com.ldt.nav.sample.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ldt.nav.sample.R;
import com.ldt.nav.sample.activity.MainActivity;
import com.ldt.navigation.NavigationFragment;

import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SamplePageThree extends NavigationFragment {
    private static final String TAG = "SamplePageThree";

    @OnClick(R.id.back_button)
    void back() {
        navigateBack();
    }
    @OnClick(R.id.button)
    void goToSomeWhere() {
        navigateTo(new SamplePage());
    }
    @BindView(R.id.root)
    View mRoot;

    @Nullable
    @Override
    protected View onCreateContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.sample_page,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this,view);
        mRoot.setBackgroundResource(R.color.FlatOrange);
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

    @OnClick(R.id.button_2)
    void openSetting() {
        Activity activity = getActivity();
        if(activity instanceof MainActivity)
            ((MainActivity)activity).showSetting();
    }
}
