package com.ldt.navigation.router;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import com.ldt.navigation.NavigationController;
import com.ldt.navigation.NavigationFragment;
import com.ldt.navigation.R;
import com.ldt.navigation.uicontainer.ExpandStaticContainer;

/*
FlexRouter cung cấp sẵn 2 Navigation Controller Master-Detail.
<br> Ở giao diện Side-by-side, Master nằm bên trái và Detail nằm bên phải, chúng là 2 controller riêng biệt
<br> Ở giao diện Compact, Detail biến mất, toàn bộ Fragment trong Detail được gộp vào Master
 */
public interface SplitRouter2 extends BaseSplitRouter2{
    String TAG = "SplitRouter2";

    default View inflateSingleModeLayout(@NonNull Context context, @Nullable ViewGroup rootView) {
        return LayoutInflater.from(context).inflate(R.layout.main_common, rootView, false);
    }

    default View inflateSplitModeLayout(@NonNull Context context, @Nullable ViewGroup rootView) {
        return LayoutInflater.from(context).inflate(R.layout.main_two_panel, rootView, false);
    }

    /**
     * Goị phương thức này trong {@link Activity#setContentView(View)} hoặc  onCreateView/onCreateContentView của Fragment/NavigationFragment
     * @param context Context
     * @return View
     */
    default View provideView(@NonNull Context context) {
        SplitRouterSaver2 saver = getRouterSaver();

        BaseSplitRouter.SplitCondition condition = new BaseSplitRouter.SplitCondition();

        int screenWidthDp = context.getResources().getConfiguration().screenWidthDp;
        int screenHeightDp = context.getResources().getConfiguration().screenHeightDp;
        float dpUnit = context.getResources().getDimension(R.dimen.dpUnit);

        onConfigureSplitRouter(condition, screenWidthDp, screenHeightDp);
        saver.setUp(condition, screenWidthDp, screenHeightDp);

        saver.mMasterSubContainerId = R.id.left_container;
        saver.mDetailSubContainerId = R.id.right_container;
        saver.mFloatingSubContainerId = R.id.floating_container;

        View rootView = (saver.isInSplitMode()) ? inflateSplitModeLayout(context, null) : inflateSingleModeLayout(context, null);

        if(saver.isInSplitMode()) {
            View leftContainer = rootView.findViewById(getRouterSaver().getMasterSubContainerId());
            View rightContainer = rootView.findViewById(getRouterSaver().getDetailSubContainerId());
            if(saver.leftWide!=-1) {
                leftContainer.getLayoutParams().width = (int) (saver.leftWide*dpUnit);
                rightContainer.getLayoutParams().width = 0;
            } else if(saver.rightWide !=-1) {
                leftContainer.getLayoutParams().width = 0;
                rightContainer.getLayoutParams().width = (int) (saver.rightWide * dpUnit);
            } else {
                saver.leftWide = 350;
                leftContainer.getLayoutParams().width = (int) (saver.leftWide * dpUnit);
                rightContainer.getLayoutParams().width = 0;
            }
        }
        return rootView;
    }

    @Override
    default void onCreateRouter(Bundle bundle) {

        /*Get Router Saver */
        SplitRouterSaver2 saver = getRouterSaver();
        /* Restore controller stack properties*/
        BaseSplitRouter2.super.onCreateRouter(bundle);
        Log.d(TAG, "onCreateRouter: with intro fragment tag:"+saver.mRightRouterIntroFragmentTag);
        /* Present Master Controller if not exists */
        presentMasterController(saver.getMasterControllerTag(), R.id.left_container);

        /* Thêm và hiển thị Detail Controller ở giao diện split nếu chưa có */
        if(saver.isInSplitMode()) {
            NavigationController controller = saver.findController(saver.getDetailControllerTag());
            if(controller==null) {
                NavigationController rightRouter = presentDetailController(saver.getDetailControllerTag(), R.id.right_container);
                if (rightRouter != null && saver.mRightRouterHasIntro) {
                    // start up fragment là intro fragment
                    // nó cần bị xóa bỏ khi controller ở giao diện một cột
                    // lưu lại tham số class type của intro fragment
                    // dùng class type này và root fragment của right controller để lấy tag của intro fragment
                    saver.setDefaultIntroFragmentClass(rightRouter.getInitialFragmentClass());
                }
            }
        } else if(bundle != null && saver.mRightRouterIntroFragmentTag != null) {
            // Xóa bỏ intro fragment tự sinh ra ở giao diện split
            NavigationController controller = saver.findController(saver.getDetailControllerTag());
            if(controller != null) {
                NavigationFragment introFragment = controller.findFragment(saver.mRightRouterIntroFragmentTag);
                if(introFragment != null) {
                    Log.d(TAG, "i mờ gô in to quýt");
                    int count = controller.getFragmentCount();
                    if(count==1) controller.quit();
                    else controller.dismissFragment(introFragment, false);
                } else {
                    Log.d(TAG, "not quýt");
                }
            } else Log.d(TAG, "right controller is null");
        }

        // đảm bảo thứ tự left-router là 1, right-router là 2 trong stack
        saver.sort();
    }

    class SplitCondition {
        boolean configOnce = true;
        int splitWhenWiderThan = 720;
        int splitWhenTallerThan = -1;
        int leftWide = -1;
        int rightWide = -1;

        public void setRightRouterStartUpFragmentIsIntroFragment(boolean startUpFragmentInRightRouterIsIntroFragment) {
            this.introStartupInRightRouter = startUpFragmentInRightRouterIsIntroFragment;
        }

        boolean introStartupInRightRouter = true;

        public SplitRouter2.SplitCondition configAgain() {
            configOnce = false;
            return this;
        }

        public SplitRouter2.SplitCondition configLeftWide(int dp) {
            leftWide = dp;
            return this;
        }

        /*
        Nếu leftWide được đặt thì giá trị này bị bỏ qua
         */
        public void configRightWide(int dp) {
            rightWide = dp;
        }

        public SplitRouter2.SplitCondition widerThan(int dp) {
            splitWhenWiderThan = dp;
            return this;
        }

        public SplitRouter2.SplitCondition tallerThan(int dp) {
            splitWhenTallerThan = dp;
            return this;
        }

        public void commit() {

        }
    }

    default void onConfigureSplitRouter(BaseSplitRouter.SplitCondition splitWhen, int screenWidthDp, int screenHeightDp) {
        splitWhen
                .widerThan(720)
                .tallerThan(-1)
                .configLeftWide(350)
                .configRightWide(-1);
    }

    @Override
    SplitRouterSaver2 getRouterSaver();

    @Override
    default void onSaveRouterState(Bundle outState) {
        BaseSplitRouter2.super.onSaveRouterState(outState);
        SplitRouterSaver2 saver = getRouterSaver();
        outState.putString(MASTER_CONTROLLER_TAG, saver.getMasterControllerTag());
        outState.putString(DETAIL_CONTROLLER_TAG, saver.getDetailControllerTag());

        if(saver.isInSplitMode()) {
            Class<? extends NavigationFragment> introFragmentCls = saver.getIntroFragmentClass();
            NavigationController rightController = saver.findController(saver.getDetailControllerTag());
            if (rightController != null) {
                NavigationFragment rootFragment = rightController.getFragmentAt(0);
                if (rightController.getFragmentCount() == 1 && rootFragment != null && rootFragment.getClass().equals(introFragmentCls)) {
                    outState.putString(DETAIL_CONTROLLER_DEFAULT_FRAGMENT_TAG, rightController.getFragmentTagAt(0));
                }
            }
        }
    }

    @Override
    default void onRestoreRouterState(Bundle bundle) {
        SplitRouterSaver2 saver = getRouterSaver();
        BaseSplitRouter2.super.onRestoreRouterState(bundle);
        saver.mRightRouterIntroFragmentTag = bundle.getString(DETAIL_CONTROLLER_DEFAULT_FRAGMENT_TAG);
    }

    @Override
    default boolean navigateBack(boolean animated) {
        // ở chế độ split
        // back tới fragment root của right router rồi tới left router, không quit right router và left router
        // khi cả left và right đều chỉ còn root, thì trả về false

        SplitRouterSaver2 saver = getRouterSaver();
        if(saver.isInSplitMode()) {
            // lấy controller hợp lệ
            NavigationController controller;
            int count = saver.count();
            boolean result = true;

            for(int i = count - 1; i >= 0; i--) {
                controller = saver.controllerAt(i);
                int fragmentCount = controller.getFragmentCount();
                //  Chưa tới root thì cứ back thôi
                if(fragmentCount != 1) {
                    result = controller.navigateBack(animated);
                    break;
                }

                if(controller.mControllerTag.equals(saver.getDetailControllerTag()) || controller.mControllerTag.equals(saver.getMasterControllerTag())) {
                    // Tới root và nó là fragment left hoặc right
                    if(i!=0) continue;

                    // hết controller rồi, trả về false
                    result = false;
                    break;
                }

                // controller bình thường
                saver.popAt(i);
                if(i!=0) {
                    controller.removeFromFragmentManager();
                    result = true;
                    break;
                } else result = false;
            }
            return result;

        }
        else return BaseSplitRouter2.super.navigateBack(animated);
    }

    FragmentManager provideFragmentManager();

    @NonNull
    Class<? extends NavigationFragment> provideDefaultDetailFragment();

    @NonNull
    Class<? extends NavigationFragment> provideDefaultMasterFragment();


    @Override
    default void detailControllerSwitchNew(NavigationFragment fragment) {
        SplitRouterSaver2 saver = getRouterSaver();
        NavigationController controller = saver.findController(saver.getDetailControllerTag());

        // chưa tồn tại right router, tạo một cái
        if(controller == null)
            controller = presentDetailController(saver.getDetailControllerTag(), saver.getDetailSubContainerId());
        controller.switchNew(fragment);
    }

    @Override
    default void detailControllerNavigateTo(NavigationFragment fragment, boolean animated) {
        SplitRouterSaver2 saver = getRouterSaver();
        NavigationController controller = saver.findController(saver.getMasterControllerTag());

        if(controller == null) {
            controller = presentMasterController(saver.getMasterControllerTag(), saver.getMasterSubContainerId());
            controller.switchNew(fragment);
        }
        else controller.navigateTo(fragment);
    }

    @Override
    default void masterControllerNavigateTo(NavigationFragment fragment, boolean animated) {
        SplitRouterSaver2 saver = getRouterSaver();
        NavigationController controller = saver.findController(saver.getMasterControllerTag());

        if(controller == null)
            controller = presentMasterController(saver.getMasterControllerTag(), saver.getMasterSubContainerId());
        controller.navigateTo(fragment, animated);
    }

    default NavigationController presentMasterController(String leftControllerTag, int leftContainerViewId) {
        return presentController(leftControllerTag, leftContainerViewId, provideDefaultMasterFragment(), ExpandStaticContainer.class);
    }

    @NonNull
    default NavigationController presentDetailController(String rightControllerTag, int rightContainerViewId) {
        return presentController(rightControllerTag, rightContainerViewId, provideDefaultDetailFragment(), ExpandStaticContainer.class);
    }
}
