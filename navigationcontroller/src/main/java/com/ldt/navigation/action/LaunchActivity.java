package com.ldt.navigation.action;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;

import com.ldt.navigation.R;
import com.ldt.navigation.action.util.AndroidUtilities;

import java.util.ArrayList;

public class LaunchActivity extends Activity implements ActionBarLayout.ActionBarLayoutDelegate {
    FrameLayout mFrameLayout;
    public ActionBarLayout actionBarLayout;
    private ArrayList<BaseFragment> mainFragmentsStack = new ArrayList<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        AndroidUtilities.density = getResources().getDimension(R.dimen.oneDP);
        super.onCreate(savedInstanceState);
        mFrameLayout = new FrameLayout(this);
        actionBarLayout = new ActionBarLayout(this);
        //actionBarLayout.setDrawerLayoutContainer(drawerLayoutContainer);
        actionBarLayout.init(mainFragmentsStack);
        actionBarLayout.setDelegate(this);

        setContentView(actionBarLayout, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    @Override
    public boolean onPreIme() {
        return false;
    }

    @Override
    public boolean needPresentFragment(BaseFragment fragment, boolean removeLast, boolean forceWithoutAnimation, ActionBarLayout layout) {
        return true;
    }

    @Override
    public boolean needAddFragmentToStack(BaseFragment fragment, ActionBarLayout layout) {
        return true;
    }

    @Override
    public boolean needCloseLastFragment(ActionBarLayout layout) {
        return true;
    }

    @Override
    public void onBackPressed() {
        if(mainFragmentsStack.size()>1)
        actionBarLayout.onBackPressed();
        else super.onBackPressed();
    }

    @Override
    public void onRebuildAllFragments(ActionBarLayout layout, boolean last) {
        actionBarLayout.rebuildAllFragmentViews(last,last);
    }

    @Override
    protected void onPause() {
        super.onPause();
        actionBarLayout.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        actionBarLayout.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
