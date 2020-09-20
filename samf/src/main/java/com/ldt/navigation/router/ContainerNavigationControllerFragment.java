package com.ldt.navigation.router;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import com.ldt.navigation.NavigationFragment;

/**
 * ContainerNavigationControllerFragment
 */
public class ContainerNavigationControllerFragment extends NavigationFragment {
    @Nullable
    @Override
    protected View onCreateContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return null;
    }

    @Override
    public boolean navigateBack(boolean animated) {
        return false;
    }

    @Override
    public void navigateTo(NavigationFragment nav, boolean animated) {

    }

    /**
     * Present a NavigationFragment inside a new NavigationControllerFragment
     * @param fragment the fragment are going to be presented
     * @param animated run transition
     */
    public void presentFragment(NavigationFragment fragment, boolean animated) {

    }

    private RouterImpl mRouter = new RouterImpl();
    private class RouterImpl implements Router {
        private final RouterAttribute mRouterAttribute = new RouterAttribute();

        @Override
        public FragmentManager provideFragmentManager() {
            return getChildFragmentManager();
        }

        @Override
        public RouterAttribute getRouterAttribute() {
            return mRouterAttribute;
        }

        @Override
        public void dismiss() {

        }
    }
}
