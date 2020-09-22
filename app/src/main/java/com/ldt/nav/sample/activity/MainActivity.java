package com.ldt.nav.sample.activity;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.ldt.nav.sample.fragment.EmptyPage;
import com.ldt.nav.sample.fragment.SamplePage;
import com.ldt.navigation.NavigationFragment;
import com.ldt.navigation.container.SplitFragmentContainerNavigator;
import com.ldt.navigation.container.SplitNavigatorAttribute;

public class MainActivity extends AppCompatActivity implements SplitFragmentContainerNavigator {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        setContentView(provideLayout(this));
        onCreateNavigator(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        onSaveNavigatorState(outState);
        super.onSaveInstanceState(outState);
    }


    @Override
    public void onConfigureSplitRouter(SplitCondition splitWhen, int screenWidthDp, int screenHeightDp) {
        splitWhen
                .widerThan(600)
                .tallerThan(-1)
                .configLeftWide(350)
                .configRightWide(-1);
    }

    @Override
    public void onBackPressed() {
        // false: not allow to back
        // true: allowed to back & backing executed
        requestBack();
        //super.onBackPressed();
    }

    private final SplitNavigatorAttribute mRouterSaver = new SplitNavigatorAttribute();

    @Override
    public void dismiss() {
        super.onBackPressed();
    }

    @Override
    public FragmentManager provideFragmentManager() {
        return getSupportFragmentManager();
    }

    @NonNull
    @Override
    public Class<? extends NavigationFragment> provideDefaultDetailFragment() {
        return EmptyPage.class;
    }

    @NonNull
    @Override
    public Class<? extends NavigationFragment> provideDefaultMasterFragment() {
        return SamplePage.class;
    }

    @Override
    public SplitNavigatorAttribute getNavigatorAttribute() {
        return mRouterSaver;
    }

}
