package com.ldt.inspired;

import androidx.fragment.app.Fragment;

import java.lang.ref.WeakReference;

public abstract class NavigationController<T extends Fragment> extends Fragment {

    private WeakReference<NavigationController<?>> mParenController;
    public NavigationController<?> getParentController() {
        return mParenController==null ? null : mParenController.get();
    }

    public NavigationController<?> getRootController() {
        NavigationController<?> current = this;
        NavigationController<?> parent = null;

        do {
            parent = current.getParentController();
            if(parent != null)
                current = parent;
        } while (parent != null);

        return current;
    }

    public abstract void navigateTo(T nextOne);
    public abstract void switchNew(T newOne);
    public void popBackStack() {

    }

    public static NavigationController findController(String tag) {
        return null;
    }

    public static NavigationFragment findFragment(String tag) {
        return null;
    }


}
