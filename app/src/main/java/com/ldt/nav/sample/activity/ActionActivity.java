package com.ldt.nav.sample.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.ldt.nav.sample.sampleaction.SamplePage;
import com.ldt.navigation.action.LaunchActivity;

public class ActionActivity extends LaunchActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actionBarLayout.presentFragment(new SamplePage());
    }
}
