package com.ldt.navigation.container;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import com.ldt.navigation.NavigationControllerFragment;
import com.ldt.navigation.NavigationFragment;
import com.ldt.navigation.R;
import com.ldt.navigation.uicontainer.ExpandStaticContainer;
import com.ldt.navigation.uicontainer.UIContainer;

import java.util.ArrayList;
import java.util.List;

/*
SplitRouter cung cấp sẵn 2 Navigation Controller Master-Detail.
<br> Ở giao diện Side-by-side, Master nằm bên trái và Detail nằm bên phải, chúng là 2 controller riêng biệt
<br> Ở giao diện Compact, Detail biến mất, toàn bộ Fragment trong Detail được gộp vào Master
 */
public interface SplitContainerNavigator extends BaseSplitContainerNavigator {
    String TAG = "SplitRouter2";
    String DETAIL_FRAGMENT_TAGS_IN_COMPACT_MODE = "detail-fragment-tags-in-compact-mode";

    default View inflateSingleModeLayout(@NonNull Context context, @Nullable ViewGroup rootView) {
        return LayoutInflater.from(context).inflate(R.layout.main_common, rootView, false);
    }

    default View inflateSplitModeLayout(@NonNull Context context, @Nullable ViewGroup rootView) {
        return LayoutInflater.from(context).inflate(R.layout.main_two_panel, rootView, false);
    }

    /**
     * Goị phương thức này trong {@link Activity#setContentView(View)} hoặc onCreateView/onCreateContentView của Fragment/NavigationFragment
     * @param context Context
     * @return View
     */
    default View provideLayout(@NonNull Context context) {
        SplitNavigatorAttribute saver = requireSplitRouterAttribute();

        SplitCondition condition = new SplitCondition();

        int screenWidthDp = context.getResources().getConfiguration().screenWidthDp;
        int screenHeightDp = context.getResources().getConfiguration().screenHeightDp;
        float dpUnit = context.getResources().getDimension(R.dimen.dpUnit);

        onConfigureSplitRouter(condition, screenWidthDp, screenHeightDp);
        saver.setUp(condition, screenWidthDp, screenHeightDp);

        View rootView = (saver.isInSplitMode()) ? inflateSplitModeLayout(context, null) : inflateSingleModeLayout(context, null);

        if(saver.isInSplitMode()) {
            View leftContainer = rootView.findViewById(requireSplitRouterAttribute().getMasterContainerViewId());
            View rightContainer = rootView.findViewById(requireSplitRouterAttribute().getDetailContainerViewId());
            if(saver.leftWide!=-1) {
                leftContainer.getLayoutParams().width = (int) (saver.leftWide*dpUnit);
                rightContainer.getLayoutParams().width = 0;
            } else if(saver.rightWide !=-1) {
                leftContainer.getLayoutParams().width = 0;
                rightContainer.getLayoutParams().width = (int) (saver.rightWide * dpUnit);
            } else {
                saver.leftWide = 350;
                leftContainer.getLayoutParams().width = (int) (saver.leftWide * dpUnit);
                rightContainer.getLayoutParams().width = 0;
            }
        }
        return rootView;
    }

    class SplitCondition {
        boolean configOnce = true;
        int splitWhenWiderThan = 720;
        int splitWhenTallerThan = -1;
        int leftWide = -1;
        int rightWide = -1;

        public void setRightRouterStartUpFragmentIsIntroFragment(boolean startUpFragmentInRightRouterIsIntroFragment) {
            this.introStartupInRightRouter = startUpFragmentInRightRouterIsIntroFragment;
        }

        boolean introStartupInRightRouter = true;

        public SplitContainerNavigator.SplitCondition configAgain() {
            configOnce = false;
            return this;
        }

        public SplitContainerNavigator.SplitCondition configLeftWide(int dp) {
            leftWide = dp;
            return this;
        }

        /*
        Nếu leftWide được đặt thì giá trị này bị bỏ qua
         */
        public void configRightWide(int dp) {
            rightWide = dp;
        }

        public SplitContainerNavigator.SplitCondition widerThan(int dp) {
            splitWhenWiderThan = dp;
            return this;
        }

        public SplitContainerNavigator.SplitCondition tallerThan(int dp) {
            splitWhenTallerThan = dp;
            return this;
        }

        public void commit() {

        }
    }

    default void onConfigureSplitRouter(SplitCondition splitWhen, int screenWidthDp, int screenHeightDp) {
        splitWhen
                .widerThan(720)
                .tallerThan(-1)
                .configLeftWide(350)
                .configRightWide(-1);
    }

    @Override
    default boolean navigateBack(boolean animated) {
        // ở chế độ split
        // back tới fragment root của right router rồi tới left router, không quit right router và left router
        // khi cả left và right đều chỉ còn root, thì trả về false

        SplitNavigatorAttribute saver = requireSplitRouterAttribute();
        if(saver.isInSplitMode()) {
            // lấy controller hợp lệ
            NavigationControllerFragment controller;
            int count = saver.count();
            boolean result = true;

            for(int i = count - 1; i >= 0; i--) {
                controller = saver.controllerAt(i);
                int fragmentCount = controller.getFragmentCount();
                //  Chưa tới root thì cứ back thôi
                if(fragmentCount != 1) {
                    result = controller.navigateBack(animated);
                    break;
                }

                if(controller.mControllerTag.equals(saver.getDetailControllerTag()) || controller.mControllerTag.equals(saver.getMasterControllerTag())) {
                    // Tới root và nó là fragment left hoặc right
                    if(i!=0) continue;

                    // hết controller rồi, trả về false
                    result = false;
                    break;
                }

                // controller bình thường
                saver.popAt(i);
                if(i!=0) {
                    controller.removeFromFragmentManager();
                    result = true;
                    break;
                } else result = false;
            }
            return result;

        }
        else return BaseSplitContainerNavigator.super.navigateBack(animated);
    }

    @Override
    default void present(@NonNull String uniquePresentName, Class<? extends UIContainer> uiContainerClass, NavigationFragment... initialFragments) {
        present(uniquePresentName, R.id.floating_container, uiContainerClass, initialFragments);
    }

    FragmentManager provideFragmentManager();

    @NonNull
    Class<? extends NavigationFragment> provideDefaultDetailFragment();

    @NonNull
    Class<? extends NavigationFragment> provideDefaultMasterFragment();
    @Override
    default void masterControllerNavigateTo(NavigationFragment fragment, boolean animated) {
        SplitNavigatorAttribute saver = requireSplitRouterAttribute();
        NavigationControllerFragment controller = findMasterController();

        if(controller == null) {
            presentMasterController(fragment);
        }
        else
            controller.navigate(this, fragment, animated);
        saver.putFragmentType(fragment.getIdentifyTag(), false);
    }

    default Class<? extends UIContainer> provideMasterUIContainer() {
        return ExpandStaticContainer.class;
    }

    default Class<? extends UIContainer> provideDetailUIContainer() {
        return ExpandStaticContainer.class;
    }


    @Override
    default NavigationControllerFragment presentMasterController(@Nullable NavigationFragment... initialFragment) {
        SplitNavigatorAttribute saver = requireSplitRouterAttribute();

        NavigationControllerFragment controller = findMasterController();
        if(controller == null) {
            Class<? extends NavigationFragment> clazz = provideDefaultMasterFragment();
            if (initialFragment == null || initialFragment.length == 0) {
                NavigationFragment fragment = null;
                try {
                    fragment = clazz.newInstance();
                } catch (Exception ignored) {
                }

                if (fragment == null)
                    throw new IllegalArgumentException("Unable to create new default fragment for master controller");
                controller = NavigationControllerFragment.newInstance(MasterNavigationController.class,
                        saver.getMasterControllerTag(),
                        provideFragmentManager(),
                        saver.getMasterContainerViewId(),
                        provideMasterUIContainer(),
                        fragment);
            } else
                controller = NavigationControllerFragment.newInstance(MasterNavigationController.class, saver.getMasterControllerTag(), provideFragmentManager(), saver.getMasterContainerViewId(), provideMasterUIContainer(), initialFragment);
                saver.setMasterController(controller);
        }

        controller.setParentNavigator(this);
        return controller;
    }

    @Override
    default NavigationControllerFragment presentDetailController(@Nullable NavigationFragment... initialFragment) {
        SplitNavigatorAttribute saver = requireSplitRouterAttribute();

        NavigationControllerFragment controller = findDetailController();
        if(controller == null) {
            Class<? extends NavigationFragment> clazz = provideDefaultDetailFragment();
            if (initialFragment == null || initialFragment.length == 0) {
                NavigationFragment fragment = null;
                try {
                    fragment = clazz.newInstance();
                } catch (Exception ignored) {
                }

                if (fragment == null)
                    throw new IllegalArgumentException("Unable to create new default fragment for detail controller");

                controller = NavigationControllerFragment.newInstance(NavigationControllerFragment.class,
                        saver.getDetailControllerTag(),
                        provideFragmentManager(),
                        saver.getDetailContainerViewId(),
                        provideDetailUIContainer(),
                        fragment);

                /* save default detail fragment tag, because it is created automatically */
                saver.mDefaultDetailFragmentTag = fragment.getIdentifyTag();

                /* save detail fragment tag into saver */
                saver.putFragmentType(fragment.getIdentifyTag(), true);
            } else {
                controller = NavigationControllerFragment.newInstance(
                        NavigationControllerFragment.class,
                        saver.getDetailControllerTag(),
                        provideFragmentManager(),
                        saver.getDetailContainerViewId(),
                        provideDetailUIContainer(), initialFragment);

                /* put detail tags into saver */
                for (NavigationFragment f :
                        initialFragment) {
                    saver.putFragmentType(f.getIdentifyTag(), true);
                }
            }
            saver.setDetailController(controller);

        }

        controller.setParentNavigator(this);
        return controller;
    }

    @Override
    default void masterControllerSwitchNew(NavigationFragment fragment) {
        SplitNavigatorAttribute saver = requireSplitRouterAttribute();
        NavigationControllerFragment controller = findMasterController();

        if(controller == null)
            presentMasterController(fragment);
        else
            controller.switchNew(fragment);
        saver.putFragmentType(fragment.getIdentifyTag(), false);
    }

    @Override
    default void onSaveNavigatorState(Bundle outState) {
        /* save controllers stack */
        BaseSplitContainerNavigator.super.onSaveNavigatorState(outState);
        SplitNavigatorAttribute saver = requireSplitRouterAttribute();
        outState.putString(DETAIL_CONTROLLER_DEFAULT_FRAGMENT_TAG, saver.mDefaultDetailFragmentTag);
        /* if current mode is split, we save the initial fragment tag to remove it if next mode is non-split */
        if (!saver.isInSplitMode()) {
            /* in compact mode */
            /* save the detail fragment tags these have been pushed to master, then the router can automatically push them to detail controller when the next mode is split */
            NavigationControllerFragment master = findMasterController();
            if(master != null) {
                int masterCount = master.getFragmentCount();
                ArrayList<String> detailTags = new ArrayList<>();
                String tag;
                for (int i = 0; i < masterCount; i++) {
                    tag = master.getFragmentAt(i).getIdentifyTag();
                    if(saver.isDetailFragment(tag)) detailTags.add(tag);
                }

                outState.putStringArrayList(DETAIL_FRAGMENT_TAGS_IN_COMPACT_MODE, detailTags);
            }
        } else {
            /* do nothing */
        }
    }

    @Override
    default void detailControllerNavigateTo(NavigationFragment fragment, boolean animated) {
        SplitNavigatorAttribute saver = requireSplitRouterAttribute();
        NavigationControllerFragment controller = (saver.isInSplitMode()) ? findDetailController() : findMasterController();

        if(controller == null)
            controller = (saver.isInSplitMode()) ? presentDetailController(fragment) : presentMasterController(fragment);
        else
            controller.navigate(fragment);
        saver.putFragmentType(fragment.getIdentifyTag(), true);
    }

    @Override
    default void onCreateNavigator(Bundle bundle) {
        /* get router saver */
        SplitNavigatorAttribute saver = requireSplitRouterAttribute();

        /* restore controller stack if any*/
        if(saver.doesRouterNeedToRestore() && bundle != null) {
            onRestoreNavigatorState(bundle);
            saver.routerRestored();
        }

        /* first-time initializing, simply present needed controllers */
        if(bundle == null) {
            presentMasterController();
            if(saver.isInSplitMode())
                presentDetailController();

            Log.d(TAG, "create router: first time");
        } else {
            /* we try to re-initialize router to fit current mode */
            /* master controller should not be null. however, if it is null, we still try to initialize it */
            NavigationControllerFragment master = presentMasterController();

            if (saver.isInSplitMode()) {
                /* in split mode */
                /* get detail fragment tags pushed-in-compact-mode-master-controller */
                ArrayList<String> detailTags =  bundle.getStringArrayList(DETAIL_FRAGMENT_TAGS_IN_COMPACT_MODE);

                if(detailTags != null && !detailTags.isEmpty()) {
                    /* there are some detail fragments in master controllers */
                    /* detail controller should be null, if not, we call switch new */
                    ArrayList<NavigationFragment> fragments = new ArrayList<>(detailTags.size());
                    NavigationFragment temp;
                    for (String tag :
                            detailTags) {
                        temp = master.findFragment(tag);
                        if (temp != null) {
                            fragments.add(temp);
                        }
                    }

                    int detailInMasterCount = fragments.size();

                    // dismiss detail fragments in master controller
                    NavigationControllerFragment.ControllerTransaction controllerTransaction = master.beginTransaction();
                    for (NavigationFragment nf : fragments)
                        controllerTransaction.dismiss(nf);
                    controllerTransaction.withAnimation(false).executeTransaction();

                    // try to find detail controller
                    // detail controller should be null. If not, we call switch new method
                    NavigationControllerFragment detailController = findDetailController();

                    // present detail fragment in detail controller

                    if (detailController == null && detailInMasterCount == 0)
                        /* no detail controller, no initial fragments */
                        presentDetailController();
                    else if (detailController == null) {
                        /* no detail controller, some initial fragments */
                        NavigationFragment[] fragmentArray = fragments.toArray(new NavigationFragment[0]);
                        presentDetailController(fragmentArray);
                    } else if (detailInMasterCount != 0) {
                        /* existing detail controller, some initial fragments */
                        detailController.switchNew(fragments.get(0));
                        for (int i = 1; i < detailInMasterCount; i++) {
                            detailController.navigate(fragments.get(i));
                        }
                    } else presentDetailController();
                } else presentDetailController(); /* else no detail fragments exist in master, nothing to do */
            } else {
                /* in compact mode */
                /*  move all detail fragments into master controller, and save their properties */

                /* get detail fragment tags pushed-in-compact-mode-master-controller */
                ArrayList<String> detailTags =  bundle.getStringArrayList(DETAIL_FRAGMENT_TAGS_IN_COMPACT_MODE);

                /* The default fragment tag, if it hasn't been replaced */
                saver.mDefaultDetailFragmentTag = bundle.getString(DETAIL_CONTROLLER_DEFAULT_FRAGMENT_TAG);
                Log.d(TAG, "onCreateRouter: found default fragment tag "+ saver.mDefaultDetailFragmentTag);
                /* restore fragment types */
                if(detailTags != null && !detailTags.isEmpty()) {

                    for (String tag :
                            detailTags) {
                        saver.putFragmentType(tag, true);
                    }
                }

                /* find detail controller, if router's previous mode is split, it will available */
                NavigationControllerFragment detail = findDetailController();
                if(detail != null) {
                    /* detail fragment count */
                    int detailCount = detail.getFragmentCount();
                    ArrayList<NavigationFragment> fragments = new ArrayList<>();

                    /* find all detail fragments by their tags */
                    NavigationFragment temp;
                    for (int i = 0; i < detailCount; i++) {
                        temp = detail.getFragmentAt(i);
                        if( temp != null) {
                            fragments.add(temp);
                            saver.putFragmentType(temp.getIdentifyTag(), true);
                        }
                    }

                    /* dismiss all fragments in detail controller */
                    NavigationControllerFragment.ControllerTransaction detailTransaction = detail.beginTransaction();
                    for (NavigationFragment nf :
                            fragments) {
                        detailTransaction.dismiss(nf);
                    }
                    detailTransaction.withAnimation(false).executeTransaction();

                    /* dismiss detail controller */
                    detail.quit();

                    /* if the detail controller has only one fragment, and it's default fragment */
                    /* not need to add it to master controller */
                    if(fragments.size() == 1 && fragments.get(0).getIdentifyTag().equals(saver.mDefaultDetailFragmentTag))
                        fragments.clear();

                    /* add all other detail fragments into master controller */
                    NavigationControllerFragment.ControllerTransaction masterTransaction = master.beginTransaction();
                    for(NavigationFragment nf: fragments)
                        masterTransaction.navigateTo(nf);
                    masterTransaction.withAnimation(false).executeTransaction();
                }
            }
        }
    }

    @Override
    default void detailControllerSwitchNew(NavigationFragment fragment) {
        SplitNavigatorAttribute saver = requireSplitRouterAttribute();
        NavigationControllerFragment controller = null;
        if(saver.isInSplitMode()) {
            /* In Split mode, we normally switch new in detail controller */
            controller = findDetailController();
            if(controller == null) presentDetailController(fragment);
            else controller.switchNew(fragment);
        } else {
            /* In Compact mode, we will dismiss all detail fragments in master, then add this fragment to top of master */
            controller = findMasterController();
            if(controller == null) {
                controller = presentMasterController();
                controller.navigate(fragment);
            }
            else {
                int masterSize = controller.getFragmentCount();
                List<NavigationFragment> fragments = new ArrayList<>();
                NavigationFragment temp;
                for (int i = masterSize - 1; i >= 0; i--) {
                    temp = controller.getFragmentAt(i);
                    if(saver.isDetailFragment(temp.getIdentifyTag())) {
                        fragments.add(temp);
                    }
                }

                NavigationControllerFragment.ControllerTransaction controllerTransaction = controller.beginTransaction();
                for (NavigationFragment nf :
                        fragments) {
                    controllerTransaction.dismiss(nf);
                }
                controllerTransaction.navigateTo(fragment);
                controllerTransaction.withAnimation(true).executeTransaction(); // :D
            }
        }

        saver.putFragmentType(fragment.getIdentifyTag(), true);
    }
}
