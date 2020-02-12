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
  default View provideLayout(Context context, LayoutInflater inflater, ViewGroup viewGroup, int subContainerId) {
  return getSubContainer().provideLayout(context, inflater, viewGroup, subContainerId);
  }

  @Override
  default void bindLayout(View view) {
    UIContainer subContainer = getSubContainer();
    if(subContainer!=null) subContainer.bindLayout(view);
  }

  @Override
  default void created(Bundle bundle) {
    UIContainer subContainer = getSubContainer();
    if(subContainer!=null) subContainer.created(bundle);
  }

  @Override
  default void destroy() {
    UIContainer subContainer = getSubContainer();
    if(subContainer!=null) subContainer.destroy();
  }

  @Override
  default void saveState(Bundle bundle) {
    UIContainer subContainer = getSubContainer();
    if(subContainer!=null) subContainer.saveState(bundle);
  }

  @Override
  default void restoreState(Bundle bundle) {
    UIContainer subContainer = getSubContainer();
    if(subContainer!=null) subContainer.restoreState(bundle);
  }

  @Override
  default void start() {
    UIContainer subContainer = getSubContainer();
    if(subContainer!=null) subContainer.start();
  }

  @Override
  default void stop() {
    UIContainer subContainer = getSubContainer();
    if(subContainer!=null) subContainer.stop();
  }

  @Override
  default void resume() {
    UIContainer subContainer = getSubContainer();
    if(subContainer!=null) subContainer.resume();
  }

  @Override
  default void pause() {
    UIContainer subContainer = getSubContainer();
    if(subContainer!=null) subContainer.pause();
  }

  @Override
  default void destroyView() {
    UIContainer subContainer = getSubContainer();
    if(subContainer!=null) subContainer.destroyView();
  }

  @Override
  default void activityCreated(Bundle savedInstanceState) {
    UIContainer subContainer = getSubContainer();
    if(subContainer!=null) subContainer.activityCreated(savedInstanceState);
  }

  @Override
  default LayoutInflater provideLayoutInflater(Bundle savedInstanceState) {
    UIContainer subContainer = getSubContainer();
    if(subContainer != null) return subContainer.provideLayoutInflater(savedInstanceState);
    return null;
  }

  @Override
  default boolean shouldAttachToContainerView() {
    UIContainer subContainer = getSubContainer();
    if(subContainer != null) return subContainer.shouldAttachToContainerView();
    return UIContainer.super.shouldAttachToContainerView();
  }

  @Override
  default void provideController(NavigationController controller, int wQualifier, int hQualifier, float dpUnit) {
    UIContainer subContainer = getSubContainer();
    if(subContainer!=null)
    subContainer.provideController(controller, wQualifier, hQualifier, dpUnit);
  }

  @Override
  default NavigationController getController() {
    UIContainer subContainer = getSubContainer();
    if(subContainer!=null) return subContainer.getController();
    return null;
  }

  @Override
  default int defaultDuration() {
    UIContainer subContainer = getSubContainer();
    if(subContainer!=null) return subContainer.defaultDuration();
    return UIContainer.super.defaultDuration();
  }

  @Override
  default int defaultTransition() {
    UIContainer subContainer = getSubContainer();
    if(subContainer!=null) return subContainer.defaultTransition();
    return UIContainer.super.defaultTransition();
  }

  @Override
  default int defaultOpenExitTransition() {
    UIContainer subContainer = getSubContainer();
    if(subContainer!=null) return subContainer.defaultOpenExitTransition();
    return UIContainer.super.defaultOpenExitTransition();
  }
}