package com.ldt.navigation.router;

import com.ldt.navigation.NavigationFragment;

public interface Navigable {
    boolean onNavigateBack();
    boolean navigateBack();
    boolean navigateBack(boolean animated);
    void navigateTo(NavigationFragment nav);
    void navigateTo(NavigationFragment nav, boolean animated);
    boolean requestBack();
}
