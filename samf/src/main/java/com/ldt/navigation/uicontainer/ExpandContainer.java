package com.ldt.navigation.uicontainer;

import android.view.View;
import android.content.Context;

import com.ldt.navigation.NavigationFragment;
import com.ldt.navigation.R;

import android.view.LayoutInflater;
import android.view.ViewGroup;

public class ExpandContainer extends AnimatorUIContainer {
  public View provideLayout(Context context, LayoutInflater inflater, ViewGroup viewGroup, int providedSubContainerId) {
    // provide container layout
    // provide sub container layout
    View v = inflater.inflate(R.layout.animator_expand_container, viewGroup, false);
    v.findViewById(R.id.sub_container).setId(providedSubContainerId);
    return v;
  }

  @Override
  public int defaultDuration() {
    return 325;
  }

  @Override
  public int defaultTransition() {
    return NavigationFragment.PRESENT_STYLE_DEFAULT;
  }
}