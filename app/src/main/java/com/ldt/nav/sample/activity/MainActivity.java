package com.ldt.nav.sample.activity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.ldt.nav.sample.fragment.EmptyPage;
import com.ldt.nav.sample.fragment.SamplePage;
import com.ldt.navigation.NavigationController;
import com.ldt.navigation.router.SplitRouterSaver;
import com.ldt.navigation.router.SplitRouter;
import com.ldt.navigation.uicontainer.ExpandContainer;

public class MainActivity extends AppCompatActivity implements SplitRouter {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(provideLayout(this));
        onCreateRouter(savedInstanceState, getSupportFragmentManager());
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        onSaveRouterState(outState);
        super.onSaveInstanceState(outState);
    }
    
    @Override
    public void onBackPressed() {
    if(onNavigateBack())
    return;
    
    super.onBackPressed();
    }

    private final SplitRouterSaver mRouterSaver = new SplitRouterSaver("left-router","right-router");

    @Override
    public NavigationController presentLeftRouter(String leftControllerTag, int leftContainerViewId) {
        return presentNavigator(leftControllerTag,
                getSupportFragmentManager(),
                leftContainerViewId,
                SamplePage.class,
                ExpandContainer.class);
    }

    @Override
    public NavigationController presentRightRouter(String rightControllerTag, int rightContainerViewId) {
        return /*null*/ presentNavigator(rightControllerTag,
                getSupportFragmentManager(),
                rightContainerViewId,
                EmptyPage.class,
                ExpandContainer.class);
    }

    @Override
    public SplitRouterSaver getRouterSaver() {
        return mRouterSaver;
    }
}
