package com.ldt.navigationx;

/**
 * FragmentController quản lý một stack các Fragment theo nguyên tắc Last In Fast Out
 * Chỉ một Fragment được hiển thị (top fragment) tại một thời điểm.
 * <br> FragmentController có thể linh hoạt thay đổi giao diện của nó khi kích thước màn hình thay đổi,
 * thông qua lớp cung cấp giao diện UI Container
 *
  */
public class FragmentController extends NavigationController<NavigationFragment> {
    @Override
    public void navigateTo(NavigationFragment nextOne) {

    }

    @Override
    public void switchNew(NavigationFragment newOne) {

    }
}
