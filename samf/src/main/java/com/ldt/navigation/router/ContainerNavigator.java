package com.ldt.navigation.router;

import com.ldt.navigation.NavigationController;
import com.ldt.navigation.base.Navigator;

/**
 * A navigator that contains other navigators (they are NavigationController)
 */
public interface Router extends Navigator<NavigationController> {
    /**
     * Dismiss a presented child NavigationController
     * @param controller the NavigationController will be dismissed
     */
    void dismissNavigationController(NavigationController controller);

    /**
     * Dismiss this router (usually a NavigationContainerController
     */
    void dismiss();
}
