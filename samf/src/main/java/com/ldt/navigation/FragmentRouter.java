package com.ldt.navigation;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.ldt.navigation.effectview.EffectFrameLayout;
import com.ldt.navigation.router.Router;
import com.ldt.navigation.router.RouterAttribute;
import com.ldt.navigation.uicontainer.UIContainer;

public class FragmentRouter extends NavigationFragment {
    public static final int CONTAINER_ROOT_VIEW = R.id.fragmentRouterRootView;

    private final InnerRouter mRouter = new InnerRouter(this);

    public void presentController(String uniqueTag, Class<? extends UIContainer> uiContainerClass, NavigationFragment... initialFragments) {
        mRouter.presentController(uniqueTag, R.id.fragmentRouterRootView, uiContainerClass, initialFragments);
    }

    public NavigationControllerFragment findController(String controllerTag) {
        return mRouter.findController(controllerTag);
    }

    public void finishController(String controllerTag) {
        mRouter.findController(controllerTag);
    }

    private static class InnerRouter extends OnBackPressedCallback implements Router {
        private final FragmentRouter mFragment;
        private RouterAttribute mRouterAttribute = new RouterAttribute();

        private InnerRouter(@NonNull FragmentRouter fragment) {
            super(true);
            mFragment = fragment;
        }

        @Override
        public FragmentManager provideFragmentManager() {
            return mFragment.getChildFragmentManager();
        }

        @Override
        public RouterAttribute getRouterAttribute() {
            return mRouterAttribute;
        }

        @Override
        public void dismiss() {
            setEnabled(false);
            remove();
            if (mFragment.getActivity() != null) {
                mFragment.getActivity().onBackPressed();
            }
        }

        @Override
        public void handleOnBackPressed() {
            if (!onNavigateBack()) {
                dismiss();
            }
        }
    }

    public boolean navigateBack(boolean animated) {
        return mRouter.navigateBack(animated);
    }


    public void navigateTo(NavigationFragment nav, boolean animated) {
        mRouter.navigateTo(nav, animated);
    }

    @Override
    public boolean navigateBack() {
        return mRouter.navigateBack();
    }

    @Override
    public void navigateTo(NavigationFragment fragment) {
        mRouter.navigateTo(fragment);
    }

    @Override
    public boolean requestBack() {
        return mRouter.requestBack();
    }

    @Override
    public boolean onNavigateBack() {
        return mRouter.onNavigateBack();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentActivity activity = getActivity();
        if (activity != null) {
            activity.getOnBackPressedDispatcher().addCallback(this, mRouter);
        }
        mRouter.onCreateRouter(savedInstanceState);
    }

    @Nullable
    @Override
    protected View onCreateContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = new EffectFrameLayout(inflater.getContext());
        view.setId(R.id.fragmentRouterRootView);
        view.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        return view;
    }

    public void finish() {
        mRouter.dismiss();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mRouter.onSaveRouterState(outState);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        requireFragmentManager().beginTransaction()
                .setPrimaryNavigationFragment(this)
                .commit();
    }

    public interface Callback {
        void onFinish();
    }
}
