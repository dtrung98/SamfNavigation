package com.ldt.navigation.uicontainer;

import android.animation.Animator;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.ldt.navigation.NavigationController;
import com.ldt.navigation.PresentStyle;
import com.ldt.navigation.R;

public class StandardBottomSheetContainer extends AnimatorUIContainer{
  private FrameLayout mBottomSheet;
  private BottomSheetBehavior<FrameLayout> mBehavior;
  private int mTopMargin = 0;
  public View provideLayout(Context context, LayoutInflater inflater, ViewGroup viewGroup, int subContainerId) {
    View v = inflater.inflate(R.layout.standard_bottom_sheet_container, viewGroup, false);
    v.findViewById(R.id.sub_container).setId(subContainerId);
    return v;
  }

  @Override
  public int defaultTransition() {
    return PresentStyle.SLIDE_UP;
  }

  @Override
  public int[] onWindowInsetsChanged(Fragment controller, int left, int top, int right, int bottom) {
    mTopMargin = (int) mBottomSheet.getContext().getResources().getDimension(R.dimen.dpUnit)*4 + top;
    View subView = getSubContainerView();
    subView.setTranslationY(mTopMargin);
    subView.setPadding(subView.getPaddingLeft(), subView.getPaddingTop(), subView.getPaddingRight(), subView.getPaddingBottom());
    return new int[] {left, 0, right, bottom};
  }

  @Override
  public void start(Fragment controller) {
    super.start(controller);
    if (mBehavior != null && mBehavior.getState() == BottomSheetBehavior.STATE_HIDDEN) {
      mBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }
  }

  @Override
  public void bindLayout(View view) {
    view.findViewById(R.id.root).setOnClickListener(v -> quit());
    mBottomSheet = view.findViewById(R.id.design_bottom_sheet);
    mBehavior = BottomSheetBehavior.from(mBottomSheet);
    mBehavior.addBottomSheetCallback(mBottomSheetCallback);
    mBehavior.setHideable(true);
    mBehavior.setSkipCollapsed(true);
    mBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
  }

  private BottomSheetBehavior.BottomSheetCallback mBottomSheetCallback =
          new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(
                    @NonNull View bottomSheet, @BottomSheetBehavior.State int newState) {
              if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                quit();
              }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {}
          };

  @Override
  public void executeAnimator(Animator animator, int transit, boolean enter, int nextAnim) {
    if(animator != null) {
      animator.setTarget(mBottomSheet);
      animator.start();
    }
   executeDimAnimator(animator, transit, enter, nextAnim);
  }

  private Fragment mController;

  @Override
  public void provideQualifier(Fragment controller, int wQualifier, int hQualifier, float dpUnit) {
    mController = controller;
  }

  @Override
  public void destroy(Fragment controller) {
    mController = null;
  }

  private void quit() {
    if(mController instanceof NavigationController) ((NavigationController)mController).quit();
  }
}