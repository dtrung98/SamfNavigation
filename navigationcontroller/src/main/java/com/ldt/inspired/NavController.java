package com.ldt.inspired;

import androidx.fragment.app.Fragment;

import java.lang.ref.WeakReference;

public abstract class NavController<T extends Fragment> extends Fragment {

    private WeakReference<NavController<?>> mParenController;
    public NavController<?> getParentController() {
        return mParenController==null ? null : mParenController.get();
    }

    public NavController<?> getRootController() {
        NavController<?> current = this;
        NavController<?> parent = null;

        do {
            parent = current.getParentController();
            if(parent != null)
                current = parent;
        } while (parent != null);

        return current;
    }

    public abstract void navigateTo(T nextOne);
    public abstract void switchNew(T newOne);

    public static NavController findController(String tag) {
        return null;
    }

    public static NavigationFragment findFragment(String tag) {
        return null;
    }


}
