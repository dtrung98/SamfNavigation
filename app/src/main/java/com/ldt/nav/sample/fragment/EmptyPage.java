package com.ldt.nav.sample.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.ldt.nav.sample.R;
import com.ldt.navigation.NavigationFragment;

public class EmptyPage extends NavigationFragment {
    @Nullable
    @Override
    protected View onCreateContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.empty_content, container, false);
    }
}
