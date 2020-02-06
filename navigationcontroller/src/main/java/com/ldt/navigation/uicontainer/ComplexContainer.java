package com.ldt.navigation.uicontainer;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ldt.navigation.NavigationController;

public interface ComplexContainer extends UIContainer {
  UIContainer getSubContainer();

  @Override
  void provideConfig(int wq, int hq, float dpUnit);

  @Override
  default View provideLayout(Context context, LayoutInflater inflater, ViewGroup viewGroup, int subContainerId) {
  return getSubContainer().provideLayout(context, inflater, viewGroup, subContainerId);
  }

  @Override
  default void bindLayout(View view) {
    UIContainer subContainer = getSubContainer();
    if(subContainer!=null) subContainer.bindLayout(view);
  }

  @Override
  default void attach(NavigationController controller) {
    UIContainer subContainer = getSubContainer();
    if(subContainer!=null) subContainer.attach(controller);
  }

  @Override
  default void detach() {
    UIContainer subContainer = getSubContainer();
    if(subContainer!=null) subContainer.detach();
  }

  @Override
  default void saveState(Bundle bundle) {
    UIContainer subContainer = getSubContainer();
    if(subContainer!=null) subContainer.saveState(bundle);
  }

  @Override
  default void restoreState(Bundle bundle) {
    UIContainer subContainer = getSubContainer();
    if(subContainer!=null) subContainer.saveState(bundle);
  }
}