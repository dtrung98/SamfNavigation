package com.ldt.nav.sample.activity;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import butterknife.ButterKnife;
import com.ldt.nav.sample.R;
import com.ldt.nav.sample.fragment.SamplePage;
import com.ldt.navigation.NavigationController;
import com.ldt.navigation.SingleHolder;
import com.ldt.navigation.uicontainer.FlexContainer;

public class MainActivity extends AppCompatActivity implements SingleHolder {
    private NavigationController mNavigationController;

    @Override
    public NavigationController getNavigationController() {
        return mNavigationController;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Fragment f = getSupportFragmentManager().findFragmentByTag("main-navigation-controller");

        //Toast.makeText(this, "finding fragment: " + (f != null), Toast.LENGTH_SHORT).show();

        mNavigationController =
                NavigationController.getInstance(
                        "main-navigation-controller",
                        getSupportFragmentManager(),
                        R.id.container,
                        SamplePage.class,
                        FlexContainer.class);
    }
    
    @Override
    public void onBackPressed() {
    if(onNavigateBack())
    return;
    
    super.onBackPressed();
    }
}
