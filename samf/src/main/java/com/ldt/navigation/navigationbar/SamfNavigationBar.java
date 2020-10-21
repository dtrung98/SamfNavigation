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

public class SamfNavigationBar extends NavigationBar {

    public SamfNavigationBar(Context context) {
        super(context);
        init(context, null);
    }

    public SamfNavigationBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public SamfNavigationBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        View.inflate(context, R.layout.samf_navigation_bar, this);
    }

    @Override
    public WindowInsets onApplyWindowInsets(WindowInsets insets) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            final WindowInsetsCompat insetsCompat = WindowInsetsCompat.toWindowInsetsCompat(insets);
            boolean isLTR = ViewCompat.getLayoutDirection(this) == ViewCompat.LAYOUT_DIRECTION_LTR;
            int top = insetsCompat.getSystemWindowInsetTop();
            int start = isLTR ? insetsCompat.getSystemWindowInsetLeft() : insetsCompat.getSystemWindowInsetRight();
            int end = isLTR ? insetsCompat.getSystemWindowInsetRight() : insetsCompat.getSystemWindowInsetLeft();

            MarginLayoutParams marginLayoutParams = ((MarginLayoutParams)findViewById(R.id.safeView).getLayoutParams());
            marginLayoutParams.topMargin = top;
            marginLayoutParams.setMarginStart(start);
            marginLayoutParams.setMarginEnd(end);
            marginLayoutParams.bottomMargin = 0;
        }
        return super.onApplyWindowInsets(insets);
    }
}
