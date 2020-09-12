package com.ldt.navigation.router;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ldt.navigation.NavigationController;
import com.ldt.navigation.NavigationFragment;

public interface BaseSplitRouter extends FlexRouter {
    String DETAIL_CONTROLLER_HAS_DEFAULT_FRAGMENT = "detail-controller-has-default-fragment";
    String DETAIL_CONTROLLER_DEFAULT_FRAGMENT_TAG = "detail-controller-default-fragment";

    @Override
    SplitRouterSaver getRouterSaver();

    @NonNull
    Class<? extends NavigationFragment> provideDefaultDetailFragment();

    @NonNull
    Class<? extends NavigationFragment> provideDefaultMasterFragment();

    /**
     *  Present master controller into with provided initial fragment. If controller exists, nothing happen
     * @param initialFragment the initial fragment, will use default master fragment if this parameter is null
     * @return master controller
     */
    NavigationController presentMasterController(@Nullable NavigationFragment... initialFragment);

    /**
     * Present detail controller into split router with provided initial fragment. If controller exists, nothing happen
     * @param initialFragment the initial fragment, will use default detail fragment if this parameter is null
     * @return
     */
    NavigationController presentDetailController(@Nullable NavigationFragment... initialFragment);

    default NavigationController findMasterController() {
        return getRouterSaver().findController(getRouterSaver().getMasterControllerTag());
    }

    default NavigationController findDetailController() {
        return getRouterSaver().findController(getRouterSaver().getDetailControllerTag());
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
        detailControllerNavigateTo(fragment, true);
    }
}
