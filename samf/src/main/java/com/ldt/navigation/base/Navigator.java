package com.ldt.navigation.base;

public interface Navigator<T extends Navigator<?>> {
    boolean onNavigateBack();
    boolean navigateBack();
    boolean navigateBack(boolean animated);
    void navigateTo(T nav);
    void navigateTo(T nav, boolean animated);
    boolean requestBack();
}
