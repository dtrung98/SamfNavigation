package com.ldt.navigation;

import androidx.fragment.app.Fragment;

import java.lang.ref.WeakReference;

/**
 * This class handles every cases in navigating a specific fragment
 */
public class FragmentNavigator {
    public static final int PRESENT_STYLE_DEFAULT = PresentStyle.SLIDE_LEFT;
    public static final int DEFAULT_ANIMATION_DURATION = 275;

    public final String mFragmentTag;

    FragmentNavigator(String fragmentTag) {
        mFragmentTag = fragmentTag;
    }

    public Fragment getFragment() {
        NavigationController controller = getNavigationController();
        if (controller != null) {
            return controller.findFragment(mFragmentTag);
        }
        return null;
    }

    private WeakReference<NavigationController> mWeakRefController;

    public NavigationController getNavigationController() {
        if (mWeakRefController != null) {
            return mWeakRefController.get()
        }
        return null;
    }

    protected void setNavigationController(NavigationController controller) {
        mWeakRefController = new WeakReference<>(controller);
    }

    public void setAnimationEnabled(boolean animationEnabled) {
        mAnimationEnabled = animationEnabled;
    }

    private boolean mAnimationEnabled = true;

    public boolean isAnimationEnabled() {
        return mAnimationEnabled;
    }

    private int mOpenEnterAnimation = PRESENT_STYLE_DEFAULT;

    public boolean navigateBack() {
        NavigationController controller = getNavigationController();
        return controller != null && controller.navigateBack();
    }

    public void navigateTo(Fragment fragment) {
        NavigationController controller = getNavigationController();
        if(controller != null) {
            controller.navigateTo();
        }
    }


}
