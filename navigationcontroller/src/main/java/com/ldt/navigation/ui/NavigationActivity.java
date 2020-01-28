package com.ldt.navigation.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import com.ldt.navigation.FragNavigationController;
import com.ldt.navigation.NavigationFragment;
import androidx.fragment.app.Fragment;
import android.widget.Toast;

public class NavigationActivity extends AppCompatActivity {
    private static final String TAG = "NavigationActivity";

private FragNavigationController mNavigationController;
    @Override
    public void onBackPressed() {
    if(mNavigationController!=null && !mNavigationController.onBackPressed())
    return;
    
    super.onBackPressed();
    }

    private boolean isNavigationControllerInit() {
        return null!= mNavigationController;
    }
    public void presentFragment(NavigationFragment fragment) {
        if(isNavigationControllerInit()) {
//            Random r = new Random();
//            mNavigationController.setPresentStyle(r.nextInt(39)+1); //exclude NONE present style

            mNavigationController.presentFragment(fragment, true);
        }
    }

/*
    protected void initNavigation(String tag, @NonNull @IdRes int container,@NonNull NavigationFragment startUpFragment) {
        FragmentManager fm = getSupportFragmentManager();
        //checkInstance(fm, savedState, tag);
        mNavigationController = FragNavigationController.getInstance(fm, container, tag);
        
        if(mNavigationController.getFragmentCount()==0) {
        mNavigationController.setInterpolator(new AccelerateDecelerateInterpolator());
        mNavigationController.presentFragment(startUpFragment);
        }
    }
    */
    
    protected void initNavigation(String tag, @IdRes int container, Class<? extends NavigationFragment> startUpFragmentCls) {
        FragmentManager fm = getSupportFragmentManager();
        mNavigationController = FragNavigationController.getInstance(fm, container, tag, startUpFragmentCls);
    }

    public void dismiss() {
        if(isNavigationControllerInit()) {
            mNavigationController.dismissFragment();
        }
    }

    public void presentFragment(NavigationFragment fragment, boolean animated) {
        if(isNavigationControllerInit()) {
            mNavigationController.presentFragment(fragment,animated);
        }
    }
    public void dismiss(boolean animated) {
        if(isNavigationControllerInit()) {
            mNavigationController.dismissFragment(animated);
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
