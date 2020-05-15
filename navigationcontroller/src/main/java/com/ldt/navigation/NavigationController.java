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
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.WeakHashMap;

import com.ldt.navigation.router.Router;
import com.ldt.navigation.router.BaseRouter;
import com.ldt.navigation.uicontainer.UIContainer;
import com.ldt.navigation.uicontainer.ExpandContainer;

/**
 * Created by burt on 2016. 5. 24..
 * Upgraded by dtrung98
 */
public class NavigationController extends NavigationFragment {
    private static final String TAG = "NavigationController";
    private static int sIdCount = 1;

    private static int nextId() {
        return ++sIdCount;
    }

    private Stack<NavigationFragment> mFragments = new Stack<>();
    private Stack<String> mFragmentTags = new Stack<>();

    public @IdRes int mNavContainerViewId;
    private int mSubContainerViewId;
    private final Object mSync = new Object();
    public String mControllerTag;
    private UIContainer mUiContainer = null;

    private Stack<NavigationFragment> mPendingFragments = new Stack<>();

    public UIContainer getUiContainer() {
        return mUiContainer;
    }

    public String getFragmentTagAt(int position) {
        if(position < getFragmentCount())
        return mFragmentTags.get(position);
        return null;
    }

    private WeakReference<BaseRouter> mWeakRouter;
    public BaseRouter getRouter() {
        if(mWeakRouter==null) return null;
        return mWeakRouter.get();
    }

    public NavigationController presentFragmentInNewController(String controllerTag, Class<? extends NavigationFragment> startUpFragmentCls, Class<? extends UIContainer> uiContainerCls) {
        BaseRouter router = getRouter();
        if(router instanceof Router) {
            return ((Router) router).presentController(controllerTag, mNavContainerViewId, startUpFragmentCls, uiContainerCls);
        }
        return null;
    }

    public NavigationController presentFragmentInNewController(String controllerTag, int navContainerId, Class<? extends NavigationFragment> startUpFragmentCls, Class<? extends UIContainer> uiContainerCls) {
        BaseRouter router = getRouter();
        if(router instanceof Router) {
            return ((Router) router).presentController(controllerTag, navContainerId, startUpFragmentCls, uiContainerCls);
        }
        return null;
    }

    public void setRouter(BaseRouter router) {
        mWeakRouter = new WeakReference<>(router);
    }

    public FragmentManager provideFragmentManager() {
        return getChildFragmentManager();
    }

    public static String retrieveControllerTag(String tag) {
        return "com.ldt.navigation.fragment:"+"-controller";
    }

    private static String generateNavigationFragmentTag() {
        return "com.ldt.navigation.fragment:"+nextId();
    }
    public final NavigationFragment getFragmentAt(int i) {
        return mFragments.get(i);
    }

    public final NavigationFragment findFragment(String tag) {
        int index = mFragmentTags.indexOf(tag);
        if(index != -1) return mFragments.get(index);
        return null;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("nav-container-id", mNavContainerViewId);

        outState.putInt("sub-container-id", mSubContainerViewId);

        outState.putString("controller-tag", mControllerTag);
        ArrayList<String> list = new ArrayList<>(mFragmentTags);
        outState.putStringArrayList("fragment-navigation-tags", list);

        UIContainer.save(mControllerTag, mUiContainer.getClass());
        mUiContainer.saveState(this, outState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState!=null) {
            mNavContainerViewId = savedInstanceState.getInt("nav-container-id", -1);
            mSubContainerViewId = savedInstanceState.getInt("sub-container-id", R.id.sub_container);
            mControllerTag = savedInstanceState.getString("controller-tag");

            mUiContainer = UIContainer.instantiate(getContext(), mControllerTag);

            ArrayList<String> list;
            list = savedInstanceState.getStringArrayList("fragment-navigation-tags");
            if (list != null) {
                mFragmentTags.clear();
                mFragmentTags.addAll(list);
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

    private void initialize() {
        boolean initNoRestore = mFragmentTags.isEmpty();
        if(initNoRestore && !mPendingFragments.isEmpty()) {
            /* this controller is init without restored state and there're some pending fragment in stack */
            for (NavigationFragment fragment :
                    mPendingFragments) {
                navigateTo(fragment);
            }
            mPendingFragments.clear();
        } /* else does nothing */
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
    public final int[] mNavWindowInsets = new int[] {0,0,0,0};

    public static int[] getWindowInsets() {
        int[] insets = new int[4];
        System.arraycopy(mWindowInsets,0, insets, 0, 4);
        return insets;
    }

    private static WeakHashMap<String, WindowInsetsListener> sControllerWICLs = new WeakHashMap<>();
    //private static ArrayList<String> sControllerWICLKeys = new ArrayList<>();

    private static WeakReference<OnApplyWindowInsetsListener> sListener;
    private static void unregisterWindowInsetsListener(String key) {
        sControllerWICLs.remove(key);
    }

    private static void registerWindowInsetsListener(Activity activity, String key, WindowInsetsListener listener) {
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

                        WindowInsetsListener l;
                        Set<Map.Entry<String, WindowInsetsListener>> entrySet = sControllerWICLs.entrySet();
                        for (Map.Entry<String, WindowInsetsListener> me : entrySet) {
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

    public void getNavWindowInsets(int[] int4) {
        System.arraycopy(mNavWindowInsets, 0, int4, 0, 4);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        registerWindowInsetsListener(requireActivity(), mControllerTag, this);
        mUiContainer.activityCreated(this, savedInstanceState);
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
        FragmentManager fm = provideFragmentManager();

        if(fm==null) return;
        int size = mFragmentTags.size();
        String t;
        NavigationFragment f;
        mFragments.clear();
        for(int i = 0; i < size; i++) {
            t = mFragmentTags.elementAt(i);
            f = (NavigationFragment)fm.findFragmentByTag(t);
            if(f != null) {
                f.setNavigationController(this);
                mFragments.push(f);
            }
        }
        if(mUiContainer != null) mUiContainer.stackChanged(this);
    }

    // This flag will become true after a successful restoration
    private boolean mRestoredFragment = false;
    public boolean isRestoredFragment() {
        return mRestoredFragment;
    }

    public static NavigationController createNewOrGetController(
            @NonNull String tag,
            @NonNull FragmentManager fragmentManager,
            @IdRes int navContainerViewId,
            Class<? extends UIContainer> uiContainerClass,
            NavigationFragment initialFragment)
    {
        NavigationController f = findInstance(tag, fragmentManager);
        if(f == null) f = newInstance(tag, fragmentManager, navContainerViewId, uiContainerClass, null, initialFragment);
        return f;
    }

    public static NavigationController createNewOrGetController(
            @NonNull String controllerTag,
            @NonNull FragmentManager fragmentManagerThatHostsController,
            @IdRes int navContainerViewId,
            @NonNull Class<? extends UIContainer> uiContainerClass,
            @NonNull Class<? extends NavigationFragment> initialFragmentClass,
            @Nullable Bundle initialFragmentArguments)
    {
        NavigationController f = findInstance(controllerTag, fragmentManagerThatHostsController);
        if(f == null) f = newInstance(controllerTag, fragmentManagerThatHostsController, navContainerViewId,uiContainerClass, initialFragmentClass, initialFragmentArguments);
        return f;
    }

    public static NavigationController createNewOrGetController(
            @NonNull String tag,
            @NonNull FragmentManager fragmentManager,
            @IdRes int navContainerId,
            Class<? extends UIContainer> uiContainerCls,
            NavigationFragment... initialFragments)
    {
        NavigationController f = findInstance(tag, fragmentManager);
        if(f == null) f = newInstance(tag, fragmentManager, navContainerId,uiContainerCls, initialFragments);
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

    /**
     * Create new controller by calling {@link NavigationController#newInstance(String, FragmentManager, int, Class, NavigationFragment...)} with an initial fragment instantiated from provided fragment class
     * @param controllerTag the unique tag of the controller
     * @param fragmentManager fragment manager that manages controller
     * @param navContainerViewId container view that host the controller
     * @param uiContainerClass ui container manages controller' UI
     * @param initialFragmentClass the initial fragment
     * @return controller
     */
    protected static NavigationController newInstance(String controllerTag,
                                                      @NonNull FragmentManager fragmentManager,
                                                      @IdRes int navContainerViewId,
                                                      Class<? extends UIContainer> uiContainerClass,
                                                      Class<? extends NavigationFragment> initialFragmentClass,
                                                      @Nullable Bundle initialFragmentArgument) {
        NavigationFragment initialFragment = null;
        try {
            initialFragment = initialFragmentClass.newInstance();
        } catch (Exception ignored) {}

        if(initialFragment ==null)
            throw new IllegalArgumentException("Unable to create new instance from initial fragment class");

        initialFragment.setArguments(initialFragmentArgument);
        return newInstance(controllerTag, fragmentManager, navContainerViewId, uiContainerClass, initialFragment);
    }

    protected static NavigationController newInstance(String controllerTag,
                                                      @NonNull FragmentManager fragmentManager,
                                                      @IdRes int navContainerViewId,
                                                      Class<? extends UIContainer> uiContainerClass,
                                                      NavigationFragment... initialFragments ) {
        NavigationController f = new NavigationController();
        f.mNavContainerViewId = navContainerViewId;
        f.mSubContainerViewId = View.generateViewId();
        f.mRestoredFragment = true; // no need to restore fragment anymore
        f.mControllerTag = controllerTag;

        UIContainer uic = null;
        try {
            uic = uiContainerClass.newInstance();
        } catch (Exception ignored) {}

        if(uic == null) {
            uic = new ExpandContainer();
            Log.e(TAG, "Can not create new ui container object, will use default container instead");
        }

        f.mUiContainer = uic;

        if(initialFragments == null || initialFragments.length == 0) throw new IllegalArgumentException("Navigation Controller requires at least one fragment object as initial fragment");

        /* add initial fragments */
            f.mPendingFragments.clear();
            f.mPendingFragments.addAll(Arrays.asList(initialFragments));

        /* add controller to host */
        f.addThenCommitToFragmentManager(fragmentManager);

        return f;
    }

    private void addThenCommitToFragmentManager(FragmentManager fragmentManager) {
        synchronized (mSync) {
            if(mUiContainer.shouldAttachToContainerView())
                addWithContainerView(fragmentManager);
            else addWithoutContainerView(fragmentManager);
        }
    }

    private void addWithContainerView(FragmentManager fragmentManager) {
        fragmentManager
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .add(mNavContainerViewId,this , mControllerTag)
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
        BaseRouter router = getRouter();
        if(router != null) router.finishController(this);
    }

    public NavigationFragment getTopFragment() {
        if(mFragments.size() != 0)return  mFragments.lastElement();
        return null;
    }

    @Nullable
    @Override
    protected View onCreateContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        initialize();
        return mUiContainer.onCreateLayout(getContext(), inflater, container, mSubContainerViewId);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mUiContainer.bindLayout(view);
    }

    public int getFragmentCount() {
        return mFragments.size();
    }

    public TimeInterpolator defaultInterpolator() {
        return new AccelerateDecelerateInterpolator();
    }

    /**
     * Loại bỏ các fragment từ vị trí cho trước
     * Đồng thời thêm một fragment chỉ định
     * @param fragment
     * @param positionAt
     */
    public void switchAt(NavigationFragment fragment, int positionAt) {
        // no animation
        switchAt(fragment, positionAt, false);
    }

    /**
     * Loại bỏ toàn bộ fragment
     * + Thêm một fragment root
     * @param fragment
     */
    public void switchNew(NavigationFragment fragment) {
        switchNew(fragment,false);
    }

    /**
     *  Xóa toàn bộ fragment từ vị trí chỉ định, và hiển thị fragment
     * @param fragment: Fragment
     * @param positionAt: vị trí
     */
    public void switchAt(NavigationFragment fragment, int positionAt, boolean animated) {

        /* đếm xem có bao nhiêu fragment */
        int count = getFragmentCount();

        /* không có fragment nào, chỉ việc navigate to fragment */
        if(count == 0|| !isAdded()) {
            navigateTo(fragment, animated);
            return;
        }

        String eTag = generateNavigationFragmentTag();
        fragment.setNavigationController(this);
        fragment.setAnimatable(animated);

        FragmentTransaction ft =
                provideFragmentManager()
                        .beginTransaction();
        if(animated)
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);

        int pos = Math.min(positionAt, count - 1);
        NavigationFragment removeFragment;
        for (int i = count - 1; i >= pos ; i--) {
            removeFragment = getFragmentAt(i);
            removeFragment.setAnimatable(animated);
            ft.remove(removeFragment);
            mFragmentTags.remove(i);
            mFragments.remove(i);
        }
        ft.add(mSubContainerViewId, fragment, eTag).commit();

        mFragmentTags.push(eTag);
        mFragments.push(fragment);
        if(mUiContainer!=null) mUiContainer.stackChanged(this);
    }

    /**
     * Remove all fragments in stack
     * + add a new root fragment
     * @param fragment root fragment
     * @param withAnimation execute with animation
     */
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

            /* add fragment to pending list, this fragment will be added after a success initialization */
            if(!isAdded()) {
            mPendingFragments.add(fragmentToPush);
            return;
            }

            fragmentToPush.setNavigationController(this);

            // phát sinh tag
            String tag = generateNavigationFragmentTag();

            if (mFragments.size() == 0) {
                fragmentToPush.setAnimatable(false);
                provideFragmentManager()
                        .beginTransaction()
                        .replace(mSubContainerViewId, fragmentToPush, tag)
                        .commit();

            } else {
                int openExit = fragmentToPush.defaultOpenExitTransition();
                NavigationFragment fragmentToHide = getTopFragment();
                /* fragment to push quy định rằng, hiệu ứng open/exit fragment nằm sau giống với nó */

                if(openExit == PresentStyle.SAME_AS_OPEN)
                    fragmentToHide.overrideOpenExitCloseEnterTransition(fragmentToPush.getOpenEnterPresentStyle(), fragmentToPush.defaultDuration(), fragmentToPush.defaultInterpolator());

                /* fragment to push ép fragment nằm sau nó tuân theo hiệu ứng chỉ định */
                else if(openExit != PresentStyle.SELF_DEFINED)
                    fragmentToHide.overrideOpenExitCloseEnterTransition(PresentStyle.inflate(openExit), fragmentToPush.defaultDuration(), fragmentToPush.defaultInterpolator());
                else {
                    /* fragment nằm sau thích gì thì chạy nấy */
                    fragmentToHide.clearOpenExitCloseEnterTransition();
                }

                fragmentToPush.setAnimatable(withAnimation);
                // hide last fragment and add new fragment
                NavigationFragment hideFragment = mFragments.peek();
                hideFragment.setAnimatable(withAnimation);
                FragmentTransaction ft = provideFragmentManager()
                        .beginTransaction();
                if(withAnimation)
                        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                        ft.hide(hideFragment)
                        .add(mSubContainerViewId, fragmentToPush, tag)
                        .commit();
            }
            mFragments.add(fragmentToPush);
            mFragmentTags.add(tag);
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
        int index = mFragments.indexOf(fragment);

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
            mFragments.remove(index);
            mFragmentTags.remove(index);
            provideFragmentManager()
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
        if(mFragments.size() == 1) {

            // navigateBack whole navigation
            return false;
        }

        synchronized (mSync) {

            NavigationFragment fragmentToRemove = mFragments.pop();
            fragmentToRemove.setNavigationController(this);
            fragmentToRemove.setAnimatable(withAnimation);

            NavigationFragment fragmentToShow = mFragments.peek();
            fragmentToShow.setNavigationController(this);
            fragmentToShow.setAnimatable(withAnimation);

            int openExit = fragmentToRemove.defaultOpenExitTransition();
            /* fragment to remove quy định rằng, hiệu ứng open/exit fragment nằm sau giống với nó */
            if(openExit == PresentStyle.SAME_AS_OPEN)
                fragmentToShow.overrideOpenExitCloseEnterTransition(fragmentToRemove.getOpenEnterPresentStyle(), fragmentToRemove.defaultDuration(), fragmentToRemove.defaultInterpolator());
            else if(openExit != PresentStyle.SELF_DEFINED)
                fragmentToShow.overrideOpenExitCloseEnterTransition(PresentStyle.inflate(openExit), fragmentToRemove.defaultDuration(), fragmentToRemove.defaultInterpolator());

            provideFragmentManager()
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

        while (mFragments.size() >= 2) {
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

        /* save window inset */
        mNavWindowInsets[0] = left;
        mNavWindowInsets[1] = top;
        mNavWindowInsets[2] = right;
        mNavWindowInsets[3] = bottom;

        if(mUiContainer!=null) {
            int[] customInset = mUiContainer.onWindowInsetsChanged(this, left, top, right, bottom);
            if(customInset != null && customInset.length==4) {
                System.arraycopy(customInset, 0,mNavWindowInsets, 0, 4);
            }
        }

        for (NavigationFragment f :
                mFragments) {
            if(f.isActivityCreatedState())
            f.onWindowInsetsChanged(mNavWindowInsets[0], mNavWindowInsets[1], mNavWindowInsets[2], mNavWindowInsets[3]);
        }
    }
}
