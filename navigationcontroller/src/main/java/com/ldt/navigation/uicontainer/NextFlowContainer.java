package com.ldt.navigation.uicontainer;

/**
 *  Container hiển thị giao diện navigation là dialog nếu màn hình rộng,
 *  dạng bottom sheet nếu màn hình vừa và dọc
 *  dạng toàn màn hình nếu màn hình nhỏ
 */
public class NextFlowContainer implements ComplexContainer {
  private UIContainer subContainer;

  @Override
  public UIContainer getSubContainer() {
    return subContainer;
  }

  @Override
  public void provideConfig(int wq, int hq, float dpUnit) {

    if(hq>=432&&wq>=432) subContainer = new ScalableDialogContainer();
    else if(hq>=300&&(float)hq/wq >=4f/3) subContainer = new BottomSheetContainer();
    else subContainer = new ExpandContainer();
    subContainer.provideConfig(wq, hq, dpUnit);
  }
}