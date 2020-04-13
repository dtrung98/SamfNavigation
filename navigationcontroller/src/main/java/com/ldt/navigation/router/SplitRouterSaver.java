package com.ldt.navigation.router;

import com.ldt.navigation.NavigationController;
import com.ldt.navigation.NavigationFragment;
import com.ldt.navigation.R;

public class SplitRouterSaver extends RouterSaver {
    final String mLeftTag;
    final String mRightTag;

    public int getLeftSubContainerId() {
        return mLeftSubContainerId;
    }

    public int getRightSubContainerId() {
        return mRightSubContainerId;
    }

    public String getLeftTag() {
        return mLeftTag;
    }

    public String getRightTag() {
        return mRightTag;
    }

    public int getFloatingSubContainerId() {
        return mFloatingSubContainerId;
    }

    int mLeftSubContainerId = R.id.left_container;
    int mRightSubContainerId = R.id.right_container;
    int mFloatingSubContainerId = R.id.floating_container;

    public SplitRouterSaver(String leftRouterTag, String rightRouterTag) {
        mLeftTag = leftRouterTag;
        mRightTag = rightRouterTag;
    }

/*    void setContainerTags(String leftTag, String rightTag) {
        mLeftTag = leftTag;
        mRightTag = rightTag;
    }*/

    boolean mConfigOnce = true;
    boolean mAlreadyConfig = false;
    public boolean isAlreadyConfig() {
        return mAlreadyConfig;
    }
    int splitWhenWiderThan = 720;
    int splitWhenTallerThan = -1;
    int leftWide = 350;
    int rightWide = -1;
    boolean mInSplitScreen = false;
    String mRightRouterIntroFragmentTag = null;
    boolean mRightRouterHasIntro = false;
    void sort() {
        NavigationController controller = findController(mRightTag);
        int index = mControllers.indexOf(controller);
        if(index != 1&&index !=-1) {
            mControllers.add(1, mControllers.remove(index));
        }
    }
    void setUp(BaseSplitRouter.SplitCondition condition, int screenWidthDp, int screenHeightDp) {
        if(!mAlreadyConfig || !mConfigOnce) {
            mConfigOnce = condition.configOnce;

                splitWhenWiderThan = condition.splitWhenWiderThan;
                splitWhenTallerThan = condition.splitWhenTallerThan;
                leftWide = condition.leftWide;
                rightWide = condition.rightWide;

            mAlreadyConfig = true;
            mRightRouterHasIntro = condition.introStartupInRightRouter;
        }
        mInSplitScreen = shouldSplitScreen(screenWidthDp, screenHeightDp);
    }

    public boolean isInSplitMode() {
        return mInSplitScreen;
    }

    private boolean shouldSplitScreen(int screenWidthDp, int screenHeightDp) {
        if(splitWhenWiderThan != -1 && splitWhenTallerThan != -1) {
            return  screenWidthDp >= splitWhenWiderThan && screenHeightDp >= splitWhenTallerThan;
        } else if(splitWhenWiderThan != -1) {
            return screenWidthDp >= splitWhenWiderThan;
        } else if(splitWhenTallerThan != -1) {
            return screenHeightDp >= splitWhenTallerThan;
        } else return !mInSplitScreen;
    }

    void popAt(int position) {
        if(position < count()) mControllers.remove(position);
    }

    public Class<? extends NavigationFragment> getIntroFragmentClass() {
        return mStartUpFragmentClass;
    }

    Class<? extends NavigationFragment> mStartUpFragmentClass = null;
    public void setDefaultIntroFragmentClass(Class<? extends NavigationFragment> startUpFragmentClass) {
        mStartUpFragmentClass = startUpFragmentClass;
    }
}
