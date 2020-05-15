package com.ldt.navigation.router;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

import com.ldt.navigation.NavigationController;
import com.ldt.navigation.NavigationFragment;
import com.ldt.navigation.uicontainer.ExpandContainer;
import com.ldt.navigation.uicontainer.ExpandStaticContainer;
import com.ldt.navigation.uicontainer.UIContainer;

public interface SplitRouter extends BaseSplitRouter {

    @NonNull
    Class<? extends NavigationFragment> provideDefaultDetailFragment();

    @NonNull
    Class<? extends NavigationFragment> provideDefaultMasterFragment();

    default void rightRouterSwitchNew(Class<? extends NavigationFragment> fragmentClazz) {
        SplitRouterSaver saver = getRouterSaver();
        NavigationController controller = saver.findController(saver.getRightTag());

        // chưa tồn tại right router, tạo một cái với startup là fragment chỉ định
        if(controller == null) {
            presentController(saver.getRightTag(), saver.getRightSubContainerId(), provideDefaultDetailFragment(), ExpandContainer.class);
        } else try {
            controller.switchNew(fragmentClazz.newInstance());
        } catch (Exception ignored) {

        }
    }

    default void rightRouterSwitchNew(NavigationFragment fragment) {
        SplitRouterSaver saver = getRouterSaver();
        NavigationController controller = saver.findController(saver.getRightTag());

        // chưa tồn tại right router, tạo một cái với startup là fragment chỉ định
        if(controller == null)
            controller = presentRightRouter(saver.getRightTag(), saver.getRightSubContainerId());
        controller.switchNew(fragment);
    }

    default void rightRouterNavigateTo(NavigationFragment fragment, boolean animated) {
        SplitRouterSaver saver = getRouterSaver();
        NavigationController controller = saver.findController(saver.getLeftTag());

        if(controller == null) {
            controller = presentLeftRouter(saver.getLeftTag(), saver.getLeftSubContainerId());
            controller.switchNew(fragment);
        }
        else controller.navigateTo(fragment);
    }

    default void leftRouterNavigateTo(NavigationFragment fragment, boolean animated) {
        SplitRouterSaver saver = getRouterSaver();
        NavigationController controller = saver.findController(saver.getLeftTag());

        if(controller == null)
            controller = presentLeftRouter(saver.getLeftTag(), saver.getLeftSubContainerId());
        controller.navigateTo(fragment, animated);
    }

    @Override
    default NavigationController presentLeftRouter(String leftControllerTag, int leftContainerViewId) {
        return presentController(leftControllerTag, leftContainerViewId, provideDefaultMasterFragment(), ExpandStaticContainer.class);
    }

    @Override
    @NonNull
    default NavigationController presentRightRouter(String rightControllerTag, int rightContainerViewId) {
        return presentController(rightControllerTag, rightContainerViewId, provideDefaultDetailFragment(), ExpandStaticContainer.class);
    }
}
