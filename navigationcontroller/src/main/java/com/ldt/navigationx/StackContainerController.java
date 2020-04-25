package com.ldt.navigationx;

/**
 * Quản lý nhiều NavigationController theo nguyên tắc Last In Fast Out.
 * <br>Stack này luôn đảm bảo ContainerController nằm sau, FragmentController nằm trước
 */
public class StackContainerController extends ContainerController<NavigationController<?>> {
    public void doSomething() {
        getChildControllers().add(new SplitContainerController());
        getChildControllers().add(new SingleNavigationController());
        getChildControllers().add(new FragmentController());
    }

    @Override
    public void navigateTo(NavigationController<?> nextOne) {

    }

    @Override
    public void switchNew(NavigationController<?> newOne) {

    }
}
