package com.ldt.navigation.uicontainer;

import com.ldt.navigation.PresentStyle;

public class ExpandStaticContainer extends ExpandContainer {
  @Override
  public int defaultTransition() {
    return PresentStyle.NONE;
  }

  @Override
  public boolean shouldDim() {
    return false;
  }
}