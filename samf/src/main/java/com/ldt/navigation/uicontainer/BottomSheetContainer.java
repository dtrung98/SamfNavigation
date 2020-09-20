package com.ldt.navigation.uicontainer;

import android.animation.Animator;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.fragment.app.Fragment;
import com.ldt.navigation.NavigationControllerFragment;
import com.ldt.navigation.PresentStyle;
import com.ldt.navigation.R;

public class BottomSheetContainer extends AnimatorUIContainer implements View.OnClickListener {
  private View mPanel;
  private int mTopMargin = 0;
  public View provideLayout(Context context, LayoutInflater inflater, ViewGroup viewGroup, int subContainerId) {
    View v = inflater.inflate(R.layout.bottom_sheet_container, viewGroup, false);
    v.findViewById(R.id.sub_container).setId(subContainerId);
    return v;
  }

  @Override
  public int defaultTransition() {
    return PresentStyle.SLIDE_UP;
  }

  @Override
  public int[] onWindowInsetsChanged(Fragment controller, int left, int top, int right, int bottom) {
    mTopMargin = (int)mPanel.getContext().getResources().getDimension(R.dimen.dpUnit)*4 + top;
    View subView = getSubContainerView();
    subView.setTranslationY(mTopMargin);
    subView.setPadding(subView.getPaddingLeft(), subView.getPaddingTop(), subView.getPaddingRight(), subView.getPaddingBottom());
    return new int[] {left, 0, right, bottom};
  }

  @Override
  public void bindLayout(View view) {
    view.findViewById(R.id.root).setOnClickListener(this);
    mPanel = view.findViewById(R.id.panel);
  }

  @Override
  public void executeAnimator(Animator animator, int transit, boolean enter, int nextAnim) {
    if(animator != null) {
      animator.setTarget(mPanel);
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

  @Override
  public void onClick(View v) {
    if(mController instanceof NavigationControllerFragment) ((NavigationControllerFragment)mController).quit();
  }
}