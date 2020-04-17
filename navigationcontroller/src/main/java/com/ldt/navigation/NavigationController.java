package com.ldt.navigation;

import android.animation.Animator;
import android.animation.AnimatorInflater;
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

    public  @IdRes int mNavContainerId;
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
        mUiContainer.saveState(this, outState);
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

        int w = (getContext() == null) ? 1 : getContext().getResources().getConfiguration().screenWidthDp;

        int h = (getContext() == null) ? 1 :  getContext().getResources().getConfiguration().screenHeightDp;

        float dpUnit = (getContext() == null) ? 1 :  getContext().getResources().getDimension(R.dimen.dpUnit);
        if (mUiContainer == null) mUiContainer = new ExpandContainer();
        mUiContainer.provideQualifier(this, w, h, dpUnit);

        mUiContainer.created(this, savedInstanceState);
        mUiContainer.restoreState(this, savedInstanceState);
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
        mUiContainer.resume(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        mUiContainer.start(this);
    }

    @Override
    public void onPause() {
        mUiContainer.pause(this);
        super.onPause();
    }

    @Override
    public void onStop() {
        mUiContainer.stop(this);
        super.onStop();
    }

    @Override
    public void onDestroy() {
        mUiContainer.destroy(this);
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        unregisterWindowInsetsListener(mControllerTag);
        mUiContainer.destroyView(this);
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
        mUiContainer.activityCreated(this, savedInstanceState);
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
        if(mUiContainer != null) mUiContainer.stackChanged(this);
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
        } catch (Exception ignored) {}

        if(uic == null) {
            uic = new ExpandContainer();
            Log.e(TAG, "Couldn't to create new UIContainer instance, using default container instead");
        }

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
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
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
        return mUiContainer.onCreateLayout(getContext(), inflater, container, mSubContainerId);
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
        if(mUiContainer!=null) mUiContainer.stackChanged(this);
    }

    public void switchNew(NavigationFragment fragment, boolean withAnimation) {
        //  Xóa toàn bộ fragment trong stack
        // Hiện thị fragment chỉ định như start fragment
        switchAt(fragment, 0, withAnimation);
    }

    public void navigateTo(NavigationFragment fragment) {
        navigateTo(fragment, true);
    }

    public void navigateTo(NavigationFragment fragmentToPush, boolean withAnimation) {

        synchronized (mSync) {

            fragmentToPush.setNavigationController(this);

            // phát sinh tag
            String tag = nextNavigationFragmentTag();

            // nếu stack rỗng
            if (mFragStack.size() == 0) {
                fragmentToPush.setAnimatable(false);
                getFragmentManagerForNavigation()
                        .beginTransaction()
                        .replace(mSubContainerId, fragmentToPush, tag)
                        .commit();

            } else {
                int openExit = fragmentToPush.defaultOpenExitTransition();
                NavigationFragment fragmentToHide = getTopFragment();
                /* fragment to push quy định rằng, hiệu ứng open/exit fragment nằm sau giống với nó */

                if(openExit==PresentStyle.SAME_AS_OPEN)
                    fragmentToHide.setSelfOpenExitPresentStyle(fragmentToPush.getOpenEnterPresentStyle());

                /* fragment to push ép fragment nằm sau nó tuân theo hiệu ứng chỉ định */
                else if(openExit != PresentStyle.SELF_DEFINED)
                    fragmentToHide.setSelfOpenExitPresentStyle(PresentStyle.inflate(openExit));
                else {
                    /* fragment nằm sau thích gì thì chạy nấy */
                }

                fragmentToPush.setAnimatable(withAnimation);
                // hide last fragment and add new fragment
                NavigationFragment hideFragment = mFragStack.peek();
                hideFragment.setAnimatable(withAnimation);
                FragmentTransaction ft = getFragmentManagerForNavigation()
                        .beginTransaction();
                if(withAnimation)
                        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                        ft.hide(hideFragment)
                        .add(mSubContainerId, fragmentToPush, tag)
                        .commit();
            }
            mFragStack.add(fragmentToPush);
            mTagStack.add(tag);
            if(mUiContainer != null) mUiContainer.stackChanged(this);
        }
    }
    public boolean dismissFragment(NavigationFragment fragment) {
        return dismissFragment(fragment, true);
    }

    /**
     *  Đóng fragment chỉ định, nếu là top fragment thì gọi hàm navigateBack()
     *  <br>Ngược lại
     */
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
            if(mUiContainer != null) mUiContainer.stackChanged(this);
            return true;
        }
    }

    @Override
    public Animator onCreateAnimator(int transit, boolean enter, int nextAnim) {
        Animator containerAnimator = super.onCreateAnimator(transit, enter, nextAnim);
        if(mUiContainer != null) mUiContainer.executeAnimator(containerAnimator, transit, enter, nextAnim);
        Animator delayedAnimator = AnimatorInflater.loadAnimator(getContext(), R.animator.none);
        delayedAnimator.setDuration(containerAnimator==null ? 0 : containerAnimator.getDuration());
        return delayedAnimator;
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

            int openExit = fragmentToRemove.defaultOpenExitTransition();
            /* fragment to remove quy định rằng, hiệu ứng open/exit fragment nằm sau giống với nó */
            if(openExit == PresentStyle.SAME_AS_OPEN)
                fragmentToShow.setSelfOpenExitPresentStyle(fragmentToRemove.getOpenEnterPresentStyle());
            else if(openExit != PresentStyle.SELF_DEFINED)
                fragmentToShow.setSelfOpenExitPresentStyle(PresentStyle.inflate(openExit));

            getFragmentManagerForNavigation()
                    .beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                    .show(fragmentToShow)
                    .remove(fragmentToRemove)
                    .commit();
        }

        if(mUiContainer != null) mUiContainer.stackChanged(this);

        return true;
    }

    public void navigateBackToRootFragment() {

        while (mFragStack.size() >= 2) {
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
        if(mUiContainer!=null) mUiContainer.onWindowInsetsChanged(this, left, top, right, bottom);
        for (NavigationFragment f :
                mFragStack) {
            if(f.attachedToActivity())
            f.onWindowInsetsChanged(left, top, right, bottom);
        }
    }
}
