package com.ldt.navigationx;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.CallSuper;
import androidx.annotation.IdRes;
import androidx.annotation.NavigationRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentFactory;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.FloatingWindow;
import androidx.navigation.NavDestination;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigator;
import androidx.navigation.NavigatorProvider;

import com.ldt.navigation.PresentStyle;
import com.ldt.navigation.R;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/*

 */
@Navigator.Name("nav-host-fragment")
public class NavHostFragmentNavigator extends Navigator<NavHostFragmentNavigator.Destination> {
    private static final String TAG = "NavHostFragNavigator";
    private static final String KEY_BACK_STACK_IDS = "androidx-nav-fragment:navigator:backStackIds";

    private final Context mContext;
    private final FragmentManager mFragmentManager;
    private final int mContainerId;
    private ArrayDeque<Integer> mSelfDesIdStack = new ArrayDeque<>();

    public NavHostFragmentNavigator(@NonNull Context context, @NonNull FragmentManager manager,
                             int containerId) {
        mContext = context;
        mFragmentManager = manager;
        mContainerId = containerId;
    }

    @NonNull
    @Override
    public NavHostFragmentNavigator.Destination createDestination() {
        return new NavHostFragmentNavigator.Destination(this);
    }

    private boolean compareObject(Object a, Object b) {
        return (a == b) || (a != null && a.equals(b));
    }

    /**
     * Instantiates the Fragment via the FragmentManager's
     * {@link androidx.fragment.app.FragmentFactory}.
     *
     * Note that this method is <strong>not</strong> responsible for calling
     * {@link Fragment#setArguments(Bundle)} on the returned Fragment instance.
     *
     * @param context Context providing the correct {@link ClassLoader}
     * @param fragmentManager FragmentManager the Fragment will be added to
     * @param className The Fragment to instantiate
     * @param args The Fragment's arguments, if any
     * @return A new fragment instance.
     * @deprecated Set a custom {@link androidx.fragment.app.FragmentFactory} via
     * {@link FragmentManager#setFragmentFactory(FragmentFactory)} to control
     * instantiation of Fragments.
     */
    @SuppressWarnings("DeprecatedIsStillUsed") // needed to maintain forward compatibility
    @Deprecated
    @NonNull
    public Fragment instantiateFragment(@NonNull Context context,
                                        @NonNull FragmentManager fragmentManager,
                                        @NonNull String className, @SuppressWarnings("unused") @Nullable Bundle args) {
        return fragmentManager.getFragmentFactory().instantiate(
                context.getClassLoader(), className);
    }

    /**
     * {@inheritDoc}
     * <p>
     * This method should always call
     * {@link FragmentTransaction#setPrimaryNavigationFragment(Fragment)}
     * so that the Fragment associated with the new destination can be retrieved with
     * {@link FragmentManager#getPrimaryNavigationFragment()}.
     * <p>
     * Note that the default implementation commits the new Fragment
     * asynchronously, so the new Fragment is not instantly available
     * after this call completes.
     */
    @SuppressWarnings("deprecation") /* Using instantiateFragment for forward compatibility */
    @Nullable
    @Override
    public NavDestination navigate(@NonNull NavHostFragmentNavigator.Destination destination, @Nullable Bundle args,
                                   @Nullable NavOptions navOptions, @Nullable Navigator.Extras navigatorExtras) {
        if (mFragmentManager.isStateSaved()) {
            Log.i(TAG, "Ignoring navigate() call: FragmentManager has already"
                    + " saved its state");
            return null;
        }
        String uiContainerClassName = destination.getUiContainerClassName();
        if(uiContainerClassName != null && !uiContainerClassName.isEmpty()) {
            if (uiContainerClassName.charAt(0) == '.') {
                uiContainerClassName = mContext.getPackageName() + uiContainerClassName;
            }
        }

        //TODO parse the class name and return the UIContainer, the default one is ExpandContainer

        final NavHostFragmentExtended hostFragment = NavHostFragmentExtended.create(NavHostFragmentExtended.class, destination.getGraphRes(), args);
        //hostFragment.setArguments(args);
        // TODO: Set UIContainer for host fragment
        hostFragment.setUIContainerName(uiContainerClassName);

        /*final Fragment frag = instantiateFragment(mContext, mFragmentManager,
                uiContainerClassName, args);*/
        /*frag.setArguments(args);*/
        final FragmentTransaction ft = mFragmentManager.beginTransaction();

        int enterAnim = navOptions != null ? navOptions.getEnterAnim() : -1;
        int exitAnim = navOptions != null ? navOptions.getExitAnim() : -1;
        int popEnterAnim = navOptions != null ? navOptions.getPopEnterAnim() : -1;
        int popExitAnim = navOptions != null ? navOptions.getPopExitAnim() : -1;
        if (enterAnim != -1 || exitAnim != -1 || popEnterAnim != -1 || popExitAnim != -1) {
            enterAnim = enterAnim != -1 ? enterAnim : 0;
            exitAnim = exitAnim != -1 ? exitAnim : 0;
            popEnterAnim = popEnterAnim != -1 ? popEnterAnim : 0;
            popExitAnim = popExitAnim != -1 ? popExitAnim : 0;
            ft.setCustomAnimations(enterAnim, exitAnim, popEnterAnim, popExitAnim);
        }

        //ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

        final @IdRes int destId = destination.getId();
        final boolean initialNavigation = mFragmentManager.getFragments().isEmpty();
        // TODO Build first class singleTop behavior for fragments

        final boolean isSingleTopReplacement = navOptions != null && !initialNavigation
                && navOptions.shouldLaunchSingleTop()
                && compareObject(mSelfDesIdStack.peekLast(), destId);

        boolean isAdded;
      if (!initialNavigation&& isSingleTopReplacement) {
            // Single Top means we only want one instance on the back stack
            if (mSelfDesIdStack.size() > 1) {
                // If the Fragment to be replaced is on the FragmentManager's
                // back stack, a simple replace() isn't enough so we
                // remove it from the back stack and put our replacement
                // on the back stack in its place

                /* Single Top Mode: Remove top fragment if it is the same "type" fragment with will-add fragment */

                Fragment current = mFragmentManager.findFragmentByTag(getSelfTag(mSelfDesIdStack.size(), destId));
                 if(current != null) ft.remove(current);

               /* mFragmentManager.popBackStack(
                        getSelfTag(mSelfDesIdStack.size(), mSelfDesIdStack.peekLast()),
                        FragmentManager.POP_BACK_STACK_INCLUSIVE);
                ft.addToBackStack(getSelfTag(mSelfDesIdStack.size(), destId));*/
            }
            isAdded = false;
        } else {
            //String backStackName = getSelfTag(mSelfDesIdStack.size() + 1, destId);
           // ft.addToBackStack(backStackName);
            isAdded = true;
        }

        if (navigatorExtras instanceof NavHostFragmentNavigator.Extras) {
            NavHostFragmentNavigator.Extras extras = (NavHostFragmentNavigator.Extras) navigatorExtras;
            for (Map.Entry<View, String> sharedElement : extras.getSharedElements().entrySet()) {
                ft.addSharedElement(sharedElement.getKey(), sharedElement.getValue());
            }
        }

        ft.add(mContainerId, hostFragment, getSelfTag(mSelfDesIdStack.size() +1 , destId));
        ft.setPrimaryNavigationFragment(hostFragment);
        ft.setReorderingAllowed(true);
        ft.commit();
        // The commit succeeded, update our view of the world
        if (isAdded) {
            mSelfDesIdStack.add(destId);
            return destination;
        } else {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * This method must call
     * {@link FragmentTransaction#setPrimaryNavigationFragment(Fragment)}
     * if the pop succeeded so that the newly visible Fragment can be retrieved with
     * {@link FragmentManager#getPrimaryNavigationFragment()}.
     * <p>
     * Note that the default implementation pops the Fragment
     * asynchronously, so the newly visible Fragment from the back stack
     * is not instantly available after this call completes.
     */
    @Override
    public boolean popBackStack() {
        Log.d(TAG, "popBackStack ");
        if (mSelfDesIdStack.isEmpty()) {
            return false;
        }
        if (mFragmentManager.isStateSaved()) {
            Log.i(TAG, "Ignoring popBackStack() call: FragmentManager has already"
                    + " saved its state");
            return false;
        }
        // mFragmentManager.beginTransaction().remove().commit();
        String backStackName = getSelfTag(mSelfDesIdStack.size(), mSelfDesIdStack.peekLast());
        Fragment fragment = mFragmentManager.findFragmentByTag(backStackName);
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        if(fragment != null) ft.remove(fragment);
        mSelfDesIdStack.removeLast();

        Fragment nextPrimary = (mSelfDesIdStack.isEmpty()) ? null :  mFragmentManager.findFragmentByTag(getSelfTag(mSelfDesIdStack.size(), mSelfDesIdStack.peekLast()));
        if(nextPrimary != null) ft.setPrimaryNavigationFragment(nextPrimary);

        ft.commit();
     //   mFragmentManager.popBackStack(backStackName, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        return true;
    }

    @Override
    @Nullable
    public Bundle onSaveState() {
        Bundle b = new Bundle();
        int[] backStack = new int[mSelfDesIdStack.size()];
        int index = 0;
        for (Integer id : mSelfDesIdStack) {
            backStack[index++] = id;
        }
        b.putIntArray(KEY_BACK_STACK_IDS, backStack);
        return b;
    }

    @Override
    public void onRestoreState(@Nullable Bundle savedState) {
        if (savedState != null) {
            int[] backStack = savedState.getIntArray(KEY_BACK_STACK_IDS);
            if (backStack != null) {
                mSelfDesIdStack.clear();
                for (int destId : backStack) {
                    mSelfDesIdStack.add(destId);
                }
            }
        }
    }

    @NonNull
    private String getSelfTag(int backStackIndex, Integer destId) {
        return backStackIndex + "-" + destId;
    }

    /**
     * NavDestination specific to {@link NavHostFragmentNavigator}
     */
    @NavDestination.ClassType(NavHostFragmentExtended.class)
    public static class Destination extends NavDestination implements FloatingWindow {

        private String mUiContainerClassName = "";
        private @NavigationRes int mGraphRes = 0;
        private int mPresentStyle = PresentStyle.SELF_DEFINED;

        public Destination(@NonNull NavigatorProvider navigatorProvider) {
            this(navigatorProvider.getNavigator(NavHostFragmentNavigator.class));
        }

        public Destination(@NonNull Navigator<? extends Destination> fragmentNavigator) {
            super(fragmentNavigator);
        }

        @CallSuper
        @Override
        public void onInflate(@NonNull Context context, @NonNull AttributeSet attrs) {
            super.onInflate(context, attrs);
            TypedArray a = context.getResources().obtainAttributes(attrs,
                    R.styleable.NavHostFragmentNavigator);

            String uiContainerClassName = a.getString(R.styleable.NavHostFragmentNavigator_uiContainer);
            if(uiContainerClassName != null)
                setUIContainerClassName(uiContainerClassName);

            int graph = a.getResourceId(R.styleable.NavHostFragmentNavigator_graph, 0);
            if (graph != 0) {
                setGraph(graph);
            }

            mPresentStyle = a.getInt(R.styleable.NavHostFragmentNavigator_presentStyle, PresentStyle.UNDEFINED);

            a.recycle();
        }

        /**
         * Set the UIContainer class name that being used in the Destination's NavHostFragment
         * @param className The class name of the UIContainer to inflate when you navigate to this
         *                  destination
         * @return this {@link NavHostFragmentNavigator.Destination}
         */
        public final Destination setUIContainerClassName(String className) {
            mUiContainerClassName = className;
            return this;
        }

        /**
         * Set the Fragment class name associated with this destination
         * @param graphRes The class name of the Fragment to show when you navigate to this
         *                  destination
         * @return this {@link NavHostFragmentNavigator.Destination}
         */
        @NonNull
        public final NavHostFragmentNavigator.Destination setGraph(int  graphRes) {
            mGraphRes = graphRes;
            return this;
        }

        /**
         * Gets the Graph Resource associated with this destination
         *
         * @throws IllegalStateException when no Navigation Graph was set.
         */
        public final int getGraphRes() {
            if (mGraphRes == 0) {
                throw new IllegalStateException("Graph navigation resource was not set. If the fragment doesn't navigate anywhere, use <fragment/> tag instead of <nav-host-fragment/>");
            }
            return mGraphRes;
        }

        @NonNull
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(super.toString());
            sb.append(" graph =");
            if (mGraphRes == 0) {
                sb.append("null");
            } else {
                sb.append(mGraphRes);
            }
            return sb.toString();
        }

        public String getUiContainerClassName() {
            return mUiContainerClassName;
        }
    }

    /**
     * Extras that can be passed to FragmentNavigator to enable Fragment specific behavior
     */
    public static final class Extras implements Navigator.Extras {
        private final LinkedHashMap<View, String> mSharedElements = new LinkedHashMap<>();

        Extras(Map<View, String> sharedElements) {
            mSharedElements.putAll(sharedElements);
        }

        /**
         * Gets the map of shared elements associated with these Extras. The returned map
         * is an {@link Collections#unmodifiableMap(Map) unmodifiable} copy of the underlying
         * map and should be treated as immutable.
         */
        @NonNull
        public Map<View, String> getSharedElements() {
            return Collections.unmodifiableMap(mSharedElements);
        }

        /**
         * Builder for constructing new {@link NavHostFragmentNavigator.Extras} instances. The resulting instances are
         * immutable.
         */
        public static final class Builder {
            private final LinkedHashMap<View, String> mSharedElements = new LinkedHashMap<>();

            /**
             * Adds multiple shared elements for mapping Views in the current Fragment to
             * transitionNames in the Fragment being navigated to.
             *
             * @param sharedElements Shared element pairs to add
             * @return this {@link NavHostFragmentNavigator.Extras.Builder}
             */
            @NonNull
            public NavHostFragmentNavigator.Extras.Builder addSharedElements(@NonNull Map<View, String> sharedElements) {
                for (Map.Entry<View, String> sharedElement : sharedElements.entrySet()) {
                    View view = sharedElement.getKey();
                    String name = sharedElement.getValue();
                    if (view != null && name != null) {
                        addSharedElement(view, name);
                    }
                }
                return this;
            }

            /**
             * Maps the given View in the current Fragment to the given transition name in the
             * Fragment being navigated to.
             *
             * @param sharedElement A View in the current Fragment to match with a View in the
             *                      Fragment being navigated to.
             * @param name The transitionName of the View in the Fragment being navigated to that
             *             should be matched to the shared element.
             * @return this {@link NavHostFragmentNavigator.Extras.Builder}
             * @see FragmentTransaction#addSharedElement(View, String)
             */
            @NonNull
            public NavHostFragmentNavigator.Extras.Builder addSharedElement(@NonNull View sharedElement, @NonNull String name) {
                mSharedElements.put(sharedElement, name);
                return this;
            }

            /**
             * Constructs the final {@link NavHostFragmentNavigator.Extras} instance.
             *
             * @return An immutable {@link NavHostFragmentNavigator.Extras} instance.
             */
            @NonNull
            public NavHostFragmentNavigator.Extras build() {
                return new NavHostFragmentNavigator.Extras(mSharedElements);
            }
        }
    }
}
