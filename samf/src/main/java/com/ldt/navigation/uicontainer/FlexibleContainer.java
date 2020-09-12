package com.ldt.navigation.uicontainer;

import android.animation.Animator;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public abstract class FlexibleContainer implements UIContainer {

  private UIContainer mSubContainer;

  public UIContainer getSubContainer() {
    return mSubContainer;
  }

  @NonNull
  protected abstract UIContainer createSubContainer(Fragment controller, int wQualifier, int hQualifier, float dpUnit);

  @Override
  public void provideQualifier(Fragment controller, int wQualifier, int hQualifier, float dpUnit) {
    mSubContainer = createSubContainer(controller, wQualifier, hQualifier, dpUnit);
    mSubContainer.provideQualifier(controller, wQualifier, hQualifier, dpUnit);
  }

  @Override
  public View onCreateLayout(Context context, LayoutInflater inflater, ViewGroup viewGroup, int subContainerId) {
    return getSubContainer().onCreateLayout(context, inflater, viewGroup, subContainerId);
  }

  @Override
  public View provideLayout(Context context, LayoutInflater inflater, ViewGroup viewGroup, int subContainerId) {
  return getSubContainer().provideLayout(context, inflater, viewGroup, subContainerId);
  }

  @Override
  public void bindLayout(View view) {
    UIContainer subContainer = getSubContainer();
    if(subContainer!=null) subContainer.bindLayout(view);
  }

  @Override
  public void created(Fragment controller, Bundle bundle) {
    UIContainer subContainer = getSubContainer();
    if(subContainer!=null) subContainer.created(controller, bundle);
  }

  @Override
  public void destroy(Fragment controller) {
    UIContainer subContainer = getSubContainer();
    if(subContainer!=null) subContainer.destroy(controller);
  }

  @Override
  public void saveState(Fragment controller, Bundle bundle) {
    UIContainer subContainer = getSubContainer();
    if(subContainer!=null) subContainer.saveState(controller, bundle);
  }

  @Override
  public void restoreState(Fragment controller, Bundle bundle) {
    UIContainer subContainer = getSubContainer();
    if(subContainer!=null) subContainer.restoreState(controller, bundle);
  }

  @Override
  public void start(Fragment controller) {
    UIContainer subContainer = getSubContainer();
    if(subContainer!=null) subContainer.start(controller);
  }

  @Override
  public void stop(Fragment controller) {
    UIContainer subContainer = getSubContainer();
    if(subContainer!=null) subContainer.stop(controller);
  }

  @Override
  public void resume(Fragment controller) {
    UIContainer subContainer = getSubContainer();
    if(subContainer!=null) subContainer.resume(controller);
  }

  @Override
  public void pause(Fragment controller) {
    UIContainer subContainer = getSubContainer();
    if(subContainer!=null) subContainer.pause(controller);
  }

  @Override
  public void destroyView(Fragment controller) {
    UIContainer subContainer = getSubContainer();
    if(subContainer!=null) subContainer.destroyView(controller);
  }

  @Override
  public void stackChanged(Fragment controller) {
    UIContainer subContainer = getSubContainer();
    if(subContainer !=null) subContainer.stackChanged(controller);
  }

  @Override
  public void activityCreated(Fragment controller, Bundle savedInstanceState) {
    UIContainer subContainer = getSubContainer();
    if(subContainer!=null) subContainer.activityCreated(controller, savedInstanceState);
  }

  @Override
  public LayoutInflater provideLayoutInflater(Bundle savedInstanceState) {
    UIContainer subContainer = getSubContainer();
    if(subContainer != null) return subContainer.provideLayoutInflater(savedInstanceState);
    return null;
  }

  @Override
  public void executeAnimator(Animator animator, int transit, boolean enter, int nextAnim) {
    if(mSubContainer != null) mSubContainer.executeAnimator(animator, transit, enter, nextAnim);
  }

  @Override
  public boolean shouldAttachToContainerView() {
    UIContainer subContainer = getSubContainer();
    if(subContainer != null) return subContainer.shouldAttachToContainerView();
    return UIContainer.super.shouldAttachToContainerView();
  }

  @Override
  public Fragment getController() {
    UIContainer subContainer = getSubContainer();
    if(subContainer!=null) return subContainer.getController();
    return null;
  }

  @Override
  public int defaultDuration() {
    UIContainer subContainer = getSubContainer();
    if(subContainer!=null) return subContainer.defaultDuration();
    return UIContainer.super.defaultDuration();
  }

  @Override
  public int defaultTransition() {
    UIContainer subContainer = getSubContainer();
    if(subContainer!=null) return subContainer.defaultTransition();
    return UIContainer.super.defaultTransition();
  }

  @Override
  public int defaultOpenExitTransition() {
    UIContainer subContainer = getSubContainer();
    if(subContainer!=null) return subContainer.defaultOpenExitTransition();
    return UIContainer.super.defaultOpenExitTransition();
  }

  @Override
  public int[] onWindowInsetsChanged(Fragment controller, int left, int top, int right, int bottom) {
    if(mSubContainer != null) return mSubContainer.onWindowInsetsChanged(controller, left, top, right, bottom);
    return UIContainer.super.onWindowInsetsChanged(controller, left, top, right, bottom);
  }
}
