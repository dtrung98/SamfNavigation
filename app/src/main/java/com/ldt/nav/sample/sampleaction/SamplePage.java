package com.ldt.nav.sample.sampleaction;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ldt.nav.sample.R;
import com.ldt.navigation.NavigationFragment;
import com.ldt.navigation.action.BaseFragment;

import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SamplePage extends BaseFragment {
    private static final String TAG = "SamplePage";

    @OnClick(R.id.back_button)
    void back() {
        finishFragment();
    }

    @OnClick(R.id.button)
    void goToSomeWhere() {
        String text = mEditText.getText().toString();
        int value = -1; try {
        value = Integer.parseInt(text);
        navigateTo(SamplePageTwo.newInstance(value));
        } catch (Exception e) {
            navigateTo(new SamplePageTwo());
        }
    }

    @BindView(R.id.edit_text)
    EditText mEditText;

    @Nullable
    @Override
    public View createView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.sample_page, null, false);
        ButterKnife.bind(this, view);
        return view;
    }
}
