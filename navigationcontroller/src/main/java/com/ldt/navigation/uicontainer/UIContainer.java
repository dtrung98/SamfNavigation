package com.ldt.navigation.uicontainer;

import android.view.View;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import com.ldt.navigation.NavigationController

public interface UIContainer {
 default void provideConfig(int wQualifier, int hQualifier, float dpUnit) {}
View provideLayout(Context context, int containerId, LayoutInflater inflater, ViewGroup viewGroup, int subContainerId);
default void bindLayout(View view) {}
default void attach(NavigationController controller) {}
default void detach() {}
default void saveState() {}
default void restoreState() {}
}