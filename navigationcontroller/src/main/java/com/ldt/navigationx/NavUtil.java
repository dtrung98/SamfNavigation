package com.ldt.navigationx;

import android.content.Context;

import com.ldt.navigation.uicontainer.BottomSheetContainer;
import com.ldt.navigation.uicontainer.ExpandContainer;
import com.ldt.navigation.uicontainer.NewFlowContainer;
import com.ldt.navigation.uicontainer.ScalableDialogContainer;
import com.ldt.navigation.uicontainer.UIContainer;

public class NavUtil {
    public static UIContainer findUIContainer(Context context, String name) {
        if(name == null) return null;
        switch (name) {
            case "0": return new ExpandContainer();
            case "1": return new ScalableDialogContainer();
            case "2": return new BottomSheetContainer();
            case "3": return new NewFlowContainer();
            default: return UIContainer.instantiate(context, name);
        }
    }
}
