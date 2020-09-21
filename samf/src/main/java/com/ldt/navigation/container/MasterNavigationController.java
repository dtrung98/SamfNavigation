package com.ldt.navigation.container;

import androidx.annotation.Nullable;

import com.ldt.navigation.NavigationControllerFragment;
import com.ldt.navigation.NavigationFragment;

/**
 * Custom NavigationController is used as the master navigation controller of the SplitNavigationContainerController
 */
public class MasterNavigationController extends NavigationControllerFragment {
    @Override
    public void switchNew(@Nullable Object sender, NavigationFragment fragment, boolean withAnimation) {
        SplitContainerNavigator router = (SplitContainerNavigator) getParentNavigator();

        if(router != null && sender instanceof NavigationFragment) {
            /* This is the detail fragment wanting to push to detail controller */
            if(router.requireSplitRouterAttribute().isDetailFragment(((NavigationFragment) sender).getIdentifyTag()))
                router.detailControllerSwitchNew(fragment);
            else
                router.masterControllerSwitchNew(fragment);
        } else
            super.switchNew(sender, fragment, withAnimation);
    }

    @Override
    public void navigate(@Nullable Object sender, NavigationFragment fragmentToPush, boolean withAnimation) {
        ContainerNavigator router = getParentNavigator();
        if(router instanceof SplitContainerNavigator && sender instanceof NavigationFragment) {
            if(((SplitContainerNavigator) router).requireSplitRouterAttribute().isDetailFragment(((NavigationFragment) sender).getIdentifyTag()))
                ((SplitContainerNavigator) router).detailControllerNavigateTo(fragmentToPush);
            else ((SplitContainerNavigator) router).masterControllerNavigateTo(fragmentToPush);
        } else super.navigate(sender, fragmentToPush, withAnimation);
    }
}
