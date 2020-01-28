package com.ldt.nav.sample.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.ldt.nav.sample.R;
import com.ldt.nav.sample.fragment.SamplePage;
import com.ldt.navigation.ui.NavigationActivity;

import butterknife.ButterKnife;
import androidx.fragment.app.Fragment;
import android.widget.Toast;

public class MainActivity extends NavigationActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
   
        //initNavigation("navigation_1",savedInstanceState,R.id.container, SamplePage.class);
        Fragment f = getSupportFragmentManager().findFragmentByTag("sample-page");
                  
         Toast.makeText(this,"finding fragment: "+ (f!=null), Toast.LENGTH_SHORT).show();
        if(savedInstanceState==null) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container,new SamplePage(),"sample-page").commit();
                }                
                }
    }
}
