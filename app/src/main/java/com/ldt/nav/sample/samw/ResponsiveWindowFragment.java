package com.ldt.nav.sample.samw;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.ldt.nav.sample.R;

public class ResponsiveWindowFragment extends DialogFragment {
    @Override
    public int getTheme() {
        return R.style.ResponsiveDialogTheme;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = new ResponsiveDialog(requireContext(), getTheme());
        dialog.getWindow().setNavigationBarColor(Color.TRANSPARENT);
        dialog.getWindow().setStatusBarColor(Color.TRANSPARENT);
        dialog.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        return dialog;
    }
}
