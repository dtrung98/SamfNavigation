package com.ldt.navigation.uicontainer;

public class FlowContainer implements ComplexContainer {
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