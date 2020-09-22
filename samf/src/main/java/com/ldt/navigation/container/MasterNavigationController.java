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
        SplitFragmentContainerNavigator router = (SplitFragmentContainerNavigator) getParentNavigator();

        if (router != null && sender instanceof NavigationFragment) {
            /* This is the detail fragment wanting to push to detail controller */
            if (router.requireSplitRouterAttribute().isDetailFragment(((NavigationFragment) sender).getIdentifyTag()))
                router.detailControllerSwitchNew(fragment);
            else
                router.masterControllerSwitchNew(fragment);
        } else
            super.switchNew(sender, fragment, withAnimation);
    }

   /* @Override
    public void navigate(@Nullable Object sender, NavigationFragment fragmentToPush, boolean withAnimation) {
        ContainerNavigator navigator = getParentNavigator();

        if (navigator instanceof SplitContainerNavigator) {
            ((SplitContainerNavigator) navigator).detailControllerNavigate(fragmentToPush);
        } else {
            navigateInternal(sender, fragmentToPush, withAnimation);
        }
    }*/


    public void navigate(@Nullable Object sender, NavigationFragment fragmentToPush, boolean withAnimation) {
        ContainerNavigator router = getParentNavigator();

        /* fragment in master calling navigate => push fragment to master stack */
        /* fragment in detail calling navigate => push new fragment to detail stack */
        if (router instanceof SplitFragmentContainerNavigator && sender instanceof NavigationFragment) {
            if (((SplitFragmentContainerNavigator) router).requireSplitRouterAttribute().isDetailFragment(((NavigationFragment) sender).getIdentifyTag())) {
                ((SplitFragmentContainerNavigator) router).detailControllerNavigate(fragmentToPush);
            } else {
                ((SplitFragmentContainerNavigator) router).masterControllerNavigate(fragmentToPush);
            }
        } else {
            navigateInternal(sender, fragmentToPush, withAnimation);
        }
    }

    public void navigateInternal(@Nullable Object sender, NavigationFragment fragmentToPush, boolean withAnimation) {
        super.navigate(sender, fragmentToPush, withAnimation);
    }
}
