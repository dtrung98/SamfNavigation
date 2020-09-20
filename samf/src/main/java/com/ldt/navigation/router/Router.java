package com.ldt.navigation.router;

import android.os.Bundle;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import com.ldt.navigation.NavigationControllerFragment;
import com.ldt.navigation.NavigationFragment;
import com.ldt.navigation.uicontainer.UIContainer;

import java.util.ArrayList;

/**
 * Router provides logic to control multiple NavigationController inside
 */
public interface Router extends ContainerNavigator {
    String NAVIGATION_CONTROLLERS_OF_ROUTER = "navigation-controllers-of-router";

    /**
     * Provide the FragmentManager that will be used to manage fragment controllers
     * @return FragmentManager
     */
    FragmentManager provideFragmentManager();

    RouterAttribute getRouterAttribute();

    /**
     * Present this fragment in a new NavigationController
     * @param uniqueTag unique tag identify the navigation controller. If any controller has this tag, this command will be ignored
     * @param viewContainerId container view that the controller will be put into
     * @param initialFragmentClass Fragment that will be presented
     * @param uiContainerClass  the ui container that will manage layout ui for controller
     * @return the Controller
     */
    default NavigationControllerFragment presentController(@NonNull String uniqueTag,
                                                           @IdRes int viewContainerId,
                                                           Class<? extends UIContainer> uiContainerClass,
                                                           Class<? extends NavigationFragment> initialFragmentClass) {
        return presentController(uniqueTag, viewContainerId, uiContainerClass, initialFragmentClass, null);
    }

    /**
     * Present this fragment in a new NavigationController
     * @param uniqueTag unique tag identify the navigation controller. If any controller has this tag, this command will be ignored
     * @param viewContainerId container view that the controller will be put into
     * @param initialFragmentClass Fragment that will be presented
     * @param uiContainerClass  the ui container that will manage layout ui for controller
     * @return the Controller
     */
    default NavigationControllerFragment presentController(@NonNull String uniqueTag,
                                                           @IdRes int viewContainerId,
                                                           Class<? extends UIContainer> uiContainerClass,
                                                           Class<? extends NavigationFragment> initialFragmentClass,
                                                           @Nullable Bundle initialFragmentArgument) {
        RouterAttribute saver = getRouterAttribute();
        NavigationControllerFragment controller = saver.findController(uniqueTag);

        if(controller == null) {
            controller = NavigationControllerFragment.createNewOrGetController(uniqueTag, provideFragmentManager(), viewContainerId, uiContainerClass, initialFragmentClass, null);
            saver.push(controller);
        }

        controller.setRouter(this);
        return controller;
    }

    /**
     * Present this fragment in a new NavigationController
     * @param uniqueTag unique tag identify the navigation controller. If any controller has this tag, this command will be ignored
     * @param viewContainerId container view that the controller will be put into
     * @param initialFragments Fragments that will be presented
     * @param uiContainerClass  the ui container that will manage layout ui for controller
     * @return the Controller
     */
    default NavigationControllerFragment presentController(@NonNull String uniqueTag,
                                                           @IdRes int viewContainerId,
                                                           Class<? extends UIContainer> uiContainerClass,
                                                           NavigationFragment... initialFragments
                                                   ) {
        RouterAttribute saver = getRouterAttribute();
        NavigationControllerFragment controller = saver.findController(uniqueTag);

        if(controller == null) {
            controller = NavigationControllerFragment.createNewOrGetController(uniqueTag, provideFragmentManager(), viewContainerId, uiContainerClass, initialFragments);
            saver.push(controller);
        }

        controller.setRouter(this);
        return controller;
    }

    @Override
    default void dismissNavigationController(@NonNull NavigationControllerFragment controller) {
        RouterAttribute saver = getRouterAttribute();
        saver.remove(controller);

        if(saver.count() != 0)
        controller.removeFromFragmentManager();
        else dismiss();
    }

    default void onSaveRouterState(Bundle outState) {
        // save all controller tags
        RouterAttribute saver  = getRouterAttribute();
        ArrayList<String> list = new ArrayList<>(saver.obtainTagList());
        outState.putStringArrayList(NAVIGATION_CONTROLLERS_OF_ROUTER,list);
    }

    default void onCreateRouter(Bundle bundle) {
        // restore all controller tags
        RouterAttribute saver = getRouterAttribute();
        if(saver.doesRouterNeedToRestore() && bundle!=null) {
            onRestoreRouterState(bundle);
            saver.routerRestored();
        }
    }

    default void onRestoreRouterState(Bundle bundle) {
        ArrayList<String> list;
        RouterAttribute saver = getRouterAttribute();
        FragmentManager fragmentManager = provideFragmentManager();
        list = bundle.getStringArrayList(NAVIGATION_CONTROLLERS_OF_ROUTER);
        if(list!=null) {
            saver.clear();

            // restore controllers stack
            int size = list.size();
            String t;
            NavigationControllerFragment f;

            for(int i = 0; i < size; i++) {
                t = list.get(i);
                f = NavigationControllerFragment.findInstance(t, fragmentManager);
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
        RouterAttribute saver = getRouterAttribute();
        NavigationControllerFragment controller = getRouterAttribute().controllerTop();
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

    /**
     * Find the current focus child navigation controller then navigate it to this fragment
     * @param nav fragment to be navigated
     */
    default void navigateTo(NavigationFragment nav) {
        // navigate in the latest controller
        navigateTo(nav, true);
    }

    /**
     * This method has no effect
     */
    @Override
    default void navigateTo(NavigationControllerFragment nav, boolean animated) {}

    /**
     * This method is unused and has no effect
     */
    @Override
    default void navigateTo(NavigationControllerFragment nav) {}

    /**
     * Find the current focus child navigation controller then navigate it to this fragment
     * @param nav fragment to be navigated
     */
    default void navigateTo(NavigationFragment nav, boolean animated) {
        RouterAttribute saver = getRouterAttribute();
        NavigationControllerFragment controller = saver.controllerTop();

        if(controller != null) controller.navigateTo(null, nav, animated);
    }

    default NavigationControllerFragment findController(String controllerTag) {
        return getRouterAttribute().findController(controllerTag);
    }

    @Override
    default boolean requestBack() {
        return onNavigateBack();
    }
}
