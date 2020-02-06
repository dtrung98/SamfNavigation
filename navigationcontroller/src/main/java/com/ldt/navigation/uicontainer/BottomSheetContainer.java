package com.ldt.navigation.uicontainer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ldt.navigation.NavigationController;
import com.ldt.navigation.R;

public class BottomSheetContainer implements UIContainer, View.OnClickListener {
  @Override
  public void provideConfig(int wQualifier, int hQualifier, float dpUnit) {

  }

  public View provideLayout(Context context, LayoutInflater inflater, ViewGroup viewGroup, int subContainerId) {
  View v = inflater.inflate(R.layout.bottom_sheet_container, viewGroup, false);
  v.findViewById(R.id.sub_container).setId(subContainerId);
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