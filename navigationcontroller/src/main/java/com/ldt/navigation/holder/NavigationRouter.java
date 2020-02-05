package com.ldt.navigation.holder;

import android.os.Bundle;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

import com.ldt.navigation.NavigationController;
import com.ldt.navigation.NavigationFragment;
import com.ldt.navigation.uicontainer.UIContainer;

import java.util.ArrayList;

public interface NavigationRouter extends Navigable<NavigationFragment> {

    String NAVIGATION_CONTROLLERS_OF_ROUTER = "navigation-controllers-of-router";

    RouterSaver getRouterSaver();

    default NavigationController obtainController(@NonNull String tag,
                                                  @NonNull FragmentManager fragmentManager,
                                                  @IdRes int navContainerId,
                                                  Class<? extends NavigationFragment> startUpFragmentCls,
                                                  Class<? extends UIContainer> uiContainerCls) {
        RouterSaver saver = getRouterSaver();
        NavigationController controller = saver.findController(tag);
        if(controller != null) return controller;

        controller = NavigationController.getInstance(tag, fragmentManager, navContainerId, startUpFragmentCls, uiContainerCls);
        saver.push(controller);
        return controller;
    }

    default void saveRouterState(Bundle outState) {
        // save all controller tags
        RouterSaver saver  = getRouterSaver();
        ArrayList<String> list = new ArrayList<>(saver.obtainTagList());
        outState.putStringArrayList(NAVIGATION_CONTROLLERS_OF_ROUTER,list);
    }

    default void restoreRouterState(Bundle bundle, @NonNull FragmentManager fragmentManager) {
        // restore all controller tags
        RouterSaver saver = getRouterSaver();
        if(bundle!=null) {
            ArrayList<String> list;
            list = bundle.getStringArrayList(NAVIGATION_CONTROLLERS_OF_ROUTER);
            if(list!=null) {
                saver.clear();
                saver.clear();

                // restore controllers stack
                int size = list.size();
                String t;
                NavigationController f;

                for(int i = 0; i < size; i++) {
                    t = list.get(i);
                    f = NavigationController.restoreInstance(t, fragmentManager);
                    if(f != null) {
                        saver.push(f);
                    }
                }

            }
        }
    }

    @Override
    default boolean onNavigateBack() {
        return navigateBack();
    }

    @Override
    default boolean navigateBack() {
        return navigateBack(true);
    }

    @Override
    default boolean navigateBack(boolean animated) {
        RouterSaver saver = getRouterSaver();
        NavigationController controller = getRouterSaver().controllerTop();
        if(controller==null) return false;
        boolean result = controller.navigateBack();
        if(!result) {
            saver.pop();
            if(saver.count() != 0) {
                controller.quit();
                return true;
            } else return false;
        }
        return true;
    }

    @Override
    default void navigateTo(NavigationFragment nav) {
        // navigate latest controller
        navigateTo(nav, true);
    }

    @Override
    default void navigateTo(NavigationFragment nav, boolean animated) {
        RouterSaver saver = getRouterSaver();
        NavigationController controller = saver.controllerTop();

        if(controller != null) controller.navigateTo(nav, animated);
    }

    @Override
    default boolean requestBack() {
        return onNavigateBack();
    }
}
