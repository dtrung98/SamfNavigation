package com.ldt.navigation.uicontainer;

import android.animation.Animator;
import android.os.Bundle;
import android.view.View;
import android.content.Context;

import com.ldt.navigation.NavigationController;
import com.ldt.navigation.NavigationFragment;
import com.ldt.navigation.PresentStyle;
import com.ldt.navigation.R;

import android.view.LayoutInflater;
import android.view.ViewGroup;

public class ExpandContainer extends AnimatorUIContainer {
  public View provideLayout(Context context, LayoutInflater inflater, ViewGroup viewGroup, int providedSubContainerId) {
    // provide container layout
    // provide sub container layout
    View v = inflater.inflate(R.layout.expand_container, viewGroup, false);
    v.setId(providedSubContainerId);
    return v;
  }

  @Override
  public int defaultTransition() {
    return NavigationFragment.PRESENT_STYLE_DEFAULT;
  }
}