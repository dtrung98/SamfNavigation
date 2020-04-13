package com.ldt.navigation.effectview;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.cardview.widget.CardView;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class EffectCardView extends CardView implements EffectView, ViewGroup
        .OnHierarchyChangeListener, OnApplyWindowInsetsListener {
    private final EffectSaver effectSaver = new EffectSaver();

    @Override
    public EffectSaver getEffectSaver() {
      return effectSaver;
    }

    public EffectCardView(Context context)
    {
      super(context);
        init();
    }

    public EffectCardView(Context context, AttributeSet attrs) {
      super(context, attrs);
        init();
    }

    public EffectCardView(Context context, AttributeSet attrs, int defStyleAttr) {
      super(context, attrs, defStyleAttr);
        init();
    }

    private WindowInsetsCompat mInsets;

    private void init() {
        ViewCompat.setOnApplyWindowInsetsListener(this, this);
        setOnHierarchyChangeListener(this);
    }

    @Override
    public WindowInsetsCompat onApplyWindowInsets(View v, WindowInsetsCompat insets) {
        mInsets = insets;

        for (int index = 0; index < getChildCount(); index++) {
            ViewCompat.dispatchApplyWindowInsets(getChildAt(index), insets);
        }

        return insets;
    }

    @Override
    public void onChildViewAdded(View parent, View child) {
        if (mInsets == null) {
            return;
        }

        ViewCompat.dispatchApplyWindowInsets(child, mInsets);
    }

    @Override
    public void onChildViewRemoved(View parent, View child) {
    }

}
