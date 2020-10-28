package com.ldt.nav.sample.embed;

import android.animation.TimeInterpolator;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;

import com.ldt.nav.sample.R;
import com.ldt.navigation.NavigationControllerFragment;
import com.ldt.navigation.NavigationFragment;
import com.ldt.navigation.PresentStyle;
import com.ldt.navigation.container.SplitNavigatorImpl;
import com.ldt.navigation.uicontainer.AdaptiveContainer;
import com.ldt.navigation.uicontainer.AnimatorUIContainer;
import com.ldt.navigation.uicontainer.ModalPresentationContainer;
import com.ldt.navigation.uicontainer.UIContainer;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SamplePage extends NavigationFragment {
    private static final String TAG = "SamplePage";
    public static final String DEFAULT_P = "default-p";
    public static final String INDEX = "index";

    public static SamplePage newInstance(int index, int value) {
        SamplePage fragment = new SamplePage();
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

    @Nullable
    @BindView(R.id.drawer)
    DrawerLayout mDrawerLayout;

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
            navigate(SamplePage.newInstance(mIndex + 1, value));
        } catch (Exception e) {
            navigate(SamplePage.newInstance(mIndex + 1, -1));
        }
        
       /* if(getFragmentManager()!=null)
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.container,new SamplePage(),"sample-page-2")
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
       .commit();*/

    }

    private void setUpNavigationBar() {
        NavigationControllerFragment controllerFragment = getNavigationController();
        if (controllerFragment != null) {
            UIContainer uiContainer = controllerFragment.getUiContainer();
            if (uiContainer instanceof AdaptiveContainer) {
                uiContainer = ((AdaptiveContainer) uiContainer).getCurrentContainer();
            }

            if (uiContainer instanceof AnimatorUIContainer) {
                ViewGroup navigationBar = ((AnimatorUIContainer) uiContainer).getRootView().findViewById(R.id.navigation_bar);
                if (navigationBar != null) {
                    TextView textView = navigationBar.findViewById(R.id.title);
                    ImageView startButton = navigationBar.findViewById(R.id.start_button);
                    ImageView endButton = navigationBar.findViewById(R.id.end_button);
                    if (textView != null && startButton != null && endButton != null) {
                        if (mIndex == 0 && getNavigationController() != null) {
                            startButton.setImageResource(R.drawable.ic_home_24dp);
                            startButton.setVisibility(View.INVISIBLE);
                            endButton.setVisibility(View.VISIBLE);


                            if(mDrawerLayout != null) {
                                mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);
                            }
                        } else {
                            startButton.setVisibility(View.VISIBLE);
                            startButton.setImageResource(R.drawable.ic_arrow_back_24dp);
                            textView.setText("Sample Page " + mIndex);
                            endButton.setVisibility(View.INVISIBLE);
                            if(mDrawerLayout != null) {
                                mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                            }
                        }
                    }

                }
            }
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(hidden) return;
        setUpNavigationBar();
    }

    @OnClick(R.id.button_2)
    void openSetting() {
        getNavigationController().presentTo("setting-nav",
                new ModalPresentationContainer(), SamplePage.newInstance(0, 0));
    }

    @OnClick(R.id.button_view1)
    void viewContent1() {
        SplitNavigatorImpl router = (SplitNavigatorImpl) getActivity();
        if (router != null) {
            router.detailControllerSwitchNew(new SamplePage());
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

        DrawerLayout drawerLayout = view.findViewById(R.id.drawer);
        if(drawerLayout != null) {
            drawerLayout.openDrawer(Gravity.RIGHT, false);
            drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
                @Override
                public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

                }

                @Override
                public void onDrawerOpened(@NonNull View drawerView) {

                }

                @Override
                public void onDrawerClosed(@NonNull View drawerView) {
                    getParentNavigator().requestBack();
                }

                @Override
                public void onDrawerStateChanged(int newState) {

                }
            });
        }

       /* if (mIndex == 0 && getNavigationController() != null) {
            //mBackButton.setImageResource(R.drawable.ic_home_24dp);
            mBackButton.setVisibility(View.INVISIBLE);
            mQuitButton.setVisibility(View.VISIBLE);
        } else {
            mBackButton.setVisibility(View.VISIBLE);
            mBackButton.setImageResource(R.drawable.ic_arrow_back_24dp);
            mTitleTextView.setText("Sample Page " + mIndex);
            mQuitButton.setVisibility(View.INVISIBLE);
        }*/

        setUpNavigationBar();

        int type = mIndex % 3;
        if (type == 0) {
        //    mBackColorParent.setBackgroundResource(R.color.FlatPurple);
            mButton.setBackgroundResource(R.drawable.background_round_green);

        } else if (type == 1) {
       //     mBackColorParent.setBackgroundResource(R.color.focusGreen);
            mButton.setBackgroundResource(R.drawable.background_round_dark_blue);
        } else if (type == 2) {
         //   mBackColorParent.setBackgroundResource(R.color.FlatOrange);
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

    @BindView(R.id.constraintParent)
    View mConstraintParent;

    @BindView(R.id.backColorParent)
    View mBackColorParent;

    @BindView(R.id.description)
    TextView mDescriptionTextView;

    @BindView(R.id.quit_button)
    View mQuitButton;

    @Override
    public TimeInterpolator defaultInterpolator() {
        return new FastOutSlowInInterpolator();
    }

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
    public int defaultTransition() {
        return PresentStyle.SLIDE_LEFT;
    }

    @Override
    public void onWindowInsetsChanged(int left, int top, int right, int bottom) {
        ((ViewGroup.MarginLayoutParams) mSafeView.getLayoutParams()).setMargins(left, (int) (top + 82 * getResources().getDimension(R.dimen.dpUnit)), right, bottom);
        mSafeView.requestLayout();
        updateDescription();
        Log.d(TAG, "(" + left + ", " + top + ", " + right + ", " + bottom + " )");
    }
}
