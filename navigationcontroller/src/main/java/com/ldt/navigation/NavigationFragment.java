package com.ldt.navigation;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.TimeInterpolator;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.ldt.navigation.effectview.EffectFrameLayout;
import com.ldt.navigation.effectview.EffectView;
import com.ldt.navigation.router.Router;

import java.lang.ref.WeakReference;

/**
 * Created by burt on 2016. 5. 24..
 * Updated by dtrung98 on 2019. 12
 */
public abstract class NavigationFragment extends Fragment implements OnWindowInsetsChangedListener {
    private static final String TAG ="NavigationFragment";
    public static final int DEFAULT_DURATION = 275;
    public static final String ANIMATABLE = "animatable";
    public static final int PRESENT_STYLE_DEFAULT = PresentStyle.SLIDE_LEFT;
    public static final String SELF_OPEN_EXIT_PRESENT_STYLE_TYPE = "self_open_exit_present_style_type";
    public static final int SELF_OPEN_EXIT_TYPE_NO_SET = -3;

    private WeakReference<NavigationController> weakNavigationController = null;
    protected boolean mAnimatable = true;
    protected boolean mIsOnConfigurationAnimation = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null) {
            mAnimatable = savedInstanceState.getBoolean(ANIMATABLE, mAnimatable);
            int savedOpenExitType = savedInstanceState.getInt(SELF_OPEN_EXIT_PRESENT_STYLE_TYPE, SELF_OPEN_EXIT_TYPE_NO_SET);
            if(savedOpenExitType != SELF_OPEN_EXIT_TYPE_NO_SET) mOpenExitPresentStyle = PresentStyle.inflate(savedOpenExitType);
        }

        mIsOnConfigurationAnimation = savedInstanceState != null;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(ANIMATABLE, mAnimatable);
        int openExitType = mOpenExitPresentStyle ==null ? SELF_OPEN_EXIT_TYPE_NO_SET : mOpenExitPresentStyle.getType();
        outState.putInt(SELF_OPEN_EXIT_PRESENT_STYLE_TYPE, openExitType);
    }

    private PresentStyle mOpenEnterPresentStyle = null;
    private PresentStyle mOpenExitPresentStyle = null;

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
        mIsOnConfigurationAnimation = false;
    }

 /*  protected voild setPresentStyle(PresentStyle presentStyle) {
        this.presentStyle = presentStyle;
    }*/

    public int defaultTransition() {
        return PRESENT_STYLE_DEFAULT;
    }

    void setSelfOpenExitPresentStyle(PresentStyle newStyle) {
        if(mOpenExitPresentStyle == null || (newStyle != null && mOpenExitPresentStyle.getType() == newStyle.getType()))
        this.mOpenExitPresentStyle = PresentStyle.inflate(newStyle.getType());
    }

    /**
     *  Cài đặt hiệu ứng chuyển cảnh cho Fragment nằm phía sau
     * @return  0 : không dùng hiệu ứng
     * <br>  -1 :  hiệu ứng đồng nhất
     * <br> -2 : dùng hiệu ứng biến mất của Fragment nằm phía sau đó
     */
    public int defaultOpenExitTransition() {
        return PresentStyle.SAME_AS_OPEN;
    }

    public PresentStyle getOpenEnterPresentStyle() {
        if(mOpenEnterPresentStyle ==null) mOpenEnterPresentStyle = PresentStyle.inflate(defaultTransition());
        return mOpenEnterPresentStyle;
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

    private boolean isRootLayoutWrapped = false;
    public boolean shouldForceWrapLayout() {
        return false;
    }

    @Nullable
    @Override
    final public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root;
        View v = onCreateContentView(inflater, container, savedInstanceState);
        if(v == null) return null;

        // nếu root không phải effectview
        // hoặc nếu yêu cầu ép buộc wrap layout
        if( !(v instanceof EffectView) || shouldForceWrapLayout()) {
            root = new EffectFrameLayout(inflater.getContext());
            root.setLayoutParams(new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            ((ViewGroup)root).addView(v);
        } else root = v;
        return root;
    }

    private boolean isAttachActivity = false;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        isAttachActivity = true;
        int[] insets = NavigationController.getWindowInsets();
        onWindowInsetsChanged(insets[0], insets[1], insets[2], insets[3]);
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

    @Nullable
    abstract protected View onCreateContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    @Override
    public Animator onCreateAnimator(final int transit, final boolean enter, int nextAnim) {
        /* Fragment trong quá trình restore lại, hãy vô hiệu hóa đi */
       if(mIsOnConfigurationAnimation) {
            mIsOnConfigurationAnimation = false;
            return null;
        }

        /* Fragment bị tắt hiệu ứng */
        if(!mAnimatable) {
            mAnimatable = true;
            return null;
        }

        NavigationController nav =  getNavigationController();
        /* Không được gắn Controller và bản thân không phải Controller */
        if(nav == null && !(this instanceof NavigationController)) {
            return null; //no animatable
        }

        /* Open Enter Transition Style */
        PresentStyle openEnterPresentStyle = getOpenEnterPresentStyle();
        if(openEnterPresentStyle.getType() == PresentStyle.NONE) return null;

        Animator animator = null;
        if(transit == FragmentTransaction.TRANSIT_FRAGMENT_OPEN) {

            /* Open Enter */
            if (enter) {
                int id = openEnterPresentStyle.getOpenEnterAnimatorId();
                if(id != -1) animator = AnimatorInflater.loadAnimator(getContext(), id);
            } else { /* Open Exit */
                int id;
                if(mOpenExitPresentStyle ==null)
                    id = openEnterPresentStyle.getOpenExitAnimatorId();
                else {
                    id = mOpenExitPresentStyle.getOpenExitAnimatorId();
                }
                if(id != -1) animator = AnimatorInflater.loadAnimator(getContext(), id);
            }

        } else {

            if (enter) { /* Close Enter */
                int id;
                if(mOpenExitPresentStyle == null)
                id = openEnterPresentStyle.getCloseEnterAnimatorId();
                else {
                    id = mOpenExitPresentStyle.getCloseEnterAnimatorId();
                    mOpenExitPresentStyle = null;
                }

                if(id != -1) animator = AnimatorInflater.loadAnimator(getContext(), id);
            } else { /* Close Exit */
                int id;
                id = openEnterPresentStyle.getCloseExitAnimatorId();
                if(id != -1) animator = AnimatorInflater.loadAnimator(getContext(), id);
            }
        }

        if(animator != null) {
            animator.setInterpolator(getInterpolator());
            animator.setDuration(defaultDuration());
        }
        return animator;
    }

    @Override
    public void onWindowInsetsChanged(int left, int top, int right, int bottom) {
    }

    public boolean attachedToActivity() {
        return isAttachActivity;
    }
}
