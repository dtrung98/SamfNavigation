package com.ldt.inspired;

/**
 * Quản lý nhiều NavigationController theo nguyên tắc Last In Fast Out.
 * <br>Stack này luôn đảm bảo ContainerController nằm sau, FragmentController nằm trước
 */
public class StackContainerController extends ContainerController<NavController<?>> {
    public void doSomething() {
        getChildControllers().add(new SplitContainerController());
        getChildControllers().add(new SingleNavController());
        getChildControllers().add(new FragmentController());
    }

    @Override
    public void navigateTo(NavController<?> nextOne) {

    }

    @Override
    public void switchNew(NavController<?> newOne) {

    }
}
