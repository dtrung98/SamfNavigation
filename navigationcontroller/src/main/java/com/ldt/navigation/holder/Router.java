package com.ldt.navigation.holder;

import com.ldt.navigation.NavigationController;

public interface Router extends Navigable {
    void finishController(NavigationController controller);
    void finish();
}
