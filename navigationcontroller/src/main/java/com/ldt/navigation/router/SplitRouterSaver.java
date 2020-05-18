package com.ldt.navigation.router;

import com.ldt.navigation.NavigationController;
import com.ldt.navigation.R;

import java.util.HashMap;

public class SplitRouterSaver extends RouterSaver {
    /**
     * The stack stores fragment tag stack in master controller
     */
    public final HashMap<String, Boolean> mFragmentTypesForCompactMode = new HashMap<>();
    public void putFragmentType(String tag,  boolean isPushInDetail) {
        mFragmentTypesForCompactMode.put(tag, isPushInDetail);
    }

    public boolean isDetailFragment(String tag) {
        Boolean value = mFragmentTypesForCompactMode.get(tag);
        return value != null ? value : false;
    }

    private final String mMasterControllerTag;
    private final String mDetailControllerTag;

    public int getMasterContainerViewId() {
        return mMasterContainerViewId;
    }

    public int getDetailContainerViewId() {
        return mDetailContainerViewId;
    }

    public String getMasterControllerTag() {
        return mMasterControllerTag;
    }

    public String getDetailControllerTag() {
        return mDetailControllerTag;
    }

    public int getFloatingContainerViewId() {
        return mFloatingContainerViewId;
    }

    final int mMasterContainerViewId;
    final int mDetailContainerViewId;

    public SplitRouterSaver(String masterControllerTag, String detailControllerTag, int masterContainerViewId, int detailContainerViewId, int floatingContainerViewId) {
        mMasterControllerTag = masterControllerTag;
        mDetailControllerTag = detailControllerTag;
        mMasterContainerViewId = masterContainerViewId;
        mDetailContainerViewId = detailContainerViewId;
        mFloatingContainerViewId = floatingContainerViewId;
    }

    final int mFloatingContainerViewId;

    public SplitRouterSaver() {
        mMasterControllerTag = "master-controller-tag";
        mDetailControllerTag = "detail-controller-tag";
        mMasterContainerViewId = R.id.left_container;
        mDetailContainerViewId = R.id.right_container;
        mFloatingContainerViewId = R.id.floating_container;
    }

    public SplitRouterSaver(String leftRouterTag, String rightRouterTag) {
        mMasterControllerTag = leftRouterTag;
        mDetailControllerTag = rightRouterTag;
        mMasterContainerViewId = R.id.left_container;
        mDetailContainerViewId = R.id.right_container;
        mFloatingContainerViewId = R.id.floating_container;
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
        NavigationController controller = findController(mDetailControllerTag);
        int index = mControllers.indexOf(controller);
        if(index != 1&&index !=-1) {
            mControllers.add(1, mControllers.remove(index));
        }
    }
    void setUp(BaseSplitRouterObsolete.SplitCondition condition, int screenWidthDp, int screenHeightDp) {
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
}
