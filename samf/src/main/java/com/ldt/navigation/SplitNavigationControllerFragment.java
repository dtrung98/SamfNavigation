package com.ldt.navigation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ldt.navigation.container.NavigatorAttribute;
import com.ldt.navigation.container.SplitNavigatorImpl;
import com.ldt.navigation.container.SplitNavigatorAttribute;
import com.ldt.navigation.uicontainer.UIContainer;

public class SplitNavigationControllerFragment extends ContainerNavigationControllerFragment implements SplitNavigatorImpl {

    public static final String KEY_DETAIL_UI_CONTAINER = "KEY_DETAIL_UI_CONTAINER";
    public static final String KEY_MASTER_UI_CONTAINER = "KEY_MASTER_UI_CONTAINER";
    public static final String KEY_DEFAULT_DETAIL_FRAGMENT_CLASS = "KEY_DEFAULT_DETAIL_FRAGMENT_CLASS";
    public static final String KEY_DEFAULT_MASTER_FRAGMENT_CLASS = "KEY_DEFAULT_MASTER_FRAGMENT_CLASS";

    @Override
    public boolean navigateBackInternal(boolean animated) {
        return SplitNavigatorImpl.super.navigateBack(animated);
    }

    @Override
    public void presentInternal(@NonNull String uniquePresentName, UIContainer uiContainer, NavigationFragment... initialFragments) {
        SplitNavigatorImpl.super.present(uniquePresentName, uiContainer, initialFragments);
    }

    public static SplitNavigationControllerFragment create(Class<? extends NavigationFragment> defaultMasterFragmentClass, Class<? extends NavigationFragment> defaultDetailFragmentClass, Class<? extends UIContainer> defaultUIContainerClass) {
        return create(defaultMasterFragmentClass, defaultDetailFragmentClass, defaultUIContainerClass, defaultUIContainerClass);
    }

    public static SplitNavigationControllerFragment create(Class<? extends NavigationFragment> defaultMasterFragmentClass, Class<? extends NavigationFragment> defaultDetailFragmentClass, Class<? extends UIContainer> masterUIContainerClass, Class<? extends UIContainer> detailUIContainerClass) {

        if (defaultMasterFragmentClass == null) {
            throw new IllegalArgumentException("Default Master Fragment class must be not null");
        }

        if (defaultDetailFragmentClass == null) {
            throw new IllegalArgumentException("Default Master Fragment class must be not null");
        }

        if (masterUIContainerClass == null) {
            throw new IllegalArgumentException("Master UI Container class must be not null");
        }

        if (detailUIContainerClass == null) {
            throw new IllegalArgumentException("Detail UI Container class must be not null");
        }

        SplitNavigationControllerFragment fragment = new SplitNavigationControllerFragment();
        Bundle bundle = new Bundle();

        bundle.putString(KEY_DEFAULT_MASTER_FRAGMENT_CLASS, defaultMasterFragmentClass.getName());
        bundle.putString(KEY_DEFAULT_DETAIL_FRAGMENT_CLASS, defaultDetailFragmentClass.getName());


        bundle.putString(KEY_MASTER_UI_CONTAINER, masterUIContainerClass.getName());
        bundle.putString(KEY_DETAIL_UI_CONTAINER, detailUIContainerClass.getName());

        bundle.putString(KEY_DEFAULT_FRAGMENT_CLASS, defaultMasterFragmentClass.getName());
        bundle.putString(KEY_DEFAULT_UI_CONTAINER_CLASS, masterUIContainerClass.getName());

        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    protected View onCreateContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return provideLayout(inflater.getContext());
    }

    @Override
    protected NavigatorAttribute onCreateAttribute() {
        return new SplitNavigatorAttribute();
    }

    @Override
    public void onCreateNavigator(Bundle bundle) {
        SplitNavigatorImpl.super.onCreateNavigator(bundle);
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public NavigationFragment provideDefaultMasterFragment() {
        NavigationFragment masterFragment;
        Bundle arguments = getArguments();
        String defaultMasterFragmentArg = arguments == null ? null : arguments.getString(KEY_DEFAULT_MASTER_FRAGMENT_CLASS);

        Fragment fragment = defaultMasterFragmentArg != null ? provideFragmentManager().getFragmentFactory().instantiate(requireContext().getClassLoader(), defaultMasterFragmentArg) : null;
        if (fragment instanceof NavigationFragment) {
            masterFragment = (NavigationFragment) fragment;
        } else {
            throw new IllegalAccessError("Could not instantiate default fragment class. Make sure you had already added attribute app:defaultFragment in the layout file");

        }

        return masterFragment;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public NavigationFragment provideDefaultDetailFragment() {
        NavigationFragment detailFragment;
        Bundle arguments = getArguments();
        String defaultDetailFragmentArg = arguments == null ? null : arguments.getString(KEY_DEFAULT_DETAIL_FRAGMENT_CLASS);

        Fragment fragment = defaultDetailFragmentArg != null ? provideFragmentManager().getFragmentFactory().instantiate(requireContext().getClassLoader(), defaultDetailFragmentArg) : null;
        if (fragment instanceof NavigationFragment) {
            detailFragment = (NavigationFragment) fragment;
        } else {
            throw new IllegalAccessError("Could not instantiate default fragment class. Make sure you had already added attribute app:defaultFragment in the layout file");

        }

        return detailFragment;
    }

    @SuppressWarnings("unchecked")
    @Override
    public UIContainer provideMasterUIContainer() {

        UIContainer uiContainer;
        Bundle arguments = getArguments();
        String uIContainerArg = arguments == null ? null : arguments.getString(KEY_MASTER_UI_CONTAINER);

        if (uIContainerArg == null || uIContainerArg.isEmpty()) {
            uiContainer = SplitNavigatorImpl.super.provideMasterUIContainer();
        } else {
            uiContainer = UIContainer.instantiate(requireContext(), uIContainerArg);
        }

        if (uiContainer == null) {
            throw new IllegalArgumentException("Could not instantiate default ui container class. Make sure you had already added attribute app:uiContainer in the layout file.", new ClassCastException());
        }

        return uiContainer;
    }

    @SuppressWarnings("unchecked")
    @Override
    public UIContainer provideDetailUIContainer() {
        UIContainer uiContainer;
        Bundle arguments = getArguments();
        String uIContainerArg = arguments == null ? null : arguments.getString(KEY_DETAIL_UI_CONTAINER);

        if (uIContainerArg == null || uIContainerArg.isEmpty()) {
            uiContainer = SplitNavigatorImpl.super.provideDetailUIContainer();
        } else {
            uiContainer = UIContainer.instantiate(requireContext(), uIContainerArg);
        }

        if (uiContainer == null) {
            throw new IllegalArgumentException("Could not instantiate default ui container class. Make sure you had already added attribute app:uiContainer in the layout file.", new ClassCastException());
        }

        return uiContainer;
    }

    @Override
    public void onConfigureSplitRouter(SplitCondition splitWhen, int screenWidthDp, int screenHeightDp) {
        splitWhen
                .widerThan(600)
                .tallerThan(-1)
                .configLeftWide(350)
                .configRightWide(-1);
    }
}
