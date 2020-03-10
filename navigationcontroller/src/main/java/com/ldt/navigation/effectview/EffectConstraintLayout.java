package com.ldt.navigation.effectview;

import android.content.Context;
import android.util.AttributeSet;

import androidx.constraintlayout.widget.ConstraintLayout;

public class EffectConstraintLayout extends ConstraintLayout implements EffectView {
    private final EffectSaver mEffectSaver = new EffectSaver();

    public EffectConstraintLayout(Context context) {
        super(context);
    }

    public EffectConstraintLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EffectConstraintLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public EffectSaver getEffectSaver() {
        return mEffectSaver;
    }
}
