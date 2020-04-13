package com.ldt.navigation.uicontainer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ldt.navigation.NavigationFragment;
import com.ldt.navigation.PresentStyle;
import com.ldt.navigation.R;

public class ExpandStaticContainer extends ExpandContainer {
  @Override
  public int defaultTransition() {
    return PresentStyle.NONE;
  }
}