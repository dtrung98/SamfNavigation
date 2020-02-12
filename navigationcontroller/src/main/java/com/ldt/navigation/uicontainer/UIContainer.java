package com.ldt.navigation.uicontainer;

import android.os.Bundle;
import android.view.View;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.collection.SimpleArrayMap;

import com.ldt.navigation.NavigationController;
import com.ldt.navigation.PresentStyle;

import static com.ldt.navigation.NavigationFragment.DEFAULT_DURATION;
import static com.ldt.navigation.NavigationFragment.PRESENT_STYLE_DEFAULT;

public interface UIContainer {
 SimpleArrayMap<String, Class<?>> sClassMap =
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

 /**
  * Called in onCreate in NavigationController
  * @param wQualifier
  * @param hQualifier
  * @param dpUnit
  */
 default void provideController(NavigationController controller, int wQualifier, int hQualifier, float dpUnit) {}

 /**
  * Equal to onCreateView
  * @param context
  * @param inflater
  * @param viewGroup
  * @param subContainerId
  * @return
  */
 View provideLayout(Context context, LayoutInflater inflater, ViewGroup viewGroup, int subContainerId);

 /**
  * Equal to onViewCreated
  * @param view
  */
 default void bindLayout(View view) {}

 default NavigationController getController() {return null;}

 /*
 Call in onCreate, after provideConfig
  */
 default void created(Bundle bundle) {}

 /**
  * Call in onDestroyView
  */
 default void destroy() {}
 default void saveState(Bundle bundle) {}
 default void restoreState(Bundle bundle) {}
 default void start() {};
 default void stop() {};
 default void resume() {};
 default void pause() {};
 default void destroyView() {}
 default void activityCreated(Bundle savedInstanceState) {}
 default LayoutInflater provideLayoutInflater(Bundle savedInstanceState) { return null;}

 default boolean shouldAttachToContainerView() {
  return true;
 }

 default int defaultDuration() {
  return DEFAULT_DURATION;
 }

 default int defaultTransition() {
  return PresentStyle.NONE;
 }

 default int defaultOpenExitTransition() {
  return PresentStyle.SAME_AS_OPEN;
 }

}