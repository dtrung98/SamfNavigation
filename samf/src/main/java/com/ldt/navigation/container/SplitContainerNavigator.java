package com.ldt.navigation.container;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ldt.navigation.NavigationControllerFragment;
import com.ldt.navigation.NavigationFragment;

public interface SplitContainerNavigator extends FragmentContainerNavigator {
    String DETAIL_CONTROLLER_HAS_DEFAULT_FRAGMENT = "detail-controller-has-default-fragment";
    String DETAIL_CONTROLLER_DEFAULT_FRAGMENT_TAG = "detail-controller-default-fragment";

    @Override
    NavigatorAttribute getNavigatorAttribute();

    default SplitNavigatorAttribute requireSplitRouterAttribute() {
        return (SplitNavigatorAttribute) getNavigatorAttribute();
    }

    @NonNull
    Class<? extends NavigationFragment> provideDefaultDetailFragment();

    @NonNull
    Class<? extends NavigationFragment> provideDefaultMasterFragment();

    /**
     *  Present master controller into split container with provided initial fragment. If controller exists, nothing happen
     * @param initialFragment the initial fragment, will use default master fragment if this parameter is null
     * @return master controller
     */
    NavigationControllerFragment presentMasterController(@Nullable NavigationFragment... initialFragment);

    /**
     * Present detail controller into split container with provided initial fragment. If controller exists, nothing happen
     * @param initialFragment the initial fragment, will use default detail fragment if this parameter is null
     * @return
     */
    NavigationControllerFragment presentDetailController(@Nullable NavigationFragment... initialFragment);

    default NavigationControllerFragment findMasterController() {
        return requireSplitRouterAttribute().findController(requireSplitRouterAttribute().getMasterControllerTag());
    }

    default NavigationControllerFragment findDetailController() {
        return getNavigatorAttribute().findController(requireSplitRouterAttribute().getDetailControllerTag());
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
    void masterControllerNavigate(NavigationFragment fragment, boolean animated);

    /**
     * Use to navigate to a fragment in detail controller
     * @param fragment fragment
     * @param animate should animate
     */
    void detailControllerNavigate(NavigationFragment fragment, boolean animate);

    default void masterControllerNavigate(NavigationFragment fragment) {
        masterControllerNavigate(fragment, true);
    }

    default void detailControllerNavigate(NavigationFragment fragment) {
        detailControllerNavigate(fragment, true);
    }
}
