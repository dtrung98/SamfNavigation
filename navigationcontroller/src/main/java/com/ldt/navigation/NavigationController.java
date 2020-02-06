package com.ldt.navigation;

import android.animation.TimeInterpolator;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Stack;

import com.ldt.navigation.holder.Router;
import com.ldt.navigation.holder.Routers;
import com.ldt.navigation.uicontainer.UIContainer;
import com.ldt.navigation.uicontainer.ExpandContainer;

/**
 * Created by burt on 2016. 5. 24..
 * Updated by dtrung98
 */
public class NavigationController extends NavigationFragment {

    private static final String TAG = "NavigationController";
    
    private Stack<NavigationFragment> mFragStack = new Stack<>();
    private @IdRes
    int navContainerId;
    int subContainerId;
    private final Object sync = new Object();
    public String mTag;
    private Stack<String> mTagStack = new Stack<>();
    private static int sIdCount = 1;
    private static int nextId() {
        return ++sIdCount;
    }

    public UIContainer getUiContainer() {
        return mUiContainer;
    }

    private UIContainer mUiContainer = null;

    private WeakReference<Router> mWeakRouter;
    public Router getRouter() {
        if(mWeakRouter==null) return null;
        return mWeakRouter.get();
    }

    public NavigationController obtainRouter(String tag, Class<? extends NavigationFragment> startUpFragmentCls,  Class<? extends UIContainer> uiContainerCls) {
        Router router = getRouter();
        if(router instanceof Routers && getFragmentManager() != null) {
            return ((Routers) router).obtainRouter(tag, getFragmentManager(), navContainerId, startUpFragmentCls, uiContainerCls);
        }
        return null;
    }

    public NavigationController obtainRouter(String tag, Class<? extends NavigationFragment> startUpFragmentCls, int navContainerId,  Class<? extends UIContainer> uiContainerCls) {
        Router router = getRouter();
        if(router instanceof Routers && getFragmentManager() != null) {
            return ((Routers) router).obtainRouter(tag, getFragmentManager(), navContainerId, startUpFragmentCls, uiContainerCls);
        }
        return null;
    }

    public void setRouter(Router router) {
        mWeakRouter = new WeakReference<>(router);
    }

    private Class<? extends NavigationFragment> mStartUpFragmentCls = null;
    
    public FragmentManager getFragmentManagerForNavigation() {
      return getChildFragmentManager();
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
    public void onSaveInstanceState(@NonNull Bundle outState) {
    	super.onSaveInstanceState(outState);
    	outState.putInt("nav-container-id", navContainerId);
    	
    	outState.putInt("sub-container-id", subContainerId);
    	
    	outState.putString("controller-tag", mTag);
        ArrayList<String> list = new ArrayList<>(mTagStack);
        outState.putStringArrayList("fragment-navigation-tags", list);
        UIContainer.save(mTag, mUiContainer.getClass());
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if(savedInstanceState!=null) {
        navContainerId = savedInstanceState.getInt("nav-container-id", -1);
        subContainerId = savedInstanceState.getInt("sub-container-id", R.id.sub_container);
        mTag = savedInstanceState.getString("controller-tag");

        mUiContainer = UIContainer.instantiate(getContext(), mTag);
        if (mUiContainer == null) mUiContainer = new ExpandContainer();

        ArrayList<String> list;
        list = savedInstanceState.getStringArrayList("fragment-navigation-tags");
        if (list != null) {
            mTagStack.clear();
            mTagStack.addAll(list);
        }
    }

        int w = getContext().getResources().getConfiguration().screenWidthDp;

        int h = getContext().getResources().getConfiguration().screenHeightDp;

        float dpUnit = getContext().getResources().getDimension(R.dimen.dpUnit);
        mUiContainer.provideConfig(w, h, dpUnit);
        mUiContainer.attach(this);
    }

    private void showStartupFragmentIfNeed() {
        if(mStartUpFragmentCls !=null && mTagStack.isEmpty()) {
            NavigationFragment mainFragment = null;
            try {
                mainFragment = mStartUpFragmentCls.newInstance();
            } catch (Exception ignored) {
                Log.e(TAG,"Unable to create new instance of start up fragment");
            }
            if(mainFragment!=null)
                navigateTo(mainFragment);
        }
    }

    @Override
    public void onDestroy() {
        mUiContainer.detach();
        super.onDestroy();
    }

    protected void restoreFragmentStack() {
      FragmentManager fm = getFragmentManagerForNavigation();
      
      if(fm==null) return;
      int size = mTagStack.size();
      String t;
      NavigationFragment f;
      mFragStack.clear();
      for(int i = 0; i < size; i++) {
        t = mTagStack.elementAt(i);
        f = (NavigationFragment)fm.findFragmentByTag(t);
        if(f != null) {
        f.setNavigationController(this);
        mFragStack.push(f);
        }
      }
    }
    
    public static NavigationController getInstance(@NonNull String tag, @NonNull FragmentManager fragmentManager, @IdRes int navContainerId, Class<? extends NavigationFragment> startUpFragmentCls) {
      return getInstance(tag, fragmentManager, navContainerId, startUpFragmentCls, null);
    }
    
    public static NavigationController getInstance(
            @NonNull String tag,
            @NonNull FragmentManager fragmentManager,
            @IdRes int navContainerId,
            Class<? extends NavigationFragment> startUpFragmentCls,
            Class<? extends UIContainer> uiContainerCls)
    {
      NavigationController f = restoreInstance(tag, fragmentManager);
            if(f==null) f = newInstance(tag, fragmentManager, navContainerId, startUpFragmentCls, uiContainerCls);
            return f;
      }
    
    public static NavigationController restoreInstance(String tag,@NonNull FragmentManager fragmentManager) {
      
      // find restored controller if any
      NavigationController f = (NavigationController)fragmentManager.findFragmentByTag(tag);
      if(f!=null) {
        
        if(f.mTag==null || f.mTag.isEmpty()) f.mTag = tag;
        
       // restore fragment stack from restored tag stack
       
       f.restoreFragmentStack();
      }
      return f;
    }

    protected static NavigationController newInstance(String tag, @NonNull FragmentManager fragmentManager, @IdRes int navContainerId, Class<? extends NavigationFragment> startUpFragmentCls, Class<? extends UIContainer> uiContainerCls) {
        NavigationController f = new NavigationController();
        f.navContainerId = navContainerId;
        f.subContainerId = View.generateViewId();
        
        f.mTag = tag;
        
        UIContainer uic = null;
        try {
          uic = uiContainerCls.newInstance();
        } catch (Exception ignored) {
          Log.e(TAG, "Unable to create new UIContainer instance");
        }
        if(uic == null) uic = new ExpandContainer();
        
        f.mUiContainer = uic;
        
        f.mStartUpFragmentCls = startUpFragmentCls;
       // this.setRetainInstance(true);

        synchronized (f.sync) {
            fragmentManager
                    .beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .add(navContainerId ,f , tag)
                    .commit();
        }

        return f;
    }

    public boolean isControllerAvailable() {
        return !isControllerRemoved;
    }

    private boolean isControllerRemoved = false;

    public void finish() {
        if(isControllerRemoved) return;
        isControllerRemoved = true;

        synchronized (sync) {
            FragmentManager fragmentManager = getFragmentManager();
            if(fragmentManager!=null)
                fragmentManager.beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .remove(this)
                        .commit();
        }
    }

    public void quit() {
        Router router = getRouter();
        if(router != null) router.finishController(this);
    }
    
    public NavigationFragment getTopFragment() {
        if(mFragStack.size() != 0)return  mFragStack.lastElement();
        return null;
    }

    @Nullable
    @Override
    protected View onCreateContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        showStartupFragmentIfNeed();
        return mUiContainer.provideLayout(getContext(), inflater, container, subContainerId);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mUiContainer.bindLayout(view);
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

        synchronized (sync) {
          
          fragment.setNavigationController(this);
          
          // phát sinh tag
            String eTag = nextNavigationFragmentTag();
            
            // nếu stack rỗng
            if (mFragStack.size() == 0) {
                fragment.setAnimatable(false);
                getFragmentManagerForNavigation()
                        .beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .replace(subContainerId, fragment, eTag)
                        .commit();

            } else {

                int openExit = fragment.defaultOpenExitTransition();
                if(openExit==PresentStyle.SAME_AS_OPEN)
                getTopFragment().setOpenExitPresentStyle(fragment.getPresentStyle());
                else if(openExit!=PresentStyle.REMOVED_FRAGMENT_PRESENT_STYLE)
                    getTopFragment().setOpenExitPresentStyle(PresentStyle.get(openExit));

                fragment.setAnimatable(withAnimation);
                // hide last fragment and add new fragment
                NavigationFragment hideFragment = mFragStack.peek();
                hideFragment.onHideFragment();
                getFragmentManagerForNavigation()
                        .beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .hide(hideFragment)
                        .add(subContainerId, fragment, eTag)
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

    public boolean navigateBack(boolean withAnimation) {

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
            getFragmentManagerForNavigation()
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

    @Override
    public boolean requestBack() {
        return onNavigateBack();
    }
}
