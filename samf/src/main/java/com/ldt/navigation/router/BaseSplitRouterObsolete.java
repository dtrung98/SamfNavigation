package com.ldt.navigation.router;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.ldt.navigation.NavigationController;
import com.ldt.navigation.NavigationFragment;
import com.ldt.navigation.R;

/*
FlexRouter quản lý giao diện phức tạp dạng 2-rows-panel ở màn hình rộng, thu gọn thành 1-rows  ở màn hình hẹp
 */
public interface BaseSplitRouterObsolete extends FlexRouter {
    String TAG = "BaseSplitRouter";

    String LEFT_ROUTER_TAG = "left-router-tag";
    String RIGHT_ROUTER_TAG = "right-router-tag";
    String RIGHT_ROUTER_HAS_INTRO_FRAGMENT = "right-router-has-intro-fragment";
    String RIGHT_ROUTER_INTRO_FRAGMENT_TAG = "right-router-intro-fragment-tag";

    NavigationController presentLeftRouter(String leftControllerTag, int leftContainerViewId);

    /**
     * Override phương thức này để hiển thị Intro Fragment/Empty Fragment ở router phải khi ở màn hình rộng
     * @param rightControllerTag
     * @param rightContainerViewId
     * @return
     */
    default NavigationController presentRightRouter(String rightControllerTag, int rightContainerViewId) {
        return null;
    }

    default void presentRouter(View rootView) {

        if (rootView instanceof ConstraintLayout)
            presentRouter((ConstraintLayout) rootView);

    }

    default View inflateSingleModeLayout(@NonNull Context context, @Nullable ViewGroup rootView) {
        return LayoutInflater.from(context).inflate(R.layout.main_common, rootView, false);
    }

    default View inflateSplitModeLayout(@NonNull Context context, @Nullable ViewGroup rootView) {
        return LayoutInflater.from(context).inflate(R.layout.main_two_panel, rootView, false);
    }

    /**
     * Goị phương thức này trong setContentView, hoặc onCreateView/onCreateContentView của Fragment/NavigationFragment
     * @param context
     * @return
     */
    default View provideLayout(@NonNull Context context) {
        SplitRouterSaverObsolete saver = getRouterSaver();

        SplitCondition condition = new SplitCondition();

        int screenWidthDp = context.getResources().getConfiguration().screenWidthDp;
        int screenHeightDp = context.getResources().getConfiguration().screenHeightDp;
        float dpUnit = context.getResources().getDimension(R.dimen.dpUnit);

        onConfigureSplitRouter(condition, screenWidthDp, screenHeightDp);
        saver.setUp(condition, screenWidthDp, screenHeightDp);

        saver.mLeftSubContainerId = R.id.left_container;
        saver.mRightSubContainerId = R.id.right_container;
        saver.mFloatingSubContainerId = R.id.floating_container;

        View rootView = (saver.isInSplitMode()) ? inflateSplitModeLayout(context, null) : inflateSingleModeLayout(context, null);

        if(saver.isInSplitMode()) {
            View leftContainer = rootView.findViewById(getRouterSaver().getLeftSubContainerId());
            View rightContainer = rootView.findViewById(getRouterSaver().getRightSubContainerId());
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
        SplitRouterSaverObsolete saver = getRouterSaver();
        FlexRouter.super.onCreateRouter(bundle);
        // Thêm và hiển thị router trái nếu chưa có
        presentLeftRouter(saver.mMasterControllerTag, R.id.left_container);

        // Thêm và hiển thị router phải ở giao diện split nếu chưa có
        if(saver.isInSplitMode()) {
            NavigationController controller = findController(saver.mDetailControllerTag);
            if(controller==null)
                presentRightRouter(saver.mDetailControllerTag, R.id.right_container);

        } else if(bundle != null && saver.mDetailControllerInitialFragment != null) {
            // Xóa bỏ intro fragment tự sinh ra ở giao diện split
            NavigationController controller = findController(saver.mDetailControllerTag);
            if(controller != null) {
                NavigationFragment introFragment = controller.findFragment(saver.mDetailControllerInitialFragment);
                if(introFragment != null) {
                    Log.d(TAG, "detail controller will dismiss initial fragment soon");
                    int count = controller.getFragmentCount();
                    if(count == 1) controller.quit();
                    else controller.dismissFragment(introFragment, false);
                } else {
                    Log.d(TAG, "no initial fragment in detail controller found");
                }
            } else Log.d(TAG, "detail controller doesn't exist");
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

        public SplitCondition configAgain() {
            configOnce = false;
            return this;
        }

        public SplitCondition configLeftWide(int dp) {
            leftWide = dp;
            return this;
        }

        /*
        Nếu leftWide được đặt thì giá trị này bị bỏ qua
         */
        public void configRightWide(int dp) {
            rightWide = dp;
        }

        public SplitCondition widerThan(int dp) {
            splitWhenWiderThan = dp;
            return this;
        }

        public SplitCondition tallerThan(int dp) {
            splitWhenTallerThan = dp;
            return this;
        }

        public void commit() {

        }
    }

    default void onConfigureSplitRouter(SplitCondition splitWhen, int screenWidthDp, int screenHeightDp) {
        splitWhen
                .widerThan(720)
                .tallerThan(-1)
                .configLeftWide(350)
                .configRightWide(-1);
    }

    default void presentRouter(ConstraintLayout rootView) {
        // Add 3 FrameLayout Container to rootView
    }

    @Override
    SplitRouterSaverObsolete getRouterSaver();

    @Override
    default void onSaveRouterState(Bundle outState) {
        FlexRouter.super.onSaveRouterState(outState);
        SplitRouterSaverObsolete saver = getRouterSaver();
        outState.putString(LEFT_ROUTER_TAG, saver.mMasterControllerTag);
        outState.putString(RIGHT_ROUTER_TAG, saver.mDetailControllerTag);

        if(saver.isInSplitMode()) {
            NavigationController rightController = findController(saver.getDetailControllerTag());
            if (rightController != null) {
                if(rightController.isInitialFragmentRootFragment()) {
                    outState.putString(RIGHT_ROUTER_INTRO_FRAGMENT_TAG, rightController.getFragmentTagAt(0));
                }
            }
        }
    }

    @Override
    default void onRestoreRouterState(Bundle bundle) {
        SplitRouterSaverObsolete saver = getRouterSaver();
        FlexRouter.super.onRestoreRouterState(bundle);
        String leftTag = bundle.getString(LEFT_ROUTER_TAG, "left-router");
        String rightTag = bundle.getString(RIGHT_ROUTER_TAG, "right-router");
        saver.mDetailControllerInitialFragment = bundle.getString(RIGHT_ROUTER_INTRO_FRAGMENT_TAG);
    }

    @Override
    default boolean navigateBack(boolean animated) {
        // ở chế độ split
        // back tới fragment root của right router rồi tới left router, không quit right router và left router
        // khi cả left và right đều chỉ còn root, thì trả về false


        SplitRouterSaverObsolete saver = getRouterSaver();
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

                if(controller.mControllerTag.equals(saver.mDetailControllerTag) || controller.mControllerTag.equals(saver.mMasterControllerTag)) {
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
        else return FlexRouter.super.navigateBack(animated);
    }
}