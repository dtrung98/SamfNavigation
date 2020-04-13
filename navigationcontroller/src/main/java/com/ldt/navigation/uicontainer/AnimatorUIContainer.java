package com.ldt.navigation.uicontainer;

import android.animation.Animator;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class AnimatorUIContainer implements UIContainer {
    private View mRootView;
    private View mSubContainerView;

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
        return mRootView;
    }

    @Override
    public void executeAnimator(Animator animator, int transit, boolean enter, int nextAnim) {
        if(animator != null) {
            animator.setTarget(mSubContainerView);
            animator.start();
        }
    }
}
