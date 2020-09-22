package com.ldt.navigation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentFactory;

import com.ldt.navigation.container.NavigatorAttribute;
import com.ldt.navigation.container.SplitFragmentContainerNavigator;
import com.ldt.navigation.container.SplitNavigatorAttribute;
import com.ldt.navigation.uicontainer.ExpandStaticContainer;
import com.ldt.navigation.uicontainer.UIContainer;

public class SplitNavigationControllerFragment extends ContainerNavigationControllerFragment implements SplitFragmentContainerNavigator {

    public static final String KEY_DETAIL_UI_CONTAINER = "KEY_DETAIL_UI_CONTAINER";
    public static final String KEY_MASTER_UI_CONTAINER = "KEY_MASTER_UI_CONTAINER";
    public static final String KEY_DEFAULT_DETAIL_FRAGMENT_CLASS = "KEY_DEFAULT_DETAIL_FRAGMENT_CLASS";
    public static final String KEY_DEFAULT_MASTER_FRAGMENT_CLASS = "KEY_DEFAULT_MASTER_FRAGMENT_CLASS";

    @Override
    public boolean navigateBackInternal(boolean animated) {
        return SplitFragmentContainerNavigator.super.navigateBack(animated);
    }

    @Override
    public void presentInternal(@NonNull String uniquePresentName, Class<? extends UIContainer> uiContainerClass, NavigationFragment... initialFragments) {
        SplitFragmentContainerNavigator.super.present(uniquePresentName, uiContainerClass, initialFragments);
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
        SplitFragmentContainerNavigator.super.onCreateNavigator(bundle);
    }

    private Class<? extends NavigationFragment> mDefaultMasterFragmentClass;
    private Class<? extends NavigationFragment> mDefaultDetailFragmentClass;
    private Class<? extends UIContainer> mMasterUIContainerClass;
    private Class<? extends UIContainer> mDetailUIContainerClass;

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public Class<? extends NavigationFragment> provideDefaultMasterFragment() {
        if (mDefaultMasterFragmentClass == null) {
            Bundle arguments = getArguments();
            String defaultMasterFragmentArg = arguments == null ? null : arguments.getString(KEY_DEFAULT_MASTER_FRAGMENT_CLASS);

            try {
                mDefaultMasterFragmentClass = (Class<? extends NavigationFragment>) FragmentFactory.loadFragmentClass(requireContext().getClassLoader(), defaultMasterFragmentArg);
            } catch (Exception e) {
                throw new IllegalAccessError("Could not instantiate default fragment class. Make sure you had already added attribute app:defaultFragment in the layout file");
            }

        }
        return mDefaultMasterFragmentClass;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public Class<? extends NavigationFragment> provideDefaultDetailFragment() {
        if (mDefaultDetailFragmentClass == null) {
            Bundle arguments = getArguments();
            String defaultDetailFragmentArg = arguments == null ? null : arguments.getString(KEY_DEFAULT_DETAIL_FRAGMENT_CLASS);

            try {
                mDefaultDetailFragmentClass = (Class<? extends NavigationFragment>) FragmentFactory.loadFragmentClass(requireContext().getClassLoader(), defaultDetailFragmentArg);
            } catch (Exception e) {
                throw new IllegalAccessError("Could not instantiate default fragment class. Make sure you had already added attribute app:defaultFragment in the layout file");
            }
        }
        return mDefaultDetailFragmentClass;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<? extends UIContainer> provideMasterUIContainer() {

        if (mMasterUIContainerClass == null) {
            Bundle arguments = getArguments();
            String uIContainerArg = arguments == null ? null : arguments.getString(KEY_MASTER_UI_CONTAINER);

            if (uIContainerArg == null || uIContainerArg.isEmpty()) {
                mMasterUIContainerClass = ExpandStaticContainer.class;
            } else {
                try {
                    mMasterUIContainerClass = (Class<? extends UIContainer>) UIContainer.loadClass(requireContext(), uIContainerArg);
                } catch (Exception e) {
                    throw new IllegalArgumentException("Could not instantiate default ui container class. Make sure you had already added attribute app:uiContainer in the layout file.", new ClassCastException());
                }
            }
        }
        return mMasterUIContainerClass;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<? extends UIContainer> provideDetailUIContainer() {
        if (mDetailUIContainerClass == null) {
            Bundle arguments = getArguments();
            String uIContainerArg = arguments == null ? null : arguments.getString(KEY_DETAIL_UI_CONTAINER);

            if (uIContainerArg == null || uIContainerArg.isEmpty()) {
                mMasterUIContainerClass = ExpandStaticContainer.class;
            } else {
                try {
                    mMasterUIContainerClass = (Class<? extends UIContainer>) UIContainer.loadClass(requireContext(), uIContainerArg);
                } catch (Exception e) {
                    throw new IllegalArgumentException("Could not instantiate default ui container class. Make sure you had already added attribute app:uiContainer in the layout file.", new ClassCastException());
                }
            }
        }
        return mDetailUIContainerClass;
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
