package com.ldt.navigation.uicontainer;

import android.animation.Animator;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.interpolator.view.animation.FastOutSlowInInterpolator;

import com.ldt.navigation.PresentStyle;
import com.ldt.navigation.R;

public abstract class AnimatorUIContainer implements UIContainer {
    private View mRootView;
    private View mSubContainerView;
    private View mDimView;
    private PresentStyle mFade;
    public boolean shouldDim() {
        return true;
    }
    public View getDimView() {
        return mDimView;
    }

    public View getRootView() {
        return mRootView;
    }

    public View getSubContainerView() {
        return mSubContainerView;
    }

    @Override
    public View onCreateLayout(Context context, LayoutInflater inflater, ViewGroup viewGroup, int subContainerId) {
        mRootView = provideLayout(context, inflater, viewGroup, subContainerId);
        mSubContainerView = mRootView.findViewById(subContainerId);
        mDimView = mRootView.findViewById(R.id.dim_view);
        return mRootView;
    }

    public void executeDimAnimator(Animator containerAnimator, int transit, boolean enter, int nextAnim) {
        if(mDimView == null || !shouldDim()) return;
        if(mFade == null) mFade = PresentStyle.inflate(PresentStyle.FADE);
        long duration = (containerAnimator == null) ?  125 : containerAnimator.getDuration();
        Animator dimAnimator = PresentStyle.inflateAnimator(mDimView.getContext(), mFade, transit, enter);
        dimAnimator.setTarget(mDimView);
        dimAnimator.setDuration(duration);
        dimAnimator.start();
    }

    @Override
    public void executeAnimator(Animator animator, int transit, boolean enter, int nextAnim) {
        if(animator != null) {
            animator.setTarget(mSubContainerView);
            animator.setInterpolator(new FastOutSlowInInterpolator());
            animator.start();
        }
        // dim background if any dim view
        executeDimAnimator(animator, transit, enter, nextAnim);
    }
}
