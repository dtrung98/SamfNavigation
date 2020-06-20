package com.ldt.navigation.uicontainer;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.ldt.navigation.NavigationController;

/**
 *  Container hiển thị giao diện navigation là dialog nếu màn hình rộng,
 *  dạng bottom sheet nếu màn hình vừa và dọc
 *  dạng toàn màn hình nếu màn hình nhỏ
 */
public class ModalPresentationContainer extends FlexibleContainer {
  @NonNull
  @Override
  protected UIContainer createSubContainer(Fragment controller, int wQualifier, int hQualifier, float dpUnit) {
    if(hQualifier >= 432 && wQualifier >= 432) return new ScalableDialogContainer();
    else if(hQualifier >= 300 &&(float)hQualifier/wQualifier >= 4f/3) return new StandardBottomSheetContainer();
    else return new ExpandContainer();
  }
}
