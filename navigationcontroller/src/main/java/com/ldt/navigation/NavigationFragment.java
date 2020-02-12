package com.ldt.navigation;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.TimeInterpolator;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.ldt.navigation.router.Router;

import java.lang.ref.WeakReference;

/**
 * Created by burt on 2016. 5. 24..
 * Updated by dtrung98 on 2019. 12
 */
public abstract class NavigationFragment extends Fragment {
    private static final String TAG ="NavigationFragment";
    public static final int DEFAULT_DURATION = 275;
    public static final String ANIMATABLE = "animatable";
    public static final int PRESENT_STYLE_DEFAULT = PresentStyle.SLIDE_LEFT;

    private WeakReference<NavigationController> weakNavigationController = null;
    protected boolean mAnimatable = true;
    protected boolean mIsOnConfigurationAnimation = false;

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(ANIMATABLE, mAnimatable);
    }

    private FrameLayout mWrapperRootLayout = null;
    private PresentStyle presentStyle = null;
    private PresentStyle exitPresentStyle = null;
    protected View mContentView = null;

    public boolean navigateBack() {
        NavigationController controller = getNavigationController();
        return controller != null &&
        controller.navigateBack();
    }

    public void navigateTo(NavigationFragment fragment) {
        NavigationController controller = getNavigationController();
        if(controller != null)
            controller.navigateTo(fragment);
    }

    public void onSetStatusBarMargin(int value) {}

    public NavigationController getNavigationController() {
        if(weakNavigationController == null)
            return null;
        return weakNavigationController.get();
    }

    protected void setNavigationController(NavigationController navigationController) {
        weakNavigationController = new WeakReference<>(navigationController);
    }

    protected void setAnimatable(boolean animatable) {
        this.mAnimatable = animatable;
    }

 /*  protected voild setPresentStyle(PresentStyle presentStyle) {
        this.presentStyle = presentStyle;
    }*/

    public int defaultTransition() {
        return PRESENT_STYLE_DEFAULT;
    }

    public void setOpenExitPresentStyle(PresentStyle exitPresentStyle) {
        this.exitPresentStyle = exitPresentStyle;
    }

    /**
     *  Cài đặt hiệu ứng cho Fragment cũ
     * @return  0 : không dùng hiệu ứng
     * <br>  -1 : dùng cùng loại hiệu ứng với Fragment mới
     * <br> -2 : dùng hiệu ứng biến mất của Fragment cũ
     */
    public int defaultOpenExitTransition() {
        return PresentStyle.SAME_AS_OPEN;
    }

    public PresentStyle getPresentStyle() {
        if(presentStyle==null) presentStyle = PresentStyle.get(defaultTransition());
        return presentStyle;
    }

    public int defaultDuration() {
        return DEFAULT_DURATION;
    }

    /**
     *  This method is called when user press the back button
     * @return true to allow the fragment to be navigateBacked
     * <br>      false to ignore the navigateBacked.
     * <br> To navigateBack the fragment, you need to call method <i>getNavigationController().navigateBack()</i> directly
     */
    public boolean onNavigateBack(){
        return true;
    }

    public boolean requestBack() {
        NavigationController controller = getNavigationController();
        return controller != null && controller.onNavigateBack();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null) {
            mAnimatable = savedInstanceState.getBoolean(ANIMATABLE, mAnimatable);
        }

        mIsOnConfigurationAnimation = savedInstanceState != null;
    }

    @Nullable
    @Override
    final public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = onCreateContentView(inflater, container, savedInstanceState);
        if(v == null) return null;

        if(mWrapperRootLayout ==null)
        mWrapperRootLayout = new EffectFrameLayout(inflater.getContext());
        else
        mWrapperRootLayout.removeAllViews();

        mContentView = v;
        mWrapperRootLayout.addView(v);
        return mWrapperRootLayout;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        onSetStatusBarMargin(getStatusHeight(getResources()));
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        sStatusHeight = -1;
    }

    public Router getRouter() {
        NavigationController controller = getNavigationController();
        if(controller == null) return null;
            return controller.getRouter();
    }

    public TimeInterpolator getInterpolator() {
        NavigationController controller = getNavigationController();
        if(controller != null) return controller.getInterpolator();
        return new AccelerateDecelerateInterpolator();
    }

    private static int sStatusHeight = -1;
    protected static int getStatusHeight(Resources myR)
    {
        if(sStatusHeight !=-1) return sStatusHeight;
        int height;
        int idSbHeight = myR.getIdentifier("status_bar_height", "dimen", "android");
        if (idSbHeight > 0) {
            height = myR.getDimensionPixelOffset(idSbHeight);
            //   Toast.makeText(this, "Status Bar Height = "+ height, Toast.LENGTH_SHORT).show();
        } else {
            height = 0;
            //        Toast.makeText(this,"Resources NOT found",Toast.LENGTH_LONG).show();
        }
        sStatusHeight =height;
        return sStatusHeight;
    }

    @Nullable
    abstract protected View onCreateContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    /**
     * This is the layout for wrapping contentView
     * @return AndroidFragmentFrameLayout
     */
    public FrameLayout getRootLayout() {
        return mWrapperRootLayout;
    }

    /**
     * This is the layout-view which is defined by user.
     * @return content view
     */
    public View getContentView() {
        return mContentView;
    }

    @Override
    public Animator onCreateAnimator(final int transit, final boolean enter, int nextAnim) {
        if(mIsOnConfigurationAnimation) {
            mIsOnConfigurationAnimation = false;
            return null;
        }

        if(!mAnimatable) {
            mAnimatable = true;
            return null;
        }

        NavigationController nav =  getNavigationController();
        if(nav == null && !(this instanceof NavigationController)) {
            return null; //no animatable
        }

        PresentStyle ps = getPresentStyle();
        if(ps.getType() == PresentStyle.NONE) return null;

        Animator animator = null;
        if(transit == FragmentTransaction.TRANSIT_FRAGMENT_OPEN) {

            if (enter) {
                int id = ps.getOpenEnterAnimatorId();
                if(id != -1) animator = AnimatorInflater.loadAnimator(getActivity(), id);
            } else {
                int id;
                if(exitPresentStyle==null)
                    id = ps.getOpenExitAnimatorId();
                else {
                    id = exitPresentStyle.getOpenExitAnimatorId();
                }
                if(id != -1) animator = AnimatorInflater.loadAnimator(getActivity(), id);
            }

        } else {

            if (enter) {
                int id;
                if(exitPresentStyle == null)
                id = ps.getCloseEnterAnimatorId();
                else {
                    id = exitPresentStyle.getCloseEnterAnimatorId();
                    exitPresentStyle = null;
                }

                if(id != -1) animator = AnimatorInflater.loadAnimator(getActivity(), id);
            } else {
                int id;
                id = ps.getCloseExitAnimatorId();
                if(id != -1) animator = AnimatorInflater.loadAnimator(getActivity(), id);
            }
        }
        if(animator != null) {
            animator.setInterpolator(getInterpolator());
            animator.setDuration(defaultDuration());
        }

        if(animator!=null)
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

                if(transit == FragmentTransaction.TRANSIT_FRAGMENT_OPEN) {

                    if(enter) {
                        onShowFragment();
                    } else {
                        //onHideFragment();
                    }
                } else {

                    if(enter) {
                        onShowFragment();
                    } else {
                        onHideFragment();
                    }
                }


            }

            @Override
            public void onAnimationEnd(Animator animator) {
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        return animator;
    }


    public void onShowFragment() {}
    public void onHideFragment() {}

}
