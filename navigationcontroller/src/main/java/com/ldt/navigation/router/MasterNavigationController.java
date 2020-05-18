package com.ldt.navigation.router;

import androidx.annotation.Nullable;

import com.ldt.navigation.NavigationController;
import com.ldt.navigation.NavigationFragment;

public class MasterNavigationController extends NavigationController {
    @Override
    public void switchNew(@Nullable Object sender, NavigationFragment fragment, boolean withAnimation) {
        SplitRouter router = (SplitRouter) getRouter();

        if(router != null && sender instanceof NavigationFragment) {
            /* This is the detail fragment wanting to push to detail controller */
            if(router.getRouterSaver().isDetailFragment(((NavigationFragment) sender).getIdentifyTag()))
                router.detailControllerSwitchNew(fragment);
            else
                router.masterControllerSwitchNew(fragment);
        } else
            super.switchNew(sender, fragment, withAnimation);
    }

    @Override
    public void navigateTo(@Nullable Object sender, NavigationFragment fragmentToPush, boolean withAnimation) {
        Router router = getRouter();
        if(router instanceof SplitRouter && sender instanceof NavigationFragment) {
            if(((SplitRouter) router).getRouterSaver().isDetailFragment(((NavigationFragment) sender).getIdentifyTag()))
                ((SplitRouter) router).detailControllerNavigateTo(fragmentToPush);
            else ((SplitRouter) router).masterControllerNavigateTo(fragmentToPush);
        } else super.navigateTo(sender, fragmentToPush, withAnimation);
    }
}
