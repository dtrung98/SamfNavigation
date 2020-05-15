package com.ldt.navigation.router;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

import com.ldt.navigation.NavigationController;
import com.ldt.navigation.NavigationFragment;

public interface BaseSplitRouter2 extends  FlexRouter {
    String MASTER_CONTROLLER_TAG = "master-controller";
    String DETAIL_CONTROLLER_TAG = "detail-controller";

    String DETAIL_CONTROLLER_HAS_DEFAULT_FRAGMENT = "detail-controller-has-default-fragment";
    String DETAIL_CONTROLLER_DEFAULT_FRAGMENT_TAG = "detail-controller-default-fragment";

    @NonNull
    Class<? extends NavigationFragment> provideDefaultDetailFragment();

    @NonNull
    Class<? extends NavigationFragment> provideDefaultMasterFragment();

    default NavigationController findMasterController() {
        return getRouterSaver().findController(MASTER_CONTROLLER_TAG);
    }

    default NavigationController findDetailController() {
        return getRouterSaver().findController(DETAIL_CONTROLLER_TAG);
    }

    /**
     * Use to switch new fragment in master controller
     * @param fragment fragment
     */
    void masterControllerSwitchNew(NavigationFragment fragment);

    /**
     * Use this method to switch new fragment in detail controller
     * @param fragment fragment
     */
    void detailControllerSwitchNew(NavigationFragment fragment);

    /**
     * Use this method to navigate to a fragment in master controller
     * @param fragment fragment
     * @param animated should animate
     */
    void masterControllerNavigateTo(NavigationFragment fragment, boolean animated);

    /**
     * Use to navigate to a fragment in detail controller
     * @param fragment fragment
     * @param animate should animate
     */
    void detailControllerNavigateTo(NavigationFragment fragment, boolean animate);

    default void masterControllerNavigateTo(NavigationFragment fragment) {
        masterControllerNavigateTo(fragment, true);
    }
    default void detailControllerNavigateTo(NavigationFragment fragment) {
        masterControllerNavigateTo(fragment, true);
    }
}
