package com.ldt.nav.sample.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.ldt.nav.sample.fragment.EmptyPage;
import com.ldt.nav.sample.fragment.SamplePage;
import com.ldt.navigation.NavigationFragment;
import com.ldt.navigation.router.SplitRouter;
import com.ldt.navigation.router.SplitRouterSaver;

public class MainActivity extends AppCompatActivity implements SplitRouter {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        setContentView(provideLayout(this));
        onCreateRouter(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        onSaveRouterState(outState);
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
    if(onNavigateBack())
    return;
    
    super.onBackPressed();
    }

    private final SplitRouterSaver mRouterSaver = new SplitRouterSaver();

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
    public SplitRouterSaver getRouterSaver() {
        return mRouterSaver;
    }

}
