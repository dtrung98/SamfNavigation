package com.ldt.nav.sample.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.dtrung98.insetsview.ext.WindowThemingKt;
import com.ldt.nav.sample.R;
import com.ldt.nav.sample.fragment.SampleNavPage;
import com.ldt.navigation.ContainerNavigationControllerFragment;

public class SampleNavActivity extends AppCompatActivity {

    public static final String TAG_CONTAINER_NAVIGATION_CONTROLLER_FRAGMENT = "container-navigation-controller-fragment";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        WindowThemingKt.setUpDarkSystemUIVisibility(getWindow());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frame_layout);

        if (null == getSupportFragmentManager().findFragmentByTag(TAG_CONTAINER_NAVIGATION_CONTROLLER_FRAGMENT)) {
            ContainerNavigationControllerFragment fragment = ContainerNavigationControllerFragment.create(SampleNavPage.class);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.rootView, fragment, TAG_CONTAINER_NAVIGATION_CONTROLLER_FRAGMENT)
                    .commitNow();
        }
    }
}
