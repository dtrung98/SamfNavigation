package com.ldt.navigation.router;

import android.os.Bundle;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import com.ldt.navigation.NavigationController;
import com.ldt.navigation.NavigationFragment;
import com.ldt.navigation.uicontainer.UIContainer;

import java.util.ArrayList;

/**
 *  Interface cung cấp logic cho 1 {@link androidx.fragment.app.FragmentActivity}, hoặc {@link androidx.fragment.app.Fragment} để quản lý một stack các NavigationController
 */
public interface Router extends BaseRouter {
    String NAVIGATION_CONTROLLERS_OF_ROUTER = "navigation-controllers-of-router";

    /**
     * Provide the FragmentManager that will be used to manage fragment controllers
     * @return FragmentManager
     */
    FragmentManager provideFragmentManager();

    RouterSaver getRouterSaver();

    /**
     * Present this fragment in a new NavigationController
     * @param uniqueTag unique tag identify the navigation controller. If any controller has this tag, this command will be ignored
     * @param viewContainerId container view that the controller will be put into
     * @param initialFragmentClass Fragment that will be presented
     * @param uiContainerClass  the ui container that will manage layout ui for controller
     * @return the Controller
     */
    default NavigationController presentController(@NonNull String uniqueTag,
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
    default NavigationController presentController(@NonNull String uniqueTag,
                                                   @IdRes int viewContainerId,
                                                   Class<? extends UIContainer> uiContainerClass,
                                                   Class<? extends NavigationFragment> initialFragmentClass,
                                                   @Nullable Bundle initialFragmentArgument) {
        RouterSaver saver = getRouterSaver();
        NavigationController controller = saver.findController(uniqueTag);

        if(controller == null) {
            controller = NavigationController.createNewOrGetController(uniqueTag, provideFragmentManager(), viewContainerId, uiContainerClass, initialFragmentClass, null);
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
    default NavigationController presentController(@NonNull String uniqueTag,
                                                   @IdRes int viewContainerId,
                                                   Class<? extends UIContainer> uiContainerClass,
                                                   NavigationFragment... initialFragments
                                                   ) {
        RouterSaver saver = getRouterSaver();
        NavigationController controller = saver.findController(uniqueTag);

        if(controller == null) {
            controller = NavigationController.createNewOrGetController(uniqueTag, provideFragmentManager(), viewContainerId, uiContainerClass, initialFragments);
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
