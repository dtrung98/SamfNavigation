package com.ldt.nav.sample.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.dtrung98.insetsview.ext.WindowThemingKt;
import com.ldt.nav.sample.R;
import com.ldt.nav.sample.fragment.SampleNavPage;
import com.ldt.nav.sample.fragment.SamplePage;
import com.ldt.navigation.FragmentRouter;
import com.ldt.navigation.uicontainer.ExpandStaticContainer;

public class SampleNavActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        WindowThemingKt.setUpDarkSystemUIVisibility(getWindow());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_host_layout);
        FragmentRouter fragmentRouter = new FragmentRouter();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.rootView, fragmentRouter)
                .commitNow();

        fragmentRouter.presentController(
                "main",
                ExpandStaticContainer.class,
                new SampleNavPage());
    }
}
