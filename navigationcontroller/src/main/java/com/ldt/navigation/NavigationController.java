package com.ldt.navigation;

import android.animation.TimeInterpolator;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import java.util.ArrayList;
import java.util.Stack;

/**
 * Created by burt on 2016. 5. 24..
 */
public class NavigationController extends NavigationFragment {

    private static final String TAG = "NavigationController";
    private FragmentManager mFragManager = null;
    private Stack<NavigationFragment> mFragStack = new Stack<>();
    private @IdRes
    int containerViewId;
    private final Object sync = new Object();
    public String mTag;
    private Stack<String> mTagStack = new Stack<>();
    private static int sIdCount = 1;
    private static int nextId() {
        return ++sIdCount;
    }

    public static String retrieveControllerTag(String tag) {
        return "com.ldt.navigation.fragment:"+"-controller";
    }

    private static String nextNavigationFragmentTag() {
        return "com.ldt.navigation.fragment:"+nextId();
    }

    private TimeInterpolator interpolator = new AccelerateDecelerateInterpolator();
    public final NavigationFragment getFragmentAt(int i) {
        return mFragStack.get(i);
    }
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
    	super.onSaveInstanceState(outState);
    	outState.putInt("container-view-id", containerViewId);
    	outState.putString("controller-tag", mTag);
        ArrayList list = new ArrayList<>(mTagStack);
        outState.putStringArrayList("fragment-navigation-tags", list);
        
    }
    
    
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if(savedInstanceState!=null) {
    	containerViewId = savedInstanceState.getInt("container-view-id",-1);
    	mTag = savedInstanceState.getString("controller-tag");
    
      ArrayList list;
      list = savedInstanceState.getStringArrayList("fragment-navigation-tags");
      if(list!=null)
      mTagStack.clear();
      mTagStack.addAll(list);
      }
    }
    
    protected void restoreFragmentStack() {
      if(mFragManager==null) return;
      int size = mTagStack.size();
      String t;
      NavigationFragment f;
      mFragStack.clear();
      for(int i = 0; i < size; i++) {
        t = mTagStack.elementAt(i);
        f = (NavigationFragment)mFragManager.findFragmentByTag(t);
        if(f != null) {
        f.setNavigationController(this);
        mFragStack.push(f);
        }
      }
    }
    
    public static NavigationController getInstance(@NonNull String tag, @NonNull FragmentManager fragmentManager, @IdRes int containerViewId, Class<? extends NavigationFragment> startUpFragmentCls) {
     
      NavigationController f = restoreInstance(tag, fragmentManager, containerViewId);
      if(f==null) f = newInstance(tag, fragmentManager, containerViewId, startUpFragmentCls);
      return f;
    }    
    
    protected static NavigationController restoreInstance(String tag, @NonNull FragmentManager fragmentManager, @IdRes int containerViewId) {
      
      // find restored controller if any
      NavigationController f = (NavigationController)fragmentManager.findFragmentByTag(tag);
      if(f!=null) {
        if(f.containerViewId==-1) f.containerViewId = containerViewId;
        if(f.mTag==null || f.mTag.isEmpty()) f.mTag = tag;
        f.mFragManager = fragmentManager;
       // restore fragment stack from restored tag stack
       
       f.restoreFragmentStack();
      }
      return f;
    }

    protected static NavigationController newInstance(String tag, @NonNull FragmentManager fragmentManager, @IdRes int containerViewId, Class<? extends NavigationFragment> startUpFragmentCls) {
        NavigationController f = new NavigationController();
        f.containerViewId = containerViewId;
        f.mFragManager = fragmentManager;
        f.mTag = tag;
       // this.setRetainInstance(true);

        synchronized (f.sync) {
            f.mFragManager
                    .beginTransaction()
                    .add(f, tag).commit();
        }
      
        NavigationFragment mainFragment = null;
        try {
             mainFragment = startUpFragmentCls.newInstance();
        } catch (Exception ignored) {
            Log.e(TAG,"Unable to create new instance of start up fragment");
        }
        if(mainFragment!=null)
        f.navigateTo(mainFragment);
        
        return f;
    }
    
    public NavigationFragment getTopFragment() {
        if(mFragStack.size() != 0)return  mFragStack.lastElement();
        return null;
    }

    @Nullable
    @Override
    protected View onCreateContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return null;
    }

    public int getFragmentCount() {
        return mFragStack.size();
    }
    public void setInterpolator(TimeInterpolator interpolator) {
        this.interpolator = interpolator;
    }
    TimeInterpolator getInterpolator() {
        return interpolator;
    }

    public void pushFragment(NavigationFragment fragment) {
        setInterpolator(new AccelerateDecelerateInterpolator());
        navigateTo(fragment);
    }

    public void popFragment() {
        navigateBack();
    }

    public void navigateTo(NavigationFragment fragment) {
        navigateTo(fragment, true);
    }

    public void navigateTo(NavigationFragment fragment, boolean withAnimation) {

        if(mFragManager == null) return;

        synchronized (sync) {
            String eTag = nextNavigationFragmentTag();
            if (mFragStack.size() == 0) {
                fragment.setNavigationController(this);
                fragment.setAnimatable(false);
                mFragManager
                        .beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .replace(containerViewId, fragment, eTag)
                        .commit();

            } else {

                fragment.setNavigationController(this);

                int openExit = fragment.defaultOpenExitTransition();
                if(openExit==PresentStyle.SAME_AS_OPEN)
                getTopFragment().setOpenExitPresentStyle(fragment.getPresentStyle());
                else if(openExit!=PresentStyle.REMOVED_FRAGMENT_PRESENT_STYLE)
                    getTopFragment().setOpenExitPresentStyle(PresentStyle.get(openExit));

                fragment.setAnimatable(withAnimation);
                // hide last fragment and add new fragment
                NavigationFragment hideFragment = mFragStack.peek();
                hideFragment.onHideFragment();
                mFragManager
                        .beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .hide(hideFragment)
                        .add(containerViewId, fragment, eTag)
                        .commitNow();
            }
            mFragStack.add(fragment);
            mTagStack.add(eTag);
        }
    }
    private boolean mIsAbleToPopRoot = false;
    public void setAbleToPopRoot(boolean able) {
        mIsAbleToPopRoot = able;
    }

    public boolean navigateBack() {
        return navigateBack(true);
    }

/*
    protected boolean navigateBackAbsolutely(boolean withAnimation) {

        if(mFragManager == null) return false;

        // mFragStack only has root fragment
        if(mFragStack.size() == 1) {

            // remove root
            NavigationFragment fragmentToRemove= mFragStack.pop();
            fragmentToRemove.setNavigationController(this);
            fragmentToRemove.setAnimatable(withAnimation);
            mFragManager
                    .beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                    .remove(fragmentToRemove)
                    .commit();
            return true;
        }
        else if(mFragStack.size()==0) return false;

        synchronized (sync) {

            NavigationFragment fragmentToRemove = mFragStack.pop();
            String eTagRemove = mTagStack.pop();
            fragmentToRemove.setNavigationController(this);
            fragmentToRemove.setAnimatable(withAnimation);

            NavigationFragment fragmentToShow = mFragStack.peek();
            String eTagShow = mTagStack.peek();
            fragmentToShow.setNavigationController(this);
            fragmentToShow.setAnimatable(withAnimation);
            mFragManager
                    .beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                    .show(fragmentToShow)
                    .remove(fragmentToRemove)
                    .commit();

        }
        return true;
    }
    */
    public boolean navigateBack(boolean withAnimation) {

        if(mFragManager == null) return false;

        // mFragStack only has root fragment
        if(mFragStack.size() == 1) {

            // navigateBack whole navigation
            
            /*
            NavigationFragment fragmentToShow = mFragStack.peek();
            fragmentToShow.setNavigationController(this);
            fragmentToShow.setAnimatable(withAnimation);
            mFragManager
                    .beginTransaction()
                    .show(fragmentToShow)
                    .commit(); */
            return false;
        }

        synchronized (sync) {

            NavigationFragment fragmentToRemove = mFragStack.pop();
            fragmentToRemove.setNavigationController(this);
            fragmentToRemove.setAnimatable(withAnimation);

            NavigationFragment fragmentToShow = mFragStack.peek();
            fragmentToShow.setNavigationController(this);
            fragmentToShow.setAnimatable(withAnimation);
            mFragManager
                    .beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                    .show(fragmentToShow)
                    .remove(fragmentToRemove)
                    .commit();

        }
        return true;
    }

    public void navigateBackToRootFragment() {

        while (mFragStack.size() >= 2) {
            navigateBack();
        }
    }
    public void navigateBackAllFragments() {
        if(!mIsAbleToPopRoot) {
            navigateBackToRootFragment();
        } else {
            while (mFragStack.size()>=1)
                navigateBack();
        }
    }
    
    @Override
     public boolean onNavigateBack(){
     // true khi handle
     // false khi ko handle
	NavigationFragment f = getTopFragment();
	
	// nếu top fragment null -> ko handle -> false
	// nếu top fragment ko back được -> ko navigateBack dc fragment -> bỏ qua lệnh navigate -> true
	// nếu top fragment dc navigateBack -> handle, ngược lại thì false
	return 
	 f!=null && (!f.onNavigateBack() ||
	 navigateBack());
    }

}