package com.ldt.navigation;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentFactory;
import androidx.fragment.app.FragmentManager;

import com.ldt.navigation.container.ContainerNavigatorImpl;
import com.ldt.navigation.container.NavigatorAttribute;
import com.ldt.navigation.effectview.EffectFrameLayout;
import com.ldt.navigation.uicontainer.ExpandStaticContainer;
import com.ldt.navigation.uicontainer.UIContainer;

/**
 * ContainerNavigationControllerFragment
 * There are 2 possible cases:
 * + the fragment is navigated/presented inside a NavigationControllerFragment
 * + the fragment is traditionally shown using FragmentTransaction
 */
public class ContainerNavigationControllerFragment extends NavigationFragment implements ContainerNavigatorImpl {
    private static final int CONTAINER_ROOT_VIEW = R.id.fragmentRouterRootView;
    private static final int TYPE_STANDALONE = 0;
    public static final String KEY_DEFAULT_FRAGMENT_CLASS = "KEY_DEFAULT_FRAGMENT_CLASS";
    public static final String KEY_DEFAULT_UI_CONTAINER_CLASS = "KEY_DEFAULT_UI_CONTAINER_CLASS";

    public static ContainerNavigationControllerFragment create(Class<? extends NavigationFragment> defaultFragmentClass) {
        return create(defaultFragmentClass, ExpandStaticContainer.class);
    }

    public static ContainerNavigationControllerFragment create(Class<? extends NavigationFragment> defaultFragmentClass, Class<? extends UIContainer> defaultUIContainerClass) {

        if (defaultFragmentClass == null) {
            throw new IllegalArgumentException("Default Fragment class must be not null");
        }

        if (defaultUIContainerClass == null) {
            throw new IllegalArgumentException("Default UI Container class must be not null");
        }

        ContainerNavigationControllerFragment fragment = new ContainerNavigationControllerFragment();
        Bundle bundle = new Bundle();

        bundle.putString(KEY_DEFAULT_FRAGMENT_CLASS, defaultFragmentClass.getName());
        bundle.putString(KEY_DEFAULT_UI_CONTAINER_CLASS, defaultUIContainerClass.getName());

        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onInflate(@NonNull Context context, @NonNull AttributeSet attrs, @Nullable Bundle savedInstanceState) {
        super.onInflate(context, attrs, savedInstanceState);
        final TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ContainerNavigator);

        final String fragmentClazz = typedArray.getString(R.styleable.ContainerNavigator_defaultFragment);
        final String uiContainerClazz = typedArray.getString(R.styleable.ContainerNavigator_uiContainer);

        Bundle bundle = new Bundle();

        bundle.putString(KEY_DEFAULT_FRAGMENT_CLASS, fragmentClazz);
        bundle.putString(KEY_DEFAULT_UI_CONTAINER_CLASS, uiContainerClazz);

        setArguments(bundle);

        typedArray.recycle();
    }

    private static final int TYPE_EMBED_INSIDE_NAVIGATION_CONTROLLER = 1;

    @Override
    public FragmentManager provideFragmentManager() {
        return getChildFragmentManager();
    }

    private final NavigatorAttribute mNavigatorAttribute = onCreateAttribute();

    protected NavigatorAttribute onCreateAttribute() {
        return new NavigatorAttribute();
    }

    public final NavigatorAttribute getNavigatorAttribute() {
        return mNavigatorAttribute;
    }

    private static int mType = TYPE_STANDALONE;

    @Nullable
    @Override
    protected View onCreateContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = new EffectFrameLayout(inflater.getContext());
        view.setId(R.id.fragmentRouterRootView);
        view.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* in standalone mode, register callback to back pressed event */
        if (mType == TYPE_STANDALONE) {
            requireActivity().getOnBackPressedDispatcher().addCallback(this, mOnBackPressedCallback);
        }
        onCreateNavigator(savedInstanceState);
    }

    @Override
    public void onCreateNavigator(Bundle bundle) {
        ContainerNavigatorImpl.super.onCreateNavigator(bundle);
        presentDefaultNavigationControllerFragment(bundle);
    }

    @SuppressWarnings("unchecked")
    protected void presentDefaultNavigationControllerFragment(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            Bundle arguments = getArguments();
            String defaultFragmentClassNameArg = arguments == null ? null : arguments.getString(KEY_DEFAULT_FRAGMENT_CLASS);
            String defaultUIContainerArg = arguments == null ? null : arguments.getString(KEY_DEFAULT_UI_CONTAINER_CLASS);

            NavigationFragment defaultFragment;
            UIContainer defaultUIContainer;


            Fragment fragment = defaultFragmentClassNameArg == null ? null : provideFragmentManager().getFragmentFactory().instantiate(requireContext().getClassLoader(), defaultFragmentClassNameArg);
            if (fragment instanceof NavigationFragment) {
                defaultFragment = (NavigationFragment) fragment;
            } else {
                throw new IllegalAccessError("Could not instantiate default fragment class. Make sure you had already added attribute app:defaultFragment in the layout file");
            }

            defaultUIContainer = UIContainer.instantiate(getContext(), defaultUIContainerArg);

            present("default", CONTAINER_ROOT_VIEW, defaultUIContainer, defaultFragment);
        }
    }

    @Override
    public boolean isAllowedToBack() {
        return isAllowedToBackInternal();
    }

    public boolean isAllowedToBackInternal() {
        return ContainerNavigatorImpl.super.isAllowedToBack();
    }

    @Override
    public boolean requestBack(boolean animated) {
        if (isAllowedToBack()) {
            if (!navigateBack(animated)) {
                dismiss();
                return true;
            }
        }
        return false;
    }

    /**
     * Request to back the container navigator
     *
     * @return True - done. False - the back had been blocked by the focus fragment
     */
    public boolean requestBackInternal(boolean animated) {
        return ContainerNavigatorImpl.super.requestBack(animated);
    }

    public boolean requestBackInternal() {
        return requestBackInternal(true);
    }

    @Override
    public boolean requestBack() {
        return requestBack(true);
    }

    @Override
    public boolean navigateBack(boolean animated) {
        return navigateBackInternal(animated);
    }

    @Override
    public boolean navigateBack() {
        return navigateBack(true);
    }


    /**
     * Navigate to new fragment INSIDE this ContainerNavigator
     * <p>
     * <br/>NOTE: If this container navigator is embed in other navigation controller and you want to navigate to the other, use {@link ContainerNavigationControllerFragment#navigateExternal} instead
     *
     * @param fragment fragment to navigate
     */
    @Override
    public void navigate(NavigationFragment fragment) {
        navigateInternal(fragment);
    }

    /**
     * Navigate to new fragment INSIDE this ContainerNavigator
     * <p>
     * <br/>NOTE: If this container navigator is embed in other navigation controller and you want to navigate to the other, use {@link ContainerNavigationControllerFragment#navigateExternal} instead
     *
     * @param fragment fragment to navigate
     * @param animated run animation for this transaction
     */
    @Override
    public void navigate(NavigationFragment fragment, boolean animated) {
        navigateInternal(fragment, animated);
    }

    public boolean navigateBackInternal(boolean animated) {
        return ContainerNavigatorImpl.super.navigateBack(animated);
    }

    public boolean navigateBackInternal() {
        return navigateBackInternal(true);
    }

    /**
     * Navigate to new fragment INSIDE this ContainerNavigator
     * <p>
     * <br/>NOTE: If this container navigator is embed in other navigation controller and you want to navigate to the other, use {@link ContainerNavigationControllerFragment#navigateExternal} instead
     *
     * @param nav      fragment to navigate
     * @param animated run animation for this transaction
     */
    public void navigateInternal(NavigationFragment nav, boolean animated) {
        ContainerNavigatorImpl.super.navigate(nav, animated);
    }

    /**
     * Navigate to new fragment INSIDE this ContainerNavigator
     * <p>
     * <br/>NOTE: If this container navigator is embed in other navigation controller and you want to navigate to the other, use {@link ContainerNavigationControllerFragment#navigateExternal} instead
     *
     * @param nav fragment to navigate
     */
    public void navigateInternal(NavigationFragment nav) {
        ContainerNavigatorImpl.super.navigate(nav);
    }

    public void navigateExternal(NavigationFragment nav, boolean animated) {
        super.navigate(nav, animated);
    }

    public void navigateExternal(NavigationFragment nav) {
        super.navigate(nav);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        onSaveNavigatorState(outState);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mType = getNavigationController() == null ? TYPE_STANDALONE : TYPE_EMBED_INSIDE_NAVIGATION_CONTROLLER;

        /* set this fragment as the primary fragment */
        if (mType == TYPE_STANDALONE) {
            requireFragmentManager().beginTransaction()
                    .setPrimaryNavigationFragment(this)
                    .commitNow();
        }

    }

    @Override
    public void dismiss() {
        if (mType == TYPE_STANDALONE) {
            mOnBackPressedCallback.setEnabled(false);
            mOnBackPressedCallback.remove();
            FragmentActivity activity = getActivity();
            if (activity != null) {
                activity.onBackPressed();
            }
        } else if (mType == TYPE_EMBED_INSIDE_NAVIGATION_CONTROLLER) {
            NavigationControllerFragment controllerFragment = getNavigationController();
            if (controllerFragment != null) {
                controllerFragment.dismissFragment(this);
            }
        }
    }

    @Override
    public void present(@NonNull String uniquePresentName, UIContainer uiContainer, NavigationFragment... initialFragments) {
        presentExternal(uniquePresentName, uiContainer, initialFragments);
    }

    public void presentInternal(@NonNull String uniquePresentName, UIContainer uiContainer, NavigationFragment... initialFragments) {
        ContainerNavigatorImpl.super.present(uniquePresentName, CONTAINER_ROOT_VIEW, uiContainer, initialFragments);
    }

    public void presentExternal(@NonNull String uniquePresentName, UIContainer uiContainer, NavigationFragment... initialFragments) {
        super.present(uniquePresentName, uiContainer, initialFragments);
    }

    private final OnBackPressedCallback mOnBackPressedCallback = new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed() {
            requestBack();
        }
    };
}
