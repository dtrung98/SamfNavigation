package com.ldt.nav.sample.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.ldt.nav.sample.R;
import com.ldt.nav.sample.fragment.SamplePage;
import com.ldt.navigation.ui.NavigationActivity;

import butterknife.ButterKnife;


public class MainActivity extends NavigationActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initNavigation("navigation_1",savedInstanceState,R.id.container, SamplePage.class);
    }
}
