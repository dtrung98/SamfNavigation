package com.ldt.nav.sample.traditional;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.dtrung98.insetsview.ext.WindowThemingKt;
import com.ldt.nav.sample.R;

public class TraditionalActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowThemingKt.setUpDarkSystemUIVisibility(getWindow());
        if (savedInstanceState == null) {
            SamplePage fragment = new SamplePage();
            fragment.setShowsDialog(false);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(android.R.id.content, fragment, "main")
                    .setPrimaryNavigationFragment(fragment)
                    .commitNow();
        }



    }
}
