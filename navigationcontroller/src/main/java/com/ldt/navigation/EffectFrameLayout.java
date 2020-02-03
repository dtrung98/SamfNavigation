package com.ldt.navigation;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class EffectFrameLayout extends FrameLayout implements EffectView {
    private final EffectSaver effectSaver;
    
    @Override 
    EffectSaver getEffectSaver() {
      return effectSaver;
    }

    public EffectFrameLayout(Context context) {
        super(context);
    }

    public EffectFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EffectFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public EffectFrameLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

}
