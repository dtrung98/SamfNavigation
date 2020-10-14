package com.ldt.nav.sample.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ldt.nav.sample.R;
import com.ldt.navigation.NavigationFragment;
import com.ldt.navigation.container.ContainerNavigator;
import com.ldt.navigation.container.SplitContainerNavigator;
import com.ldt.navigation.uicontainer.ModalPresentationContainer;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SampleNavFragment extends NavigationFragment {
    private static final String TAG = "SampleNavFragment";
    public static final String DEFAULT_P = "default-p";
    public static final String INDEX = "index";

    public static SampleNavFragment newInstance(int index, int value) {
        SampleNavFragment fragment = new SampleNavFragment();
        Bundle bundle = new Bundle();

        bundle.putInt(DEFAULT_P, value);
        bundle.putInt(INDEX, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    private int mIndex = 0;

    @BindView(R.id.sample)
    TextView mTitleTextView;

    @BindView(R.id.button)
    View mButton;

    @BindView(R.id.button_2)
    View mButtonTwo;

    @BindView(R.id.back_button)
    ImageView mBackButton;

    @OnClick(R.id.back_button)
    void back() {
        navigateBack();
    }

    @OnClick(R.id.button)
    void goToSomeWhere() {

        String text = mEditText.getText().toString();
        int value;
        try {
            value = Integer.parseInt(text);
        } catch (Exception e) {
            value = 0;
        }


        NavigationFragment fragment = SampleNavFragment.newInstance(mIndex + 1, value);
        navigate(fragment);

    }

    @OnClick(R.id.button_2)
    void openSetting() {
        present("setting-nav", ModalPresentationContainer.class, SampleNavFragment.newInstance(0, 0));
    }

    @OnClick(R.id.button_view1)
    void viewContent1() {
        ContainerNavigator navigator = getParentNavigator();
        if(navigator instanceof SplitContainerNavigator) {
            ((SplitContainerNavigator) navigator).detailControllerNavigate(new SampleNavFragment());
        } else {
            navigate(new SampleNavFragment());
        }

    }

    @BindView(R.id.edit_text)
    EditText mEditText;

    @BindView(R.id.safeView)
    View mSafeView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            defaultP = bundle.getInt(DEFAULT_P, defaultP);
            mIndex = bundle.getInt(INDEX, 0);
        }
    }

    @Nullable
    @Override
    protected View onCreateContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.sample_page, container, false);
    }

    private int[] size = new int[6];

    private void updateDescription() {
        mDescriptionTextView.setText(getString(R.string.dimen_description, size[0], size[1], size[2], size[3], size[4], size[5]));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        int w = view.getContext().getResources().getConfiguration().screenWidthDp;//getInteger(R.integer.width_qualifier);

        int h = view.getContext().getResources().getConfiguration().screenHeightDp;//.getInteger(R.integer.height_qualifier);

        size[0] = w;
        size[1] = h;

        if (mIndex == 0 && getNavigationController() != null) {
            //mBackButton.setImageResource(R.drawable.ic_home_24dp);
            mBackButton.setVisibility(View.INVISIBLE);
            mQuitButton.setVisibility(View.VISIBLE);
        } else {
            mBackButton.setVisibility(View.VISIBLE);
            mBackButton.setImageResource(R.drawable.ic_arrow_back_24dp);
            mTitleTextView.setText("Sample Page " + mIndex);
            mQuitButton.setVisibility(View.INVISIBLE);
        }

        int type = mIndex % 3;
        if (type == 0) {
            mRoot.setBackgroundResource(R.color.FlatPurple);
            mButton.setBackgroundResource(R.drawable.background_round_green);

        } else if (type == 1) {
            mRoot.setBackgroundResource(R.color.focusGreen);
            mButton.setBackgroundResource(R.drawable.background_round_dark_blue);
        } else if (type == 2) {
            mRoot.setBackgroundResource(R.color.FlatOrange);
            mButton.setBackgroundResource(R.drawable.background_round_pink);
        }

    }

    @OnClick(R.id.quit_button)
    void quit() {
        if (getNavigationController() != null)
            getNavigationController().quit();
    }

    @BindView(R.id.root)
    View mRoot;

    @BindView(R.id.description)
    TextView mDescriptionTextView;

    @BindView(R.id.quit_button)
    View mQuitButton;

    private int defaultP = -1;

  /*  @Override
    public int defaultTransition() {
      
        if(defaultP ==-1) {
            Random r = new Random();
            defaultP = r.nextInt(40) + 1; //exclude NONE present style
        }
        
        return defaultP;
    }*/

    @Override
    public void onWindowInsetsChanged(int left, int top, int right, int bottom) {
        ((ViewGroup.MarginLayoutParams) mSafeView.getLayoutParams()).setMargins(left, top, right, bottom);
        mSafeView.requestLayout();
        updateDescription();
        Log.d(TAG, "(" + left + ", " + top + ", " + right + ", " + bottom+" )");
    }
}
