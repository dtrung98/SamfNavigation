package com.ldt.navigation.container;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

import com.ldt.navigation.NavigationControllerFragment;
import com.ldt.navigation.NavigationFragment;
import com.ldt.navigation.base.Navigator;
import com.ldt.navigation.uicontainer.UIContainer;

/**
 * A navigator that contains other navigators (they are NavigationController)
 */
public interface ContainerNavigator extends Navigator<NavigationFragment> {
    /**
     * Present one or some fragments in new NavigationControllerFragment
     * @param uniquePresentName
     * @param uiContainerClass
     * @param initialFragments
     */
    void present(@NonNull String uniquePresentName,
                 Class<? extends UIContainer> uiContainerClass,
                 NavigationFragment... initialFragments);

    /**
     * Dismiss a presented child NavigationController
     * @param controller the NavigationController will be dismissed
     */
    void dismissNavigationController(NavigationControllerFragment controller);

    /**
     * Dismiss this router (usually a NavigationContainerController
     */
    void dismiss();
    NavigationControllerFragment findController(String tag);
    void navigate(NavigationFragment nav, boolean animated);
    FragmentManager provideFragmentManager();
    NavigatorAttribute getNavigatorAttribute();

}
