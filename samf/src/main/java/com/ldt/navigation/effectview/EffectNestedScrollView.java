package com.ldt.navigation.effectview;

import android.content.Context;
import android.util.AttributeSet;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;

public class EffectNestedScrollView extends NestedScrollView implements EffectView {
    private final EffectSaver mEffectSaver = new EffectSaver();

    public EffectNestedScrollView(Context context) {
        super(context);
    }

    public EffectNestedScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EffectNestedScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public EffectSaver getEffectSaver() {
        return mEffectSaver;
    }
}
