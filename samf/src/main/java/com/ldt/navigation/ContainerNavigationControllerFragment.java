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
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentFactory;
import androidx.fragment.app.FragmentManager;

import com.ldt.navigation.container.FragmentContainerNavigator;
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
public class ContainerNavigationControllerFragment extends NavigationFragment implements FragmentContainerNavigator {
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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presentDefaultNavigationControllerFragment(savedInstanceState);
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

    @SuppressWarnings("unchecked")
    protected void presentDefaultNavigationControllerFragment(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            Bundle arguments = getArguments();
            String defaultFragmentArg = arguments == null ? null : arguments.getString(KEY_DEFAULT_FRAGMENT_CLASS);
            String defaultUIContainerArg = arguments == null ? null : arguments.getString(KEY_DEFAULT_UI_CONTAINER_CLASS);

            Class<? extends NavigationFragment> defaultFragmentClass;
            Class<? extends UIContainer> defaultUIContainerClass;

            try {
                defaultFragmentClass = (Class<? extends NavigationFragment>) FragmentFactory.loadFragmentClass(requireContext().getClassLoader(), defaultFragmentArg);
            } catch (Exception e) {
                throw new IllegalAccessError("Could not instantiate default fragment class. Make sure you had already added attribute app:defaultFragment in the layout file");
            }

            if (defaultUIContainerArg == null || defaultUIContainerArg.isEmpty()) {
                defaultUIContainerClass = ExpandStaticContainer.class;
            } else {
                try {
                    defaultUIContainerClass = (Class<? extends UIContainer>) UIContainer.loadClass(requireContext(), defaultUIContainerArg);
                } catch (Exception e) {
                    throw new IllegalArgumentException("Could not instantiate default ui container class. Make sure you had already added attribute app:uiContainer in the layout file.", new ClassCastException());
                }
            }

            present("default", CONTAINER_ROOT_VIEW, defaultUIContainerClass, defaultFragmentClass);
        }
    }

    @Override
    public boolean isAllowedToBack() {
        return isAllowedToBackInternal();
    }

    public boolean isAllowedToBackInternal() {
        return FragmentContainerNavigator.super.isAllowedToBack();
    }

    @Override
    public boolean requestBack(boolean animated) {
        return isAllowedToBack() && navigateBack(animated);
    }

    /**
     * Request to back the container navigator
     *
     * @return True - successful back. False - the back is blocked by
     */
    public boolean requestBackInternal(boolean animated) {
        return FragmentContainerNavigator.super.requestBack(animated);
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
        boolean internalBack = navigateBackInternal(animated);
        if(!internalBack) dismiss();

        return true;
    }

    public boolean navigateBackInternal(boolean animated) {
        return FragmentContainerNavigator.super.navigateBack(animated);
    }

    public boolean navigateBackInternal() {
        return navigateBackInternal(true);
    }

    public void navigateInternal(NavigationFragment nav, boolean animated) {
        FragmentContainerNavigator.super.navigate(nav, animated);
    }

    public void navigateInternal(NavigationFragment nav) {
        FragmentContainerNavigator.super.navigate(nav);
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
                    .commit();
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
    public void present(@NonNull String uniquePresentName, Class<? extends UIContainer> uiContainerClass, NavigationFragment... initialFragments) {
        presentExternal(uniquePresentName, uiContainerClass, initialFragments);
    }

    public void presentInternal(@NonNull String uniquePresentName, Class<? extends UIContainer> uiContainerClass, NavigationFragment... initialFragments) {
        FragmentContainerNavigator.super.present(uniquePresentName, CONTAINER_ROOT_VIEW, uiContainerClass, initialFragments);
    }

    public void presentExternal(@NonNull String uniquePresentName, Class<? extends UIContainer> uiContainerClass, NavigationFragment... initialFragments) {
        super.present(uniquePresentName, uiContainerClass, initialFragments);
    }

    private final OnBackPressedCallback mOnBackPressedCallback = new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed() {
            requestBack();
        }
    };
}
