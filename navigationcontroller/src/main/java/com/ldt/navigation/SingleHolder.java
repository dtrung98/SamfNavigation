package com.ldt.navigation;
import com.ldt.navigation.FragNavigationController;
import com.ldt.navigation.NavigationFragment;
/**
 * Created by dtrung98 on 2020. 1. 30
 */
public interface SingleHolder {
FragNavigationController getNavigationController();

default boolean onNavigateBack() {
  return isNavigationAvailable() && getNavigationController().onNavigateBack();
}

default void dismissFragment() {
  return dismissFragment(true);
}

default boolean isNavigationAvailable() {
  return null != getNavigationController();
}

default boolean dismissFragment(boolean animated) {
        return isNavigationAvailable() &&
        getNavigationController().dismissFragment(animated);
    }

default void presentFragment(NavigationFragment fragment) {
  presentFragment(fragment, true);
}

default void presentFragment(NavigationFragment fragment, boolean animated) {
  if(isNavigationAvailable()) getNavigationController().presentFragment(fragment, animated);
}

}
