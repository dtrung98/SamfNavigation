package com.ldt.navigation.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.animation.AccelerateDecelerateInterpolator;
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
        if(mNavigationController.getTopFragment().onBackPressed())
            if(!(isNavigationControllerInit() && mNavigationController.dismissFragment(true)))
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

    protected void initNavigation(String tag, Bundle savedState,@NonNull @IdRes int container,@NonNull NavigationFragment startUpFragment) {
        FragmentManager fm = getSupportFragmentManager();
        checkInstance(fm, tag);
        mNavigationController = FragNavigationController.newInstance(fm, container, tag);
        mNavigationController.setInterpolator(new AccelerateDecelerateInterpolator());
       
        mNavigationController.presentFragment(startUpFragment);
    }
    
    public void checkInstance(FragmentManager fm, String tag) {
      Fragment saved = fm.findFragmentByTag(tag);
        if(saved != null) {
        Toast.makeText(this,"found saved navigation instance", Toast.LENGTH_SHORT).show();
      } else Toast.makeText(this, "no navigation instance", Toast.LENGTH_SHORT).show();
    }

    protected void initNavigation(String tag, Bundle savedState, @IdRes int container, Class<? extends NavigationFragment> startUpFragmentCls) {
        FragmentManager fm = getSupportFragmentManager();
        checkInstance(fm, tag);
        mNavigationController = FragNavigationController.newInstance(fm, container, tag);
        mNavigationController.setInterpolator(new AccelerateDecelerateInterpolator());
        NavigationFragment mainFragment = null;
        try {
             mainFragment = startUpFragmentCls.newInstance();
        } catch (Exception ignored) {
            Log.d(TAG,"Unable to create new instance of start up fragment");
        }
        if(mainFragment!=null)
        mNavigationController.presentFragment(mainFragment);
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
                dismiss();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
