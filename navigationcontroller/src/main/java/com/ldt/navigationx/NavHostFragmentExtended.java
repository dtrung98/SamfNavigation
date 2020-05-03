package com.ldt.navigationx;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NavigationRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.fragment.NavHostFragment;

import com.ldt.navigation.R;
import com.ldt.navigation.uicontainer.ExpandContainer;
import com.ldt.navigation.uicontainer.UIContainer;

/*
Lớp mở rộng của NavHostFragment
<br> Có khả năng xử lý navigation-in-fragment (thẻ nav-fragment trong file graph)
 */
public class NavHostFragmentExtended extends NavHostFragment {
    private static final String TAG = "NavHostFragmentExtended";
    private static final String KEY_GRAPH_ID = "android-support-nav:fragment:graphId";
    private static final String KEY_START_DESTINATION_ARGS =
            "android-support-nav:fragment:startDestinationArgs";
    public static final String NAV_HOST_FRAGMENT_ID = "nav-host-fragment:id";
    public static final String NAV_HOST_FRAGMENT_UI_CONTAINER = "nav-host-fragment:ui-container";

    public UIContainer getUiContainer() {
        return mUiContainer;
    }

    private UIContainer mUiContainer = null;

    public void setUIContainerName(String UIContainerName) {
        mUIContainerName = UIContainerName;
    }

    private String mUIContainerName;

    /**
     * Create a new NavHostFragment instance with an inflated {@link NavGraph} resource.
     *
     * @param graphResId resource id of the navigation graph to inflate
     * @return a new NavHostFragment instance
     */
    @NonNull
    public static <T extends NavHostFragmentExtended> T create(Class<T> navHostClass, @NavigationRes int graphResId) {
        return create(navHostClass, graphResId, null);
    }

    /**
     * Create a new NavHostFragment instance with an inflated {@link NavGraph} resource.
     *
     * @param graphResId resource id of the navigation graph to inflate
     * @param startDestinationArgs arguments to send to the start destination of the graph
     * @return a new NavHostFragment instance
     */
    @NonNull
    public static <T extends NavHostFragmentExtended> T create(Class<T> navHostClass, @NavigationRes int graphResId,
                                         @Nullable Bundle startDestinationArgs) {
        Bundle b = null;
        if (graphResId != 0) {
            b = new Bundle();
            b.putInt(KEY_GRAPH_ID, graphResId);
        }
        if (startDestinationArgs != null) {
            if (b == null) {
                b = new Bundle();
            }
            b.putBundle(KEY_START_DESTINATION_ARGS, startDestinationArgs);
        }

        final T result;
        try {
            result = navHostClass.newInstance();
        } catch (Exception e) {
            throw new IllegalArgumentException("NavHostFragment requires empty constructor");
        }

        if (b != null) {
            result.setArguments(b);
        }
        return result;
    }

    @Override
    protected void onCreateNavController(@NonNull NavController navController) {
        navController.getNavigatorProvider().addNavigator(
                new NavHostFragmentNavigator(requireContext(), getChildFragmentManager(),
                getContainerId()));
        super.onCreateNavController(navController);

    }

    /**
     * We specifically can't use {@link View#NO_ID} as the container ID (as we use
     * {@link androidx.fragment.app.FragmentTransaction#add(int, Fragment)} under the hood),
     * so we need to make sure we return a valid ID when asked for the container ID.
     *
     * @return a valid ID to be used to contain child fragments
     */
    private int getContainerId() {
        int id = getId();
        if (id != 0 && id != View.NO_ID) {
            return id;
        }
        // Fallback to using our own ID if this Fragment wasn't added via
        // add(containerViewId, Fragment)
        return R.id.nav_host_fragment_container;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentContainerView containerView = new FragmentContainerView(inflater.getContext());
        // When added via XML, this has no effect (since this FragmentContainerView is given the ID
        // automatically), but this ensures that the View exists as part of this Fragment's View
        // hierarchy in cases where the NavHostFragment is added programmatically as is required
        // for child fragment transactions
        containerView.setId(getContainerId());
        View boundView = mUiContainer.onCreateLayout(getContext(), inflater, container, R.id.sub_container);
        ViewGroup parentOfContainerView = boundView.findViewById(R.id.sub_container);
        if(parentOfContainerView == null) throw new IllegalArgumentException("Bound view doesn't contain the given id view");
        parentOfContainerView.addView(containerView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        return boundView;
    }

    private static int sIdCount = 1;
    private int mHostId;
    private static int nextId() {
        return ++sIdCount;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState !=null) {
            /* Restore Host Id */
            mHostId = savedInstanceState.getInt(NAV_HOST_FRAGMENT_ID, 0);
        }

        if(mHostId ==0) mHostId = nextId();
        Log.d(TAG, "onCreated nav host id "+ mHostId +", graph "+getNavController().getGraph().getDisplayName());
        onCreateUIContainer(savedInstanceState);
    }

    protected void onCreateUIContainer(@Nullable Bundle savedInstanceState) {
        if(savedInstanceState != null)  mUIContainerName = savedInstanceState.getString(NAV_HOST_FRAGMENT_UI_CONTAINER, null);

        if(mUIContainerName != null)
        mUiContainer =  NavUtil.findUIContainer(requireContext(), mUIContainerName);
        if (mUiContainer == null) mUiContainer = new ExpandContainer();

        int w = (getContext() == null) ? 1 : getContext().getResources().getConfiguration().screenWidthDp;

        int h = (getContext() == null) ? 1 :  getContext().getResources().getConfiguration().screenHeightDp;

        float dpUnit = (getContext() == null) ? 1 :  getContext().getResources().getDimension(R.dimen.dpUnit);
        mUiContainer.provideQualifier(this, w, h, dpUnit);

        mUiContainer.created(this, savedInstanceState);
        mUiContainer.restoreState(this, savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(NAV_HOST_FRAGMENT_ID, mHostId);
        outState.putString(NAV_HOST_FRAGMENT_UI_CONTAINER, mUIContainerName);
    }

    private String getUIContainerName() {
        return mUIContainerName;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "destroy nav host id "+ mHostId);
        mUiContainer.destroy(this);
        super.onDestroy();
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
    public void onDestroyView() {
        mUiContainer.destroyView(this);
        super.onDestroyView();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mUiContainer.bindLayout(view);
    }

    @Override
    public Animator onCreateAnimator(int transit, boolean enter, int nextAnim) {
        Animator containerAnimator = (nextAnim == 0) ? null : AnimatorInflater.loadAnimator(requireContext(), nextAnim);
        if(mUiContainer != null) mUiContainer.executeAnimator(containerAnimator, transit, enter, nextAnim);
        Animator delayedAnimator = AnimatorInflater.loadAnimator(requireContext(), R.animator.none);
        delayedAnimator.setDuration(containerAnimator==null ? 0 : containerAnimator.getDuration());
        return delayedAnimator;
    }
}
