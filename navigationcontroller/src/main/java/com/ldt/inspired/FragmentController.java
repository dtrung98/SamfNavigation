package com.ldt.inspired;

import androidx.fragment.app.Fragment;

/**
 * FragmentController quản lý một stack các Fragment theo nguyên tắc Last In Fast Out
 * Chỉ một Fragment được hiển thị (top fragment) tại một thời điểm.
 * <br> FragmentController có thể linh hoạt thay đổi giao diện của nó khi kích thước màn hình thay đổi,
 * thông qua lớp cung cấp giao diện UI Container
 *
  */
public class FragmentController extends NavController<Fragment> {
    @Override
    public void navigateTo(Fragment nextOne) {

    }

    @Override
    public void switchNew(Fragment newOne) {

    }
}
