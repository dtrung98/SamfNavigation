package com.ldt.navigation.uicontainer;

import android.view.View;
import android.content.Context;
import com.ldt.navigation.NavigationController;
import com.ldt.navigation.NavigationFragment;
import com.ldt.navigation.R;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

public class ScalableDialogContainer implements UIContainer, View.OnClickListener {
  private int w;
  private int h;
  private float dpUnit;
  @Override
  public void provideConfig(int wQualifier, int hQualifier, float dpUnit) {
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

  private NavigationController mController;

  @Override
  public void attach(NavigationController controller) {
    mController = controller;
  }

  @Override
  public void detach() {
    mController = null;
  }

  @Override
  public void onClick(View v) {
    if(mController!=null) mController.quit();
  }
}