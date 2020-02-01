package com.ldt.navigation;
import com.ldt.navigation.NavigationController;
import com.ldt.navigation.NavigationFragment;
/**
 * Created by dtrung98 on 2020. 1. 30
 */
public interface SingleHolder {
NavigationController getNavigationController();

default boolean onNavigateBack() {
  return isNavigationAvailable() && getNavigationController().onNavigateBack();
}

default boolean navigateBack() {
  return navigateBack(true);
}

default boolean isNavigationAvailable() {
  return null != getNavigationController();
}

default boolean navigateBack(boolean animated) {
        return isNavigationAvailable() &&
        getNavigationController().navigateBack(animated);
    }

default void navigateTo(NavigationFragment fragment) {
  navigateTo(fragment, true);
}

default void navigateTo(NavigationFragment fragment, boolean animated) {
  if(isNavigationAvailable()) getNavigationController().navigateTo(fragment, animated);
}

}
