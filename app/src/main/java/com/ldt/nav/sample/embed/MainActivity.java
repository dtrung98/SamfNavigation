package com.ldt.nav.sample.embed;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.dtrung98.insetsview.ext.WindowThemingKt;
import com.ldt.navigation.NavigationFragment;
import com.ldt.navigation.container.SplitNavigatorImpl;
import com.ldt.navigation.container.SplitNavigatorAttribute;

public class MainActivity extends AppCompatActivity implements SplitNavigatorImpl {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        WindowThemingKt.setUpLightSystemUIVisibility(getWindow());
        super.onCreate(savedInstanceState);
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
    public NavigationFragment provideDefaultDetailFragment() {
        return new EmptyPage();
    }

    @NonNull
    @Override
    public NavigationFragment provideDefaultMasterFragment() {
        return SamplePage.newInstance(0, 0);
    }

    @Override
    public SplitNavigatorAttribute getNavigatorAttribute() {
        return mRouterSaver;
    }

}
