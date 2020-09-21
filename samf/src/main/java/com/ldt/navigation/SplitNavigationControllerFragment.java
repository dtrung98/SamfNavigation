package com.ldt.navigation;

import android.os.Bundle;

import androidx.annotation.NonNull;

import com.ldt.navigation.container.NavigatorAttribute;
import com.ldt.navigation.container.FragmentSplitContainerNavigator;
import com.ldt.navigation.container.SplitNavigatorAttribute;

public class SplitNavigationControllerFragment extends ContainerNavigationControllerFragment implements FragmentSplitContainerNavigator {

    @Override
    protected NavigatorAttribute onCreateAttribute() {
        return new SplitNavigatorAttribute();
    }

    @Override
    public void onCreateNavigator(Bundle bundle) {
        FragmentSplitContainerNavigator.super.onCreateNavigator(bundle);
    }

    @Override
    public boolean navigateBackInternal(boolean animated) {
        return FragmentSplitContainerNavigator.super.navigateBack(animated);
    }

    @NonNull
    @Override
    public Class<? extends NavigationFragment> provideDefaultDetailFragment() {
        return null;
    }

    @NonNull
    @Override
    public Class<? extends NavigationFragment> provideDefaultMasterFragment() {
        return null;
    }
}
