package com.ldt.navigation.router;

import android.os.Bundle;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

import com.ldt.navigation.NavigationController;
import com.ldt.navigation.NavigationFragment;
import com.ldt.navigation.uicontainer.UIContainer;

import java.util.ArrayList;

/**
 *  Interface cung cấp logic cho {@link androidx.fragment.app.FragmentActivity} or {@link androidx.fragment.app.Fragment} giúp điều khiển một stack các NavigationController nằm chồng lên nhau
 */
public interface FlexRouter extends Router {
    String NAVIGATION_CONTROLLERS_OF_ROUTER = "navigation-controllers-of-router";

    FragmentManager provideFragmentManager();

    RouterSaver getRouterSaver();

    default NavigationController presentController(@NonNull String tag,
                                                   @IdRes int navContainerId,
                                                   Class<? extends NavigationFragment> startUpFragmentCls,
                                                   Class<? extends UIContainer> uiContainerCls) {
        RouterSaver saver = getRouterSaver();
        NavigationController controller = saver.findController(tag);

        if(controller == null) {
            controller = NavigationController.getInstance(tag, provideFragmentManager(), navContainerId, startUpFragmentCls, uiContainerCls);
            saver.push(controller);
        }

        controller.setRouter(this);
        return controller;
    }

    @Override
    default void finishController(@NonNull NavigationController controller) {
        RouterSaver saver = getRouterSaver();
        saver.remove(controller);

        if(saver.count() != 0)
        controller.removeFromFragmentManager();
        else finish();
    }

    default void onSaveRouterState(Bundle outState) {
        // save all controller tags
        RouterSaver saver  = getRouterSaver();
        ArrayList<String> list = new ArrayList<>(saver.obtainTagList());
        outState.putStringArrayList(NAVIGATION_CONTROLLERS_OF_ROUTER,list);
    }

    default void onCreateRouter(Bundle bundle) {
        // restore all controller tags
        RouterSaver saver = getRouterSaver();
        if(saver.doesRouterNeedToRestore() && bundle!=null) {
            onRestoreRouterState(bundle);
            saver.routerRestored();
        }
    }

    default void onRestoreRouterState(Bundle bundle) {
        ArrayList<String> list;
        RouterSaver saver = getRouterSaver();
        FragmentManager fragmentManager = provideFragmentManager();
        list = bundle.getStringArrayList(NAVIGATION_CONTROLLERS_OF_ROUTER);
        if(list!=null) {
            saver.clear();

            // restore controllers stack
            int size = list.size();
            String t;
            NavigationController f;

            for(int i = 0; i < size; i++) {
                t = list.get(i);
                f = NavigationController.findInstance(t, fragmentManager);
                if(f != null) {
                    saver.push(f);
                    f.setRouter(this);
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
                controller.removeFromFragmentManager();
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

    default NavigationController findController(String controllerTag) {
        return getRouterSaver().findController(controllerTag);
    }

    @Override
    default boolean requestBack() {
        return onNavigateBack();
    }
}
