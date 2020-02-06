package com.ldt.navigation.uicontainer;

/**
 *  Container hiển thị giao diện navigation là dialog nếu màn hình rộng,
 *  dạng bottom sheet nếu màn hình vừa và dọc
 *  dạng toàn màn hình nếu màn hình nhỏ
 */
public class NextFlowContainer implements ComplexContainer {
  protected UIContainer mSubContainer;

  @Override
  public UIContainer getSubContainer() {
    return mSubContainer;
  }

  @Override
  public void provideConfig(int wq, int hq, float dpUnit) {

    if(hq>=432&&wq>=432) mSubContainer = new ScalableDialogContainer();
    else if(hq>=300&&(float)hq/wq >=4f/3) mSubContainer = new BottomSheetContainer();
    else mSubContainer = new ExpandContainer();
    mSubContainer.provideConfig(wq, hq, dpUnit);
  }
}