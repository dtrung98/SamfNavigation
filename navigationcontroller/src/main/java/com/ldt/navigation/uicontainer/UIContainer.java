package com.ldt.navigation.uicontainer;

import android.animation.Animator;
import android.os.Bundle;
import android.view.View;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.collection.SimpleArrayMap;
import androidx.fragment.app.Fragment;

import com.ldt.navigation.PresentStyle;

import static com.ldt.navigation.NavigationFragment.DEFAULT_DURATION;

public interface UIContainer {
 SimpleArrayMap<String, Class<?>> sClassMap =
         new SimpleArrayMap<>();

 static UIContainer instantiate(Context context, String name) {
  try {
   Class<?> clazz = sClassMap.get(name);
   if (clazz == null) {
    // Class not found in the cache, see if it's real, and try to add it
    clazz = context.getClassLoader().loadClass(name);
    sClassMap.put(name, clazz);
   }
   if(clazz!=null)
    return  (UIContainer) clazz.newInstance();

  } catch (Exception ignored) {}
  return null;
 }

 static void save(String name, Class<?> clazz) {
  if(sClassMap.get(name)==null) sClassMap.put(name, clazz);
 }

 /**
  * Called in onCreate in Fragment
  * @param wQualifier
  * @param hQualifier
  * @param dpUnit
  */
 default void provideQualifier(Fragment controller, int wQualifier, int hQualifier, float dpUnit) {}

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
  *  Called by Navigation Controller
  */
 default View onCreateLayout(Context context, LayoutInflater inflater, ViewGroup viewGroup, int subContainerId) {
  return provideLayout(context, inflater, viewGroup, subContainerId);
 }
 /**
  * Equal to onViewCreated
  * @param view
  */
 default void bindLayout(View view) {}

 default Fragment getController() {return null;}

 /*
 Call in onCreate, after provideConfig
  */
 default void created(Fragment controller, Bundle bundle) {}

 /**
  * Call in onDestroyView
  */
 default void destroy(Fragment controller) {}
 default void saveState(Fragment controller, Bundle bundle) {}
 default void restoreState(Fragment controller, Bundle bundle) {}
 default void start(Fragment controller) {};
 default void stop(Fragment controller) {};
 default void resume(Fragment controller) {};
 default void pause(Fragment controller) {};
 default void destroyView(Fragment controller) {}
 default void stackChanged(Fragment controller) {}
 default void activityCreated(Fragment controller, Bundle savedInstanceState) {}
 default LayoutInflater provideLayoutInflater(Bundle savedInstanceState) { return null;}
 default void executeAnimator(Animator animator, int transit, boolean enter, int nextAnim) {}

 default boolean shouldAttachToContainerView() {
  return true;
 }

 default int defaultDuration() {
  return DEFAULT_DURATION;
 }

 default int defaultTransition() {
  return PresentStyle.FADE;
 }

 default int defaultOpenExitTransition() {
  return PresentStyle.SAME_AS_OPEN;
 }

 default int[] onWindowInsetsChanged(Fragment controller, int left, int top, int right, int bottom) {
  return null;
 }
}