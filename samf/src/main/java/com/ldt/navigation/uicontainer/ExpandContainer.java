package com.ldt.navigation.uicontainer;

import android.animation.Animator;
import android.view.View;
import android.content.Context;

import com.ldt.navigation.NavigationControllerFragment;
import com.ldt.navigation.NavigationFragment;
import com.ldt.navigation.R;
import com.ldt.navigation.container.SplitContainerNavigator;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;

import java.util.Objects;

public class ExpandContainer extends AnimatorUIContainer {
    public View provideLayout(Context context, LayoutInflater inflater, ViewGroup viewGroup, int providedSubContainerId) {
        // provide container layout
        // provide sub container layout
        View v = inflater.inflate(R.layout.animator_expand_container, viewGroup, false);
        v.findViewById(R.id.sub_container).setId(providedSubContainerId);
        return v;
    }

    @Override
    public int[] onWindowInsetsChanged(Fragment controller, int left, int top, int right, int bottom) {
        if (controller instanceof NavigationControllerFragment) {
            NavigationControllerFragment navigationController = (NavigationControllerFragment) controller;
            if (navigationController.getParentNavigator() instanceof SplitContainerNavigator) {
                SplitContainerNavigator containerNavigator = (SplitContainerNavigator) navigationController.getParentNavigator();
                if (((NavigationControllerFragment) controller).getIdentifyTag().equals(containerNavigator.requireSplitRouterAttribute().getMasterControllerTag()) && containerNavigator.requireSplitRouterAttribute().isInSplitMode()) {
                    return new int[]{left, top, 0, bottom};
                } else if (containerNavigator.requireSplitRouterAttribute().getDetailControllerTag().equals(((NavigationControllerFragment) controller).getIdentifyTag()) && containerNavigator.requireSplitRouterAttribute().isInSplitMode()) {
                    return new int[]{0, top, right, bottom};
                }
            }
        }
        return null;

    }

    @Override
    public int defaultDuration() {
        return 425;
    }

    @Override
    public int defaultTransition() {
        return NavigationFragment.PRESENT_STYLE_DEFAULT;
    }
}