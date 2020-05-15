package com.ldt.navigation.router;

import androidx.annotation.NonNull;

import com.ldt.navigation.NavigationController;
import com.ldt.navigation.NavigationFragment;
import com.ldt.navigation.uicontainer.ExpandStaticContainer;

public interface SplitRouter extends BaseSplitRouter {

    @NonNull
    Class<? extends NavigationFragment> provideDefaultDetailFragment();

    @NonNull
    Class<? extends NavigationFragment> provideDefaultMasterFragment();

    default void rightRouterSwitchNew(NavigationFragment fragment) {
        SplitRouterSaver saver = getRouterSaver();
        NavigationController controller = saver.findController(saver.getDetailControllerTag());

        // chưa tồn tại right router, tạo một cái với startup là fragment chỉ định
        if(controller == null)
            controller = presentRightRouter(saver.getDetailControllerTag(), saver.getRightSubContainerId());
        controller.switchNew(fragment);
    }

    default void rightRouterNavigateTo(NavigationFragment fragment, boolean animated) {
        SplitRouterSaver saver = getRouterSaver();
        NavigationController controller = saver.findController(saver.getMasterControllerTag());

        if(controller == null) {
            controller = presentLeftRouter(saver.getMasterControllerTag(), saver.getLeftSubContainerId());
            controller.switchNew(fragment);
        }
        else controller.navigateTo(fragment);
    }

    default void leftRouterNavigateTo(NavigationFragment fragment, boolean animated) {
        SplitRouterSaver saver = getRouterSaver();
        NavigationController controller = saver.findController(saver.getMasterControllerTag());

        if(controller == null)
            controller = presentLeftRouter(saver.getMasterControllerTag(), saver.getLeftSubContainerId());
        controller.navigateTo(fragment, animated);
    }

    @Override
    default NavigationController presentLeftRouter(String leftControllerTag, int leftContainerViewId) {
        return presentController(leftControllerTag, leftContainerViewId, ExpandStaticContainer.class, provideDefaultMasterFragment(), null);
    }

    @Override
    @NonNull
    default NavigationController presentRightRouter(String rightControllerTag, int rightContainerViewId) {
        return presentController(rightControllerTag, rightContainerViewId, ExpandStaticContainer.class, provideDefaultDetailFragment(), null);
    }
}
