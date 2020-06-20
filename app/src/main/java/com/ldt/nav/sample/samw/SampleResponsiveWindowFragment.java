package com.ldt.nav.sample.samw;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ldt.nav.sample.R;

import java.util.Random;

public class SampleResponsiveWindowFragment extends ResponsiveWindowFragment {
    private static int mFrameHeight = 650;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(mFrameHeight>100) mFrameHeight -=100;

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bottom_sheet, container, false);
        ((ViewGroup.MarginLayoutParams)view.findViewById(R.id.viewFrame).getLayoutParams()).height = (int) (mFrameHeight * view.getContext().getResources().getDimension(R.dimen.dpUnit));
        view.findViewById(R.id.actionOne).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SampleResponsiveWindowFragment().show(requireFragmentManager(),"tag"+new Random());
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

    }
}
