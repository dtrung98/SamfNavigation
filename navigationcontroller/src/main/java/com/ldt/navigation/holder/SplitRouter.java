package com.ldt.navigation.holder;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;

import com.ldt.navigation.NavigationController;
import com.ldt.navigation.NavigationFragment;
import com.ldt.navigation.R;
import com.ldt.navigation.uicontainer.UIContainer;

/*
FlexRouter quản lý giao diện phức tạp dạng 2-rows-panel ở màn hình rộng, thu gọn thành 1-rows  ở màn hình hẹp
 */
public interface SplitRouter extends FlexRouter {

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

    default NavigationController presentFloatingNavigator(String tag,
                                                          FragmentManager fragmentManager,
                                                          Class<? extends NavigationFragment> startUpFragmentCls,
                                                          Class<? extends UIContainer> uiContainerCls) {
        return presentNavigator(tag, fragmentManager, R.id.float_container, startUpFragmentCls, uiContainerCls);
    }

    /**
     * Goị phương thức này trong setContentView, hoặc onCreateView/onCreateContentView của Fragment/NavigationFragment
     * @param context
     * @return
     */
    default View provideLayout(@NonNull Context context) {
        SplitRouterSaver saver = getRouterSaver();

        SplitCondition condition = new SplitCondition();

        int screenWidthDp = context.getResources().getConfiguration().screenWidthDp;
        int screenHeightDp = context.getResources().getConfiguration().screenHeightDp;

        setUpConfig(condition, screenWidthDp, screenHeightDp);
        saver.setUp(condition, screenWidthDp, screenHeightDp);

        View rootView = (saver.isInSplitMode()) ? inflateSplitModeLayout(context, null) : inflateSingleModeLayout(context, null);
        return rootView;
    }

    @Override
    default void onCreateRouter(Bundle bundle, @NonNull FragmentManager fragmentManager) {
        SplitRouterSaver saver = getRouterSaver();
        FlexRouter.super.onCreateRouter(bundle, fragmentManager);

        // Thêm và hiển thị router trái nếu chưa có
        presentLeftRouter(saver.mLeftContainerTag, R.id.left_container);

        // Thêm và hiển thị router phải ở giao diện split nếu chưa có
        if(saver.isInSplitMode()) {
            NavigationController  rightRouter = presentRightRouter(saver.mRightContainerTag, R.id.right_container);
            // Lưu lại tag của intro fragment ở lần chạy đầu tiên
            if(rightRouter != null && bundle == null) {
                saver.mRightRouterIntroFragmentTag = rightRouter.getFragmentTagAt(0);
            } else if(rightRouter == null){
                saver.mRightRouterIntroFragmentTag = null;
            }
        } else if(bundle != null && saver.mRightRouterIntroFragmentTag != null) {
            // Xóa bỏ intro fragment tự sinh ra ở giao diện split
            NavigationController controller = saver.findController(saver.mRightContainerTag);
            if(controller != null) {
                NavigationFragment introFragment = controller.findFragment(saver.mRightRouterIntroFragmentTag);
                if(introFragment != null) {
                    int count = controller.getFragmentCount();
                    if(count==1) controller.quit();
                    else controller.dismissFragment(introFragment, false);
                }
            }
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

    default void setUpConfig(SplitCondition splitWhen, int screenWidthDp, int screenHeightDp) {
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
    SplitRouterSaver getRouterSaver();

    @Override
    default void onSaveRouterState(Bundle outState) {
        FlexRouter.super.onSaveRouterState(outState);
        SplitRouterSaver saver = getRouterSaver();
        outState.putString(LEFT_ROUTER_TAG, saver.mLeftContainerTag);
        outState.putString(RIGHT_ROUTER_TAG, saver.mRightContainerTag);
        outState.putString(RIGHT_ROUTER_INTRO_FRAGMENT_TAG,saver.mRightRouterIntroFragmentTag);
    }

    @Override
    default void onRestoreRouterState(Bundle bundle, @NonNull FragmentManager fragmentManager) {
        SplitRouterSaver saver = getRouterSaver();
        FlexRouter.super.onRestoreRouterState(bundle, fragmentManager);
        String leftTag = bundle.getString(LEFT_ROUTER_TAG, "left-router");
        String rightTag = bundle.getString(RIGHT_ROUTER_TAG, "right-router");
        saver.mRightRouterIntroFragmentTag = bundle.getString(RIGHT_ROUTER_INTRO_FRAGMENT_TAG);
    }

    @Override
    default boolean navigateBack(boolean animated) {
        // ở chế độ split
        // back tới fragment root của right router rồi tới left router và không quit right router và left router
        // khi không còn back được nữa (cả left và right đều chỉ còn root), thì trả về false


        SplitRouterSaver saver = getRouterSaver();
        if(!saver.isInSplitMode())
        return FlexRouter.super.navigateBack(animated);
        else {
            return FlexRouter.super.navigateBack(animated);
        }
    }

    /*    @Override
    default void finishController(@NonNull NavigationController controller) {
        SplitRouterSaver saver = getRouterSaver();
        if(!controller.mTag.equals(saver.mLeftContainerTag) && !controller.mTag.equals(saver.mRightContainerTag))
        FlexRouter.super.finishController(controller);
    }*/
}