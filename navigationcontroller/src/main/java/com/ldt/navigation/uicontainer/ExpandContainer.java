package com.ldt.navigation.uicontainer;

import android.view.View;
import android.content.Context;
import com.ldt.navigation.NavigationController;
import android.view.LayoutInflater;
import android.view.ViewGroup;

public class ExpandContainer implements UIContainer {
public View provideLayout(Context context, LayoutInflater inflater, ViewGroup viewGroup, int subContainerId) {
  View v = inflater.inflate(R.layout.expand_container, viewGroup, false);
  v.findViewById(R.id.root).setId(subContainerId);
  return v;
}

}