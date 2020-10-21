package com.ldt.navigation.navigationbar;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowInsets;

import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.ldt.navigation.R;
import com.ldt.navigation.effectview.EffectConstraintLayout;

public abstract class NavigationBar extends EffectConstraintLayout {
    public NavigationBar(Context context) {
        super(context);
    }

    public NavigationBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public NavigationBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
