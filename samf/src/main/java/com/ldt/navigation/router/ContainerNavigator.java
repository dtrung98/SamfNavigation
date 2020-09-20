package com.ldt.navigation.router;

import com.ldt.navigation.NavigationControllerFragment;
import com.ldt.navigation.base.Navigator;

/**
 * A navigator that contains other navigators (they are NavigationController)
 */
public interface ContainerNavigator extends Navigator<NavigationControllerFragment> {
    /**
     * Dismiss a presented child NavigationController
     * @param controller the NavigationController will be dismissed
     */
    void dismissNavigationController(NavigationControllerFragment controller);

    /**
     * Dismiss this router (usually a NavigationContainerController
     */
    void dismiss();
}
