package com.ldt.navigationx;

/**
 * SingleFragmentController chỉ chứa một Fragment, không thể navigate đi tới fragment khác được<br>
 * Dùng SingleFragmentController để push 1 Fragment vào ContainerController mà không cần tạo FragmentController mới
 */
public class SingleNavigationController extends NavigationController<NavigationFragment> {
    @Override
    public void navigateTo(NavigationFragment nextOne) {

    }

    @Override
    public void switchNew(NavigationFragment newOne) {

    }
}
