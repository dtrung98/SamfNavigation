package com.ldt.navigation.uicontainer;

import android.animation.Animator;
import android.view.View;
import android.content.Context;

import com.ldt.navigation.NavigationController;
import com.ldt.navigation.PresentStyle;
import com.ldt.navigation.R;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.fragment.app.FragmentTransaction;

/**
 *  Container hiển thị giao diện dialog có w/h lớn hơn 3/4 và bé thua 4/3,
 *  dialog to ra theo kích cỡ màn hình
 */
public class ScalableDialogContainer extends AnimatorUIContainer implements View.OnClickListener {

  private int w;
  private int h;
  private float dpUnit;
  @Override
  public void provideQualifier(NavigationController controller, int wQualifier, int hQualifier, float dpUnit) {
    mController = controller;
    w = wQualifier;
    h = hQualifier;
    this.dpUnit = dpUnit;
  }

  private float suitableSize(int qualifier) {
    if(qualifier<=400) return qualifier - 32;
    else if(qualifier<=600) return qualifier - 48;
    else if(qualifier<=800) return qualifier - 128;
    else return qualifier*3f/4;
  }

  public View provideLayout(Context context, LayoutInflater inflater, ViewGroup viewGroup, int subContainerId) {
  View v = inflater.inflate(R.layout.dialog_container, viewGroup, false);
  View dialog = v.findViewById(R.id.sub_container);
  dialog.setId(subContainerId);

  float ratio = ((float) w)/h;
  // 3/4 <= ratio <= 4/3
    float newW, newH;
  if(ratio < 3f/4) {

    newW = suitableSize(w);
    newH = newW*4f/3;

  } else if (ratio > 4f/3){
    newH = suitableSize(h);
    newW = newH*4f/3;
  } else {
    newW  = suitableSize(w);
    newH = suitableSize(h);
  }

    ViewGroup.LayoutParams params = dialog.getLayoutParams();
    params.width = (int)(newW*dpUnit);
    params.height = (int)(newH*dpUnit);
  return v;
}

  @Override
  public void bindLayout(View view) {
  view.findViewById(R.id.root).setOnClickListener(this);
  }

  @Override
  public int defaultTransition() {
    return PresentStyle.ROTATE_DOWN_LEFT;
  }

  @Override
  public void start(NavigationController controller) {
    //mSubContainerView.setTranslationY(h);
    //mSubContainerView.animate().translationY(h/2).setInterpolator(new AccelerateDecelerateInterpolator()).start();
  }

  private NavigationController mController;

  @Override
  public void destroy(NavigationController controller) {
    mController = null;
  }

  @Override
  public void onClick(View v) {
    if(mController!=null) mController.quit();
  }

}