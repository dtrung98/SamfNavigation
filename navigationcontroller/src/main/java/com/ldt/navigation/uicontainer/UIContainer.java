package com.ldt.navigation.uicontainer;

import android.view.View;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.collection.SimpleArrayMap;

import com.ldt.navigation.NavigationController;

public interface UIContainer {
 static final SimpleArrayMap<String, Class<?>> sClassMap =
         new SimpleArrayMap<>();

 static UIContainer instantiate(Context context, String name) {
  try {
   Class<?> clazz = sClassMap.get(name);
/*   if (clazz == null) {
    // Class not found in the cache, see if it's real, and try to add it
    clazz = context.getClassLoader().loadClass(name);
    sClassMap.put(name, clazz);
   }*/
   if(clazz!=null)
    return  (UIContainer) clazz.newInstance();

  } catch (Exception ignored) {
  }
  return null;
 }

 static void save(String name, Class<?> clazz) {
  if(sClassMap.get(name)==null) sClassMap.put(name, clazz);
 }

 default void provideConfig(int wQualifier, int hQualifier, float dpUnit) {}
View provideLayout(Context context, LayoutInflater inflater, ViewGroup viewGroup, int subContainerId);
default void bindLayout(View view) {}
default void attach(NavigationController controller) {}
default void detach() {}
default void saveState() {}
default void restoreState() {}
}