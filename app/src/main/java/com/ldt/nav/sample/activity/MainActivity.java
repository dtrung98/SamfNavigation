package com.ldt.nav.sample.activity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.ldt.nav.sample.R;
import com.ldt.nav.sample.fragment.SamplePage;
import com.ldt.navigation.holder.NavigationRouters;
import com.ldt.navigation.holder.RouterSaver;
import com.ldt.navigation.uicontainer.ExpandContainer;
import com.ldt.navigation.uicontainer.NextFlowContainer;

public class MainActivity extends AppCompatActivity implements NavigationRouters {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Fragment f = getSupportFragmentManager().findFragmentByTag("main-navigation-controller");
        //Toast.makeText(this, "finding fragment: " + (f != null), Toast.LENGTH_SHORT).show();
        restoreRoutersState(savedInstanceState, getSupportFragmentManager());
        obtainRouter(
                        "main-navigation-controller",
                        getSupportFragmentManager(),
                        R.id.container,
                        SamplePage.class,
                        ExpandContainer.class);

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        saveRouterState(outState);
        super.onSaveInstanceState(outState);
    }

    public void showSetting() {
        obtainRouter(
                "setting-navigation-controller",
                getSupportFragmentManager(),
                R.id.container,
                SamplePage.class,
                NextFlowContainer.class);
    }
    
    @Override
    public void onBackPressed() {
    if(onNavigateBack())
    return;
    
    super.onBackPressed();
    }

    private final RouterSaver mRouterSaver = new RouterSaver();

    @Override
    public RouterSaver getRouterSaver() {
        return mRouterSaver;
    }
}
