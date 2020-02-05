package com.ldt.navigation.uicontainer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ldt.navigation.R;

public class BottomSheetContainer implements UIContainer {
public View provideLayout(Context context, LayoutInflater inflater, ViewGroup viewGroup, int subContainerId) {
  View v = inflater.inflate(R.layout.bottom_sheet_container, viewGroup, false);
  v.findViewById(R.id.sub_container).setId(subContainerId);
  return v;
}

}