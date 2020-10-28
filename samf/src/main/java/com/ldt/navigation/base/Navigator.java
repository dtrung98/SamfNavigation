package com.ldt.navigation.base;

public interface Navigator<T> {
    boolean isAllowedToBack();
    boolean navigateBack();
    boolean navigateBack(boolean animated);
    void navigate(T destination);
    void navigate(T destination, boolean animated);
    boolean requestBack();
    boolean requestBack(boolean animated);
}
