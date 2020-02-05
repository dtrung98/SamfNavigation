package com.ldt.navigation.holder;


public interface Navigable<T> {
    boolean onNavigateBack();
    boolean navigateBack();
    boolean navigateBack(boolean animated);
    void navigateTo(T nav);
    void navigateTo(T nav, boolean animated);
    boolean requestBack();
}
