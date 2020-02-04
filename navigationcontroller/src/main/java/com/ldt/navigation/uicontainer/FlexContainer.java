package com.ldt.navigation.uicontainer;

import android.view.View;
import android.content.Context;
import com.ldt.navigation.NavigationController;
import android.view.LayoutInflater;
import android.view.ViewGroup;

public class FlexContainer implements UIContainer {
  private UIContainer subContainer;
  
  @Override
  public void provideConfig(int wq, int hq, float dpUnit) {
    if(hq>=400) subContainer = new DialogContainer();
    else subContainer = new ExpandContainer();
  }

  @Override
  public View provideLayout(Context context, LayoutInflater inflater, ViewGroup viewGroup, int subContainerId) {
  return subContainer.provideLayout(context, inflater, viewGroup, subContainerId);
  }
}