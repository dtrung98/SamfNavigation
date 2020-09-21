package com.ldt.navigation.container;

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
 * Provides logic to control multiple NavigationController inside
 */
public interface FragmentContainerNavigator extends ContainerNavigator {
    String NAVIGATION_CONTROLLERS_OF_ROUTER = "navigation-controllers-of-router";

    /**
     * Dismiss this ContainerNavigator
     */
    @Override
    void dismiss();

    @Override
    void present(@NonNull String uniquePresentName, Class<? extends UIContainer> uiContainerClass, NavigationFragment... initialFragments);

    /**
     * Provide the FragmentManager that will be used to manage fragment controllers
     * @return FragmentManager
     */
    FragmentManager provideFragmentManager();

    NavigatorAttribute getNavigatorAttribute();

    /**
     * Present this fragment in a new NavigationController
     * @param uniqueTag unique tag identify the navigation controller. If any controller has this tag, this command will be ignored
     * @param viewContainerId container view that the controller will be put into
     * @param initialFragmentClass Fragment that will be presented
     * @param uiContainerClass  the ui container that will manage layout ui for controller
     * @return the Controller
     */
    default NavigationControllerFragment present(@NonNull String uniqueTag,
                                                 @IdRes int viewContainerId,
                                                 Class<? extends UIContainer> uiContainerClass,
                                                 Class<? extends NavigationFragment> initialFragmentClass) {
        return present(uniqueTag, viewContainerId, uiContainerClass, initialFragmentClass, null);
    }

    /**
     * Present this fragment in a new NavigationController
     * @param uniqueTag unique tag identify the navigation controller. If any controller has this tag, this command will be ignored
     * @param viewContainerId container view that the controller will be put into
     * @param initialFragmentClass Fragment that will be presented
     * @param uiContainerClass  the ui container that will manage layout ui for controller
     * @return the Controller
     */
    default NavigationControllerFragment present(@NonNull String uniqueTag,
                                                 @IdRes int viewContainerId,
                                                 Class<? extends UIContainer> uiContainerClass,
                                                 Class<? extends NavigationFragment> initialFragmentClass,
                                                 @Nullable Bundle initialFragmentArgument) {
        NavigatorAttribute saver = getNavigatorAttribute();
        NavigationControllerFragment controller = saver.findController(uniqueTag);

        if(controller == null) {
            controller = NavigationControllerFragment.createNewOrGetController(uniqueTag, provideFragmentManager(), viewContainerId, uiContainerClass, initialFragmentClass, null);
            saver.push(controller);
        }

        controller.setParentNavigator(this);
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
    default NavigationControllerFragment present(@NonNull String uniqueTag,
                                                 @IdRes int viewContainerId,
                                                 Class<? extends UIContainer> uiContainerClass,
                                                 NavigationFragment... initialFragments
                                                   ) {
        NavigatorAttribute saver = getNavigatorAttribute();
        NavigationControllerFragment controller = saver.findController(uniqueTag);

        if(controller == null) {
            controller = NavigationControllerFragment.createNewOrGetController(uniqueTag, provideFragmentManager(), viewContainerId, uiContainerClass, initialFragments);
            saver.push(controller);
        }

        controller.setParentNavigator(this);
        return controller;
    }

    @Override
    default void dismissNavigationController(@NonNull NavigationControllerFragment controller) {
        NavigatorAttribute saver = getNavigatorAttribute();
        saver.remove(controller);

        if(saver.count() != 0)
        controller.removeFromFragmentManager();
        else dismiss();
    }

    default void onSaveNavigatorState(Bundle outState) {
        // save all controller tags
        NavigatorAttribute saver  = getNavigatorAttribute();
        ArrayList<String> list = new ArrayList<>(saver.obtainTagList());
        outState.putStringArrayList(NAVIGATION_CONTROLLERS_OF_ROUTER,list);
    }

    default void onCreateNavigator(Bundle bundle) {
        // restore all controller tags
        NavigatorAttribute saver = getNavigatorAttribute();
        if(saver.doesRouterNeedToRestore() && bundle!=null) {
            onRestoreNavigatorState(bundle);
            saver.routerRestored();
        }
    }

    default void onRestoreNavigatorState(Bundle bundle) {
        ArrayList<String> list;
        NavigatorAttribute saver = getNavigatorAttribute();
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
                    f.setParentNavigator(this);
                }
            }

        }
    }

    @Override
    default boolean isAllowedToBack() {
        NavigatorAttribute saver = getNavigatorAttribute();
        NavigationControllerFragment controllerFragment = getNavigatorAttribute().controllerTop();

        /* there are no controller fragment in the stack */
        if(controllerFragment == null) {
            return true;
        }

        /* decide by the focused fragment controller */
        return controllerFragment.isAllowedToBack();
    }

    default boolean requestBack() {
        return requestBack(true);
    }

    @Override
    default boolean requestBack(boolean animated) {
        return isAllowedToBack() && navigateBack(animated);
    }

    @Override
    default boolean navigateBack() {
        return navigateBack(true);
    }

    @Override
    default boolean navigateBack(boolean animated) {
        NavigatorAttribute saver = getNavigatorAttribute();
        NavigationControllerFragment controller = getNavigatorAttribute().controllerTop();
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
    default void navigate(NavigationFragment nav) {
        // navigate in the latest controller
        navigate(nav, true);
    }

    /**
     * Find the current focus child navigation controller then navigate it to this fragment
     * @param nav fragment to be navigated
     */
    default void navigate(NavigationFragment nav, boolean animated) {
        NavigatorAttribute saver = getNavigatorAttribute();
        NavigationControllerFragment controller = saver.controllerTop();

        if(controller != null) controller.navigate(null, nav, animated);
    }

    default NavigationControllerFragment findController(String controllerTag) {
        return getNavigatorAttribute().findController(controllerTag);
    }
}
