package com.ldt.nav.sample.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;

import com.ldt.nav.sample.R;
import com.ldt.navigation.NavigationFragment;

import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SamplePage extends NavigationFragment {
    private static final String TAG = "SamplePage";

    @OnClick(R.id.back_button)
    void back() {
        dismiss();
    }

    @OnClick(R.id.button)
    void goToSomeWhere() {
        String text = mEditText.getText().toString();
        int value = -1; try {
        value = Integer.parseInt(text);
        presentFragment(SamplePageTwo.newInstance(value));
        } catch (Exception e) {
            presentFragment(new SamplePageTwo());
        }

     /*   getFragmentManager()
                .beginTransaction()
                .replace(R.id.container,new SamplePage(),"sample-page")
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        .setCustomAnimations(R.animator.rotatedown_left_in,R.animator.slide_fragment_horizontal_left_in).commit();*/
    }

    @BindView(R.id.edit_text)
    EditText mEditText;

    @Nullable
    @Override
    protected View onCreateContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.sample_page,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this,view);
       root = view;
    }
    View root;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    int p = -1;
    @Override
    public int defaultTransition() {
        if(p==-1) {
            Random r = new Random();
            p = r.nextInt(39) + 1; //exclude NONE present style
        }
        return p;
    }
}
