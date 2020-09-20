package com.ldt.navigation.router;

import com.ldt.navigation.NavigationFragment;

public interface Navigator<T extends Navigator<?>> {
    boolean onNavigateBack();
    boolean navigateBack();
    boolean navigateBack(boolean animated);
    void navigateTo(T nav);
    void navigateTo(T nav, boolean animated);
    boolean requestBack();
}
