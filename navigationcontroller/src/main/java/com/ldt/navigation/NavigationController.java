package com.ldt.navigation;

import android.animation.TimeInterpolator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.WeakHashMap;

import com.ldt.navigation.router.FlexRouter;
import com.ldt.navigation.router.Router;
import com.ldt.navigation.uicontainer.UIContainer;
import com.ldt.navigation.uicontainer.ExpandContainer;

/**
 * Created by burt on 2016. 5. 24..
 * Updated by dtrung98
 */
public class NavigationController extends NavigationFragment {

    private static final String TAG = "NavigationController";

    private Stack<NavigationFragment> mFragStack = new Stack<>();
    public  @IdRes
    int mNavContainerId;
    private int mSubContainerId;
    private final Object mSync = new Object();
    public String mControllerTag;
    private Stack<String> mTagStack = new Stack<>();
    private static int sIdCount = 1;
    private static int nextId() {
        return ++sIdCount;
    }

    public UIContainer getUiContainer() {
        return mUiContainer;
    }

    private UIContainer mUiContainer = null;
    public String getFragmentTagAt(int position) {
        if(position < getFragmentCount())
        return mTagStack.get(position);
        return null;
    }

    private WeakReference<Router> mWeakRouter;
    public Router getRouter() {
        if(mWeakRouter==null) return null;
        return mWeakRouter.get();
    }

    public NavigationController presentNavigator(String tag, Class<? extends NavigationFragment> startUpFragmentCls, Class<? extends UIContainer> uiContainerCls) {
        Router router = getRouter();
        if(router instanceof FlexRouter && getFragmentManager() != null) {
            return ((FlexRouter) router).presentNavigator(tag, getFragmentManager(), mNavContainerId, startUpFragmentCls, uiContainerCls);
        }
        return null;
    }

    public NavigationController presentNavigator(String tag, int navContainerId, Class<? extends NavigationFragment> startUpFragmentCls, Class<? extends UIContainer> uiContainerCls) {
        Router router = getRouter();
        if(router instanceof FlexRouter && getFragmentManager() != null) {
            return ((FlexRouter) router).presentNavigator(tag, getFragmentManager(), navContainerId, startUpFragmentCls, uiContainerCls);
        }
        return null;
    }

    public void setRouter(Router router) {
        mWeakRouter = new WeakReference<>(router);
    }

    public Class<? extends NavigationFragment> getStartUpFragmentClass() {
        return mStartUpFragmentCls;
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

    public final NavigationFragment findFragment(String tag) {
        int index = mTagStack.indexOf(tag);
        if(index != -1) return mFragStack.get(index);
        return null;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("nav-container-id", mNavContainerId);

        outState.putInt("sub-container-id", mSubContainerId);

        outState.putString("controller-tag", mControllerTag);
        ArrayList<String> list = new ArrayList<>(mTagStack);
        outState.putStringArrayList("fragment-navigation-tags", list);

        UIContainer.save(mControllerTag, mUiContainer.getClass());
        mUiContainer.saveState(outState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState!=null) {
            mNavContainerId = savedInstanceState.getInt("nav-container-id", -1);
            mSubContainerId = savedInstanceState.getInt("sub-container-id", R.id.sub_container);
            mControllerTag = savedInstanceState.getString("controller-tag");

            mUiContainer = UIContainer.instantiate(getContext(), mControllerTag);


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
        if (mUiContainer == null) mUiContainer = new ExpandContainer();
        mUiContainer.provideController(this, w, h, dpUnit);

        mUiContainer.created(savedInstanceState);
        mUiContainer.restoreState(savedInstanceState);
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
    public void onResume() {
        super.onResume();
        mUiContainer.resume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mUiContainer.start();
    }

    @Override
    public void onPause() {
        mUiContainer.pause();
        super.onPause();
    }

    @Override
    public void onStop() {
        mUiContainer.stop();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        mUiContainer.destroy();
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        unregisterWindowInsetsListener(mControllerTag);
        mUiContainer.destroyView();
        super.onDestroyView();
    }

    private static final int[] mWindowInsets = new int[4];

    public static int[] getWindowInsets() {
        int[] insets = new int[4];
        System.arraycopy(mWindowInsets,0, insets, 0, 4);
        return insets;
    }

    private static WeakHashMap<String, OnWindowInsetsChangedListener> sControllerWICLs = new WeakHashMap<>();
    //private static ArrayList<String> sControllerWICLKeys = new ArrayList<>();

    private static WeakReference<OnApplyWindowInsetsListener> sListener;
    private static void unregisterWindowInsetsListener(String key) {
        sControllerWICLs.remove(key);
    }

    private static void registerWindowInsetsListener(Activity activity, String key, OnWindowInsetsChangedListener listener) {
        if(key != null && !key.isEmpty() && listener != null) sControllerWICLs.put(key, listener);

        if(activity != null) {
            OnApplyWindowInsetsListener applyListener = new OnApplyWindowInsetsListener() {
                @Override
                public WindowInsetsCompat onApplyWindowInsets(View v, WindowInsetsCompat insets) {
                    int left = insets.getSystemWindowInsetLeft();
                    int top = insets.getSystemWindowInsetTop();
                    int right = insets.getSystemWindowInsetRight();
                    int bottom = insets.getSystemWindowInsetBottom();

                    if(left!=mWindowInsets[0] || top != mWindowInsets[1] || right != mWindowInsets[2] || bottom != mWindowInsets[3]) {
                        mWindowInsets[0] = left;
                        mWindowInsets[1] = top;
                        mWindowInsets[2] = right;
                        mWindowInsets[3] = bottom;

                        OnWindowInsetsChangedListener l;
                        Set<Map.Entry<String, OnWindowInsetsChangedListener>> entrySet = sControllerWICLs.entrySet();
                        for (Map.Entry<String, OnWindowInsetsChangedListener> me : entrySet) {
                            l = me.getValue();
                            if (l != null) l.onWindowInsetsChanged(left, top, right, bottom);
                        }
                    }
                    return ViewCompat.onApplyWindowInsets(v, insets);//insets.consumeSystemWindowInsets();
                }
            };

            ViewCompat.setOnApplyWindowInsetsListener(activity.getWindow().getDecorView(), applyListener);

            sListener = new WeakReference<>(applyListener);
        }
    }

    public void getWindowInsets(int[] int4) {
        System.arraycopy(mWindowInsets, 0, int4, 0, 4);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mUiContainer.activityCreated(savedInstanceState);
        registerWindowInsetsListener(getActivity(), mControllerTag, this);
    }


    @SuppressLint("RestrictedApi")
    @NonNull
    @Override
    public LayoutInflater getLayoutInflater(@Nullable Bundle savedFragmentState) {
        if(mUiContainer != null) {
            LayoutInflater inflater = mUiContainer.provideLayoutInflater(savedFragmentState);
            if(inflater!=null) return inflater;
        }
        return super.getLayoutInflater(savedFragmentState);
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

    // This flag will become true after a successful restoration
    private boolean mRestoredFragment = false;
    public boolean isRestoredFragment() {
        return mRestoredFragment;
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
        NavigationController f = findInstance(tag, fragmentManager);
        if(f==null) f = newInstance(tag, fragmentManager, navContainerId, startUpFragmentCls, uiContainerCls);
        return f;
    }

    public static NavigationController findInstance(String tag, @NonNull FragmentManager fragmentManager) {

        // find restored controller if any
        NavigationController f = (NavigationController)fragmentManager.findFragmentByTag(tag);
        if(f!=null) {

            if(f.mControllerTag ==null || f.mControllerTag.isEmpty()) f.mControllerTag = tag;

            // restore fragment stack from restored tag stack
            if(!f.isRestoredFragment()) {
                f.restoreFragmentStack();
                f.mRestoredFragment = true;
            }
        }
        return f;
    }

    protected static NavigationController newInstance(String tag, @NonNull FragmentManager fragmentManager, @IdRes int navContainerId, Class<? extends NavigationFragment> startUpFragmentCls, Class<? extends UIContainer> uiContainerCls) {
        NavigationController f = new NavigationController();
        f.mNavContainerId = navContainerId;
        f.mSubContainerId = View.generateViewId();
        f.mRestoredFragment = true; // no need to restore fragment anymore
        f.mControllerTag = tag;

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

        f.addToFragmentManager(fragmentManager);

        return f;
    }

    private void addToFragmentManager(FragmentManager fragmentManager) {
        synchronized (mSync) {
            if(mUiContainer.shouldAttachToContainerView())
                addToContainerView(fragmentManager);
            else addWithoutContainerView(fragmentManager);
        }
    }

    private void addToContainerView(FragmentManager fragmentManager) {
        fragmentManager
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .add(mNavContainerId,this , mControllerTag)
                .commit();
    }

    private void addWithoutContainerView(FragmentManager fragmentManager) {
        fragmentManager
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .add(this , mControllerTag)
                .commit();
    }

    public boolean isControllerAvailable() {
        return !isControllerRemoved;
    }

    private boolean isControllerRemoved = false;

    public void removeFromFragmentManager() {
        if(isControllerRemoved) return;
        isControllerRemoved = true;

        synchronized (mSync) {
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
        return mUiContainer.provideLayout(getContext(), inflater, container, mSubContainerId);
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
    public TimeInterpolator getInterpolator() {
        return interpolator;
    }

    public void switchAt(NavigationFragment fragment, int positionAt) {
        // no animation
        switchAt(fragment, positionAt, false);
    }

    public void switchNew(NavigationFragment fragment) {
        switchNew(fragment,false);
    }

    public void navigateBack(int positionAt, boolean animated) {

    }

    /**
     *  Xóa toàn bộ fragment từ vị trí chỉ định, và hiển thị fragment chỉ địh
     * @param fragment: Fragment
     * @param positionAt: vị trí
     */
    public void switchAt(NavigationFragment fragment, int positionAt, boolean animated) {
        // Xóa toàn bộ fragment từ vị trí đó trở định

        int count = getFragmentCount();
         // count = 0 -> gọi navigateTo
        // count = 1 -> replace
        // Hiển thị fragment chi định

        if(count == 0) {
            navigateTo(fragment, animated);
            return;
        }

        String eTag = nextNavigationFragmentTag();
        fragment.setNavigationController(this);
        fragment.setAnimatable(animated);

        FragmentTransaction ft =
                getFragmentManagerForNavigation()
                        .beginTransaction();
        if(animated)
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);

      /*  if(count == 1) {
            // phát sinh tag
            ft.replace(mSubContainerId, fragment, eTag).commit();
            mTagStack.clear();
            mFragStack.clear();

        } else */{
            int pos = Math.min(positionAt, count - 1);
            NavigationFragment removeFragment;
            for (int i = count - 1; i >= pos ; i--) {
                removeFragment = getFragmentAt(i);
                removeFragment.setAnimatable(animated);
                ft.remove(removeFragment);
                mTagStack.remove(i);
                mFragStack.remove(i);
            }
            ft.add(mSubContainerId, fragment, eTag).commit();
        }

        mTagStack.push(eTag);
        mFragStack.push(fragment);

    }

    public void switchNew(NavigationFragment fragment, boolean withAnimation) {
        //  Xóa toàn bộ fragment trong stack
        // Hiện thị fragment chỉ định như start fragment
        switchAt(fragment, 0, withAnimation);
    }

    public void navigateTo(NavigationFragment fragment) {
        navigateTo(fragment, true);
    }

    public void navigateTo(NavigationFragment fragment, boolean withAnimation) {

        synchronized (mSync) {

            fragment.setNavigationController(this);

            // phát sinh tag
            String tag = nextNavigationFragmentTag();

            // nếu stack rỗng
            if (mFragStack.size() == 0) {
                fragment.setAnimatable(false);
                getFragmentManagerForNavigation()
                        .beginTransaction()
                        .replace(mSubContainerId, fragment, tag)
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
                hideFragment.setAnimatable(withAnimation);
                hideFragment.onHideFragment();
                FragmentTransaction ft = getFragmentManagerForNavigation()
                        .beginTransaction();
                if(withAnimation)
                        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                        ft.hide(hideFragment)
                        .add(mSubContainerId, fragment, tag)
                        .commit();
            }
            mFragStack.add(fragment);
            mTagStack.add(tag);
        }
    }
    private boolean mIsAbleToPopRoot = false;
    public void setAbleToPopRoot(boolean able) {
        mIsAbleToPopRoot = able;
    }
    public boolean dismissFragment(NavigationFragment fragment) {
        return dismissFragment(fragment, true);
    }

    public boolean dismissFragment(NavigationFragment fragment, boolean withAnimation) {
        int count = getFragmentCount();
        int index = mFragStack.indexOf(fragment);

        // not existed in stack
        if(index == -1) return false;

        fragment.setAnimatable(false);
        // couldn't remove root fragment, please call quit() instead
        if(count <= 1) {
            return false;
        } else if(index == count - 1) {
            return navigateBack(withAnimation);
        } else {
            // not the top fragment
            mFragStack.remove(index);
            mTagStack.remove(index);
            getFragmentManagerForNavigation()
                    .beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                    .remove(fragment)
                    .commit();
            return true;
        }
    }

    @Override
    public int defaultDuration() {
        if(mUiContainer != null) return mUiContainer.defaultDuration();
        return super.defaultDuration();
    }

    @Override
    public int defaultTransition() {
        if(mUiContainer!=null) return mUiContainer.defaultTransition();
        return PresentStyle.NONE;
    }

    @Override
    public int defaultOpenExitTransition() {
        if(mUiContainer!=null) return mUiContainer.defaultDuration();
        return super.defaultOpenExitTransition();
    }

    public boolean navigateBack() {
        return navigateBack(true);
    }

    public boolean navigateBack(boolean withAnimation) {

        // mFragStack only has root fragment
        if(mFragStack.size() == 1) {

            // navigateBack whole navigation
            return false;
        }

        synchronized (mSync) {

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

    @Override
    public void onWindowInsetsChanged(int left, int top, int right, int bottom) {
        for (NavigationFragment f :
                mFragStack) {
            if(f.attachedToActivity())
            f.onWindowInsetsChanged(left, top, right, bottom);
        }
    }
}
