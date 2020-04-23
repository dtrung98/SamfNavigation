package com.ldt.navigation.router;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

import com.ldt.navigation.NavigationController;
import com.ldt.navigation.NavigationFragment;
import com.ldt.navigation.uicontainer.ExpandContainer;
import com.ldt.navigation.uicontainer.ExpandStaticContainer;
import com.ldt.navigation.uicontainer.UIContainer;

public interface SplitRouter extends BaseSplitRouter {
    FragmentManager provideFragmentManager();

    default void switchNew(String controllerTag, Class<? extends NavigationFragment> fragmentCls, Class<? extends UIContainer> uiContainerCls) {
        SplitRouterSaver saver = getRouterSaver();
        NavigationController controller = saver.findController(controllerTag);

        if(controller == null)
            presentFloatingNavigator(controllerTag, provideFragmentManager(), fragmentCls, uiContainerCls);
        else try {
            controller.switchNew(fragmentCls.newInstance());
        } catch (Exception ignored) {}
    }

    default NavigationController presentFloatingNavigator(String controllerTag, Class<? extends NavigationFragment> startUpFragmentCls, Class<? extends UIContainer> uiContainerCls) {
        return presentFloatingNavigator(controllerTag, provideFragmentManager(), startUpFragmentCls, uiContainerCls);
    }

    @NonNull
    Class<? extends NavigationFragment> provideDefaultDetailFragment();

    @NonNull
    Class<? extends NavigationFragment> provideDefaultMasterFragment();

    default void rightRouterSwitchNew(Class<? extends NavigationFragment> fragmentClazz) {
        SplitRouterSaver saver = getRouterSaver();
        NavigationController controller = saver.findController(saver.getRightTag());

        // chưa tồn tại right router, tạo một cái với startup là fragment chỉ định
        if(controller == null) {
            presentNavigator(saver.getRightTag(), provideFragmentManager(), saver.getRightSubContainerId(), provideDefaultDetailFragment(), ExpandContainer.class);
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
        return presentNavigator(leftControllerTag, provideFragmentManager(), leftContainerViewId, provideDefaultMasterFragment(), ExpandStaticContainer.class);
    }

    @Override
    @NonNull
    default NavigationController presentRightRouter(String rightControllerTag, int rightContainerViewId) {
        return presentNavigator(rightControllerTag, provideFragmentManager(), rightContainerViewId, provideDefaultDetailFragment(), ExpandStaticContainer.class);
    }
}
