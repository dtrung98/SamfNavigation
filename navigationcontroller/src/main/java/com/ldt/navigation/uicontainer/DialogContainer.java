package com.ldt.navigation.uicontainer;

import android.view.View;
import android.content.Context;
import com.ldt.navigation.NavigationController;
import com.ldt.navigation.R;

import android.view.LayoutInflater;
import android.view.ViewGroup;

public class DialogContainer implements UIContainer {
public View provideLayout(Context context, LayoutInflater inflater, ViewGroup viewGroup, int subContainerId) {
  View v = inflater.inflate(R.layout.dialog_container, viewGroup, false);
  v.findViewById(R.id.sub_container).setId(subContainerId);
  return v;
}

}