package com.ldt.nav.sample.fragment;

import android.content.Context;
import android.util.AttributeSet;

import com.ldt.navigation.effectview.EffectView;
import com.ldt.springback.view.SpringBackLayout;

public class EffectSpringBackLayout extends SpringBackLayout implements EffectView {
    private final EffectSaver mEffectSaver = new EffectSaver();

    public EffectSpringBackLayout(Context context) {
        super(context);
    }

    public EffectSpringBackLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public EffectSaver getEffectSaver() {
        return mEffectSaver;
    }
}
