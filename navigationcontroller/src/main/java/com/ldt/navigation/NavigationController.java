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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
 * Upgraded by dtrung98
 */
public class NavigationController extends NavigationFragment {
    private static final String TAG = "NavigationController";
    public static final String INITIAL_FRAGMENT_TAG = "initial-fragment-tag";
    public static final String CONTROLLER_TAG = "controller-tag";
    public static final String SUB_CONTAINER_ID = "sub-container-id";
    public static final String NAV_CONTAINER_ID = "nav-container-id";
    public static final String FRAGMENT_NAVIGATION_TAGS = "fragment-navigation-tags";
    public static final String UI_CONTAINER_CLASS_NAME = "ui-container-class-name";

    private final Stack<NavigationFragment> mFragments = new Stack<>();
    public @IdRes int mNavContainerViewId;
    protected int mSubContainerViewId;
    private final Object mSync = new Object();
    public String mControllerTag;
    protected UIContainer mUiContainer = null;
    private String mInitialFragmentTag = null;

    public ControllerTransaction beginTransaction() {
        return new ControllerTransaction(this);
    }

    private static class Op {
        private final int mType;
        private final NavigationFragment mAssociationFragment;
        private Op(int type, NavigationFragment associationFragment) {
            mType = type;
            mAssociationFragment = associationFragment;
        }
    }

    private void doNavigateTo(NavigationFragment fragment, boolean animated) {
        beginTransaction().navigateTo(fragment).withAnimation(animated).executeTransaction();
    }

    private void doSwitchAt(NavigationFragment fragment, boolean animated) {
    }

    private final ControllerTransaction mPendingTransaction = new ControllerTransaction(this);

    public static class ControllerTransaction {
        private static final int OP_NAVIGATE_BACK = 0;
        private static final int OP_NAVIGATE_TO = 1;
        private static final int OP_DISMISS = 2;
        private final ArrayList<Op> mOps = new ArrayList<>();
        private boolean mAnimated = false;
        public ControllerTransaction withAnimation(boolean animated) {
            mAnimated = animated;
            return this;
        }

        /**
         * Thực thi transaction
         * <br>Đảm bảo đồng bộ với fragment stack
         * <br> Đảm bảo fragment top đang show, toàn bộ fragment còn lại đang hide
         * <br><b>Note:</b> Nếu controller chưa được attach, transaction được đưa vào pending transaction, pending được thực thi khi controller được attach
         */
        public void executeTransaction() {
            if(mOps.isEmpty()) return;
            if(!mController.isAdded()) {
                mController.mPendingTransaction.mOps.addAll(mOps);
                mController.mPendingTransaction.withAnimation(mAnimated);
                return;
            }

            // execute pending transaction before any other
            if(!mController.mPendingTransaction.mOps.isEmpty()) {
                List<Op> ops = new ArrayList<>(mOps);
                mOps.clear();
                mOps.addAll(mController.mPendingTransaction.mOps);
                mOps.addAll(ops);
                ops.clear();
                ops = null;
            }

            FragmentTransaction transaction = mController.provideFragmentManager().beginTransaction();
            // Nhiều dismiss ?
            // Nhiều navigate ?
            // Nhiều back ?

            // dismiss = remove fragment
            // navigate = add
            // back = remove

            // commit = transaction + show top fragment

            String topTag = (mController.mFragments.isEmpty()) ? null : mController.getTopFragment().getIdentifyTag();
            int opCount = mOps.size();
            Op op;
            for (int i = 0; i < opCount; i++) {
                op = mOps.get(i);
                switch (op.mType) {
                    case OP_DISMISS:
                        NavigationFragment f = mController.findFragment(op.mAssociationFragment.getIdentifyTag());
                        if(f != null) {
                            transaction.remove(op.mAssociationFragment).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
                            mController.mFragments.remove(f);
                        }
                        break;
                    case OP_NAVIGATE_BACK:
                        if(mController.getFragmentCount() != 0) {
                            NavigationFragment top = mController.getTopFragment();
                            transaction.remove(top).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
                            mController.mFragments.remove(top);
                        }
                        break;
                    case OP_NAVIGATE_TO:
                        NavigationFragment addedFragment = op.mAssociationFragment;
                        addedFragment.setAnimatable(mAnimated);
                        addedFragment.setNavigationController(mController);
                        NavigationFragment prevFragment = (mController.mFragments.isEmpty()) ? null : mController.getTopFragment();
                        int openExit = addedFragment.defaultOpenExitTransition();

                        if(prevFragment != null) {
                            if (openExit == PresentStyle.SAME_AS_OPEN)
                                prevFragment.overrideOpenExitCloseEnterTransition(
                                        addedFragment.getOpenEnterPresentStyle(),
                                        addedFragment.defaultDuration(),
                                        addedFragment.defaultInterpolator());
                            else if (openExit != PresentStyle.SELF_DEFINED)
                                prevFragment.overrideOpenExitCloseEnterTransition(
                                        PresentStyle.inflate(openExit),
                                        addedFragment.defaultDuration(),
                                        addedFragment.defaultInterpolator());
                            else prevFragment.clearOpenExitCloseEnterTransition();
                            prevFragment.setAnimatable(mAnimated);
                        }
                        transaction.add(mController.mSubContainerViewId, op.mAssociationFragment, op.mAssociationFragment.getIdentifyTag())
                        .hide(op.mAssociationFragment).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                        mController.mFragments.add(op.mAssociationFragment);
                        break;
                }
            }

            // show the top fragment
            if(mController.getFragmentCount() != 0) {
                String newTop = mController.getTopFragment().getIdentifyTag();
                if(newTop != null && !newTop.equals(topTag)) {
                    NavigationFragment oldTop = mController.findFragment(topTag);
                    if(oldTop != null) transaction.hide(oldTop);
                    transaction.show(mController.getTopFragment());
                }
            }

            transaction.commit();
            mController.provideFragmentManager().executePendingTransactions();
        }

        /**
         * Dismiss
         * <br>Remove fragment chỉ định khỏi controller
         * @param fragment
         * @return
         */
        public ControllerTransaction dismiss(NavigationFragment fragment) {
            mOps.add(new Op(OP_DISMISS, fragment));
            return this;
        }

        /**
         * Navigate back
         * <br>Remove fragment top hiện tại khỏi controller
         * @return
         */
        public ControllerTransaction navigateBack() {
            mOps.add(new Op(OP_NAVIGATE_BACK, null));
            return this;
        }

        /**
         * Navigate to
         * <br>Add và show fragment chỉ định
         * <br> Hide fragment hiện đang show
         * @param fragment
         * @return
         */
        public ControllerTransaction navigateTo(NavigationFragment fragment) {
            mOps.add(new Op(OP_NAVIGATE_TO, fragment));
            return this;
        }

        final NavigationController mController;
        ControllerTransaction(NavigationController controller) {
            mController = controller;
        }
        public Stack<NavigationFragment> getFragments() {
            return mController.mFragments;
        }

        public Stack<NavigationFragment> getPendingFragments() {
            return mController.mPendingFragments;
        }
    }

    /**
     * Check if the current root fragment is the initial fragment. The initial fragment is the root fragment created the same time with controller)
     * @return
     */
    public boolean isInitialFragmentRootFragment() {

        return !mFragments.isEmpty() && mInitialFragmentTag != null && mInitialFragmentTag.equals(mFragments.get(0).getTag());
    }

    public Fragment getRootFragment() {
        return !mPendingFragments.isEmpty() ? mPendingFragments.get(0) : !mFragments.isEmpty() ? mFragments.get(0) : null;
    }

    protected final Stack<NavigationFragment> mPendingFragments = new Stack<>();

    public UIContainer getUiContainer() {
        return mUiContainer;
    }

    public String getFragmentTagAt(int position) {
        if(position < getFragmentCount())
        return mFragments.get(position).getIdentifyTag();
        return null;
    }

    private WeakReference<Router> mWeakRouter;
    public Router getRouter() {
        if(mWeakRouter==null) return null;
        return mWeakRouter.get();
    }

    public NavigationController presentFragmentInNewController(String controllerTag, Class<? extends UIContainer> uiContainerCls, Class<? extends NavigationFragment> initialFragmentClass, @Nullable Bundle initialFragmentArgument) {
        Router router = getRouter();
        if(router instanceof FlexRouter) {
            return ((FlexRouter) router).presentController(controllerTag, mNavContainerViewId, uiContainerCls, initialFragmentClass, initialFragmentArgument);
        }
        return null;
    }

    public NavigationController presentFragmentInNewController(String controllerTag, int navContainerId, Class<? extends UIContainer> uiContainerCls, Class<? extends NavigationFragment> initialFragmentClass, @Nullable Bundle initialFragmentArgument) {
        Router router = getRouter();
        if(router instanceof FlexRouter) {
            return ((FlexRouter) router).presentController(controllerTag, navContainerId,  uiContainerCls, initialFragmentClass, initialFragmentArgument);
        }
        return null;
    }

    public NavigationController presentFragmentInNewController(String controllerTag, Class<? extends UIContainer> uiContainerCls, NavigationFragment startUpFragment) {
        Router router = getRouter();
        if(router instanceof FlexRouter) {
            return ((FlexRouter) router).presentController(controllerTag, mNavContainerViewId, uiContainerCls, startUpFragment);
        }
        return null;
    }

    public NavigationController presentFragmentInNewController(String controllerTag, int navContainerViewId, Class<? extends UIContainer> uiContainerCls, NavigationFragment fragments) {
        Router router = getRouter();
        if(router instanceof FlexRouter) {
            return ((FlexRouter) router).presentController(controllerTag, navContainerViewId,  uiContainerCls, fragments);
        }
        return null;
    }

    public void setRouter(Router router) {
        mWeakRouter = new WeakReference<>(router);
    }

    public FragmentManager provideFragmentManager() {
        return getChildFragmentManager();
    }

    public final NavigationFragment getFragmentAt(int i) {
        return mFragments.get(i);
    }

    public final NavigationFragment findFragment(String tag) {
        if(tag == null) return null;
        for (NavigationFragment f :
                mFragments) {
            if(tag.equals(f.getIdentifyTag())) return f;
        }
        return null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState!=null) {
            mNavContainerViewId = savedInstanceState.getInt(NAV_CONTAINER_ID, -1);
            mSubContainerViewId = savedInstanceState.getInt(SUB_CONTAINER_ID, R.id.sub_container);
            mControllerTag = savedInstanceState.getString(CONTROLLER_TAG);
            mInitialFragmentTag = savedInstanceState.getString(INITIAL_FRAGMENT_TAG, mInitialFragmentTag);
            String uiContainerClassName = savedInstanceState.getString(UI_CONTAINER_CLASS_NAME);

            if(uiContainerClassName != null)
            mUiContainer = UIContainer.instantiate(getContext(), uiContainerClassName);

            mFragmentsNeedToRestore = savedInstanceState.getStringArrayList(FRAGMENT_NAVIGATION_TAGS);
        }

        int w = (getContext() == null) ? 1 : getContext().getResources().getConfiguration().screenWidthDp;

        int h = (getContext() == null) ? 1 :  getContext().getResources().getConfiguration().screenHeightDp;

        float dpUnit = (getContext() == null) ? 1 :  getContext().getResources().getDimension(R.dimen.dpUnit);
        if (mUiContainer == null) mUiContainer = new ExpandContainer();
        mUiContainer.provideQualifier(this, w, h, dpUnit);

        mUiContainer.created(this, savedInstanceState);
        mUiContainer.restoreState(this, savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(NAV_CONTAINER_ID, mNavContainerViewId);

        outState.putInt(SUB_CONTAINER_ID, mSubContainerViewId);

        outState.putString(CONTROLLER_TAG, mControllerTag);

        if(mUiContainer != null)
        outState.putString(UI_CONTAINER_CLASS_NAME, mUiContainer.getClass().getName());

        ArrayList<String> list = new ArrayList<>();
        for (NavigationFragment f :
                mFragments) {
            list.add(f.getIdentifyTag());
        }

        outState.putStringArrayList(FRAGMENT_NAVIGATION_TAGS, list);
        outState.putString(INITIAL_FRAGMENT_TAG, mInitialFragmentTag);

        if(mUiContainer != null)
        UIContainer.save(mUiContainer.getClass().getName(), mUiContainer.getClass());

        if(mUiContainer != null)
        mUiContainer.saveState(this, outState);
    }

    private void initialize() {
        boolean initNoRestore = mFragments.isEmpty() && (mFragmentsNeedToRestore == null || mFragmentsNeedToRestore.isEmpty());
        if(!mPendingFragments.isEmpty()) {
            /*There're some pending fragment in stack */
            for (NavigationFragment fragment :
                    mPendingFragments) {
                navigateTo(fragment);
            }
            mPendingFragments.clear();
        }

        if(initNoRestore)
            mInitialFragmentTag = mFragments.get(0).getTag();
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

    private List<String> mFragmentsNeedToRestore = null;

    protected void restoreFragmentStack() {
        FragmentManager fm = provideFragmentManager();

        if(fm==null || mFragmentsNeedToRestore == null) return;
        int size = mFragmentsNeedToRestore.size();
        String t;
        NavigationFragment f;
        mFragments.clear();
        for(int i = 0; i < size; i++) {
            t = mFragmentsNeedToRestore.get(i);
            f = (NavigationFragment)fm.findFragmentByTag(t);
            if(f != null) {
                f.setNavigationController(this);
                mFragments.push(f);
            }
        }
        mFragmentsNeedToRestore = null;
        if(mUiContainer != null) mUiContainer.stackChanged(this);
    }

    // This flag will become true after a successful restoration
    protected boolean mRestoredFragment = false;
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
        if(f == null) f = newInstance(NavigationController.class, tag, fragmentManager, navContainerViewId, uiContainerClass, null, initialFragment);
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
        if(f == null) f = newInstance(NavigationController.class, tag, fragmentManager, navContainerId,uiContainerCls, initialFragments);
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
     * Create new controller by calling {@link NavigationController#newInstance(Class, String, FragmentManager, int, Class, NavigationFragment...)} #
     * with an initial fragment instantiated from provided fragment class
     * @param controllerTag the unique tag of the controller
     * @param fragmentManager fragment manager that manages controller
     * @param navContainerViewId container view that host the controller
     * @param uiContainerClass ui container manages controller' UI
     * @param initialFragmentClass the initial fragment
     * @return controller
     */
    public static NavigationController newInstance(String controllerTag,
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
        return newInstance(NavigationController.class, controllerTag, fragmentManager, navContainerViewId, uiContainerClass, initialFragment);
    }

    public static <T extends NavigationController> T newInstance(Class<T> controllerClass,
                                                                String controllerTag,
                                                      @NonNull FragmentManager fragmentManager,
                                                      @IdRes int navContainerViewId,
                                                      Class<? extends UIContainer> uiContainerClass,
                                                      NavigationFragment... initialFragments ) {

        T controller = null;
        try {
            controller = controllerClass.newInstance();
        } catch (Exception ignored) {};
        if(controller == null) throw new IllegalArgumentException("Navigation Controller requires empty constructor");

        controller.mNavContainerViewId = navContainerViewId;
        controller.mSubContainerViewId = View.generateViewId();
        controller.mRestoredFragment = true; // no need to restore fragment anymore
        controller.mControllerTag = controllerTag;

        UIContainer uic = null;
        try {
            uic = uiContainerClass.newInstance();
        } catch (Exception ignored) {}

        if(uic == null) {
            uic = new ExpandContainer();
            Log.e(TAG, "Can not create new ui container object, will use default container instead");
        }

        controller.mUiContainer = uic;

        if(initialFragments == null || initialFragments.length == 0) throw new IllegalArgumentException("Navigation Controller requires at least one fragment object as initial fragment");

        /* add initial fragments */
            controller.mPendingFragments.clear();
            controller.mPendingFragments.addAll(Arrays.asList(initialFragments));

        /* add controller to host */
        controller.addThenCommitToFragmentManager(fragmentManager);

        return controller;
    }

    protected void addThenCommitToFragmentManager(FragmentManager fragmentManager) {
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
                .commitNow();
    }

    private void addWithoutContainerView(FragmentManager fragmentManager) {
        fragmentManager
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .add(this , mControllerTag)
                .commitNow();
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
                        .commitNow();
        }
    }

    public void quit() {
        Router router = getRouter();
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
        switchAt(null, fragment, positionAt, false);
    }

    /**
     * Loại bỏ toàn bộ fragment
     * + Thêm một fragment root
     * @param fragment
     */
    public void switchNew(NavigationFragment fragment) {
        switchNew(null, fragment,false);
    }

    /**
     *  Xóa toàn bộ fragment từ vị trí chỉ định, và hiển thị fragment
     * @param fragment: Fragment
     * @param positionAt: vị trí
     */
    public void switchAt(@Nullable Object sender, NavigationFragment fragment, int positionAt, boolean animated) {

        /* đếm xem có bao nhiêu fragment */
        int count = getFragmentCount();

        /* không có fragment nào, chỉ việc navigate to fragment */
        if(count == 0|| !isAdded()) {
            navigateTo(sender, fragment, animated);
            return;
        }

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
            mFragments.remove(i);
        }
        ft.add(mSubContainerViewId, fragment, fragment.getIdentifyTag()).commit();
        provideFragmentManager().executePendingTransactions();

        mFragments.push(fragment);
        if(mUiContainer!=null) mUiContainer.stackChanged(this);
    }

    /**
     * Remove all fragments in stack
     * + add a new root fragment
     * @param fragment root fragment
     * @param withAnimation execute with animation
     */
    public void switchNew(@Nullable Object sender, NavigationFragment fragment, boolean withAnimation) {
        //  Xóa toàn bộ fragment trong stack
        // Hiện thị fragment chỉ định như start fragment
        switchAt(sender, fragment, 0, withAnimation);
    }

    public void navigateTo(NavigationFragment fragment) {
        navigateTo(null, fragment, true);
    }

    public void navigateTo(@Nullable Object sender, NavigationFragment fragmentToPush, boolean withAnimation) {

        synchronized (mSync) {

            /* add fragment to pending list, this fragment will be added after a success initialization */
            if(!isAdded()) {
            mPendingFragments.add(fragmentToPush);
            return;
            }

            fragmentToPush.setNavigationController(this);

            if (mFragments.size() == 0) {
                fragmentToPush.setAnimatable(false);
                provideFragmentManager()
                        .beginTransaction()
                        .replace(mSubContainerViewId, fragmentToPush, fragmentToPush.getIdentifyTag())
                        .commit();
                provideFragmentManager().executePendingTransactions();

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
                        .add(mSubContainerViewId, fragmentToPush, fragmentToPush.getIdentifyTag())
                        .commit();
                provideFragmentManager().executePendingTransactions();
            }
            mFragments.add(fragmentToPush);
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
            provideFragmentManager()
                    .beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                    .remove(fragment)
                    .commit();
            provideFragmentManager().executePendingTransactions();
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

        // mFragStack has only the root fragment
        if(mFragments.size() == 1) {

            // dismiss navigation
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
            provideFragmentManager().executePendingTransactions();
        }

        if(mUiContainer != null) mUiContainer.stackChanged(this);

        return true;
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
