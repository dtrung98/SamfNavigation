package com.ldt.navigation.holder;
import com.ldt.navigation.NavigationController;
import com.ldt.navigation.NavigationFragment;
/**
 * Created by dtrung98 on 2020. 1. 30
 */
public interface SingleRouter extends Navigable<NavigationFragment> {
    NavigationController getNavigationController();

    @Override
    default boolean requestBack() {
        return onNavigateBack();
    }

    @Override
    default boolean onNavigateBack() {
        return isNavigationAvailable() && getNavigationController().onNavigateBack();
    }

    @Override
    default boolean navigateBack() {
        return navigateBack(true);
    }

    default boolean isNavigationAvailable() {
        return null != getNavigationController();
    }

    @Override
    default boolean navigateBack(boolean animated) {
        return isNavigationAvailable() &&
                getNavigationController().navigateBack(animated);
    }
    @Override
    default void navigateTo(NavigationFragment fragment) {
        navigateTo(fragment, true);
    }

    @Override
    default void navigateTo(NavigationFragment fragment, boolean animated) {
        if(isNavigationAvailable()) getNavigationController().navigateTo(fragment, animated);
    }

}
