package com.ldt.navigation.uicontainer;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.ldt.navigation.NavigationController;
import com.ldt.navigation.R;

public class ScalableDialogContainer2 implements UIContainer, DialogInterface.OnCancelListener, DialogInterface.OnDismissListener {
    private int w;
    private int h;
    private float dpUnit;
    @Override
    public void provideController(NavigationController controller, int wQualifier, int hQualifier, float dpUnit) {
        mController = controller;
        w = wQualifier;
        h = hQualifier;
        this.dpUnit = dpUnit;
    }

    private float suitableSize(int qualifier) {
        if(qualifier<=400) return qualifier - 32;
        else if(qualifier<=600) return qualifier - 48;
        else if(qualifier<=800) return qualifier - 128;
        else return qualifier*3f/4;
    }

    public View provideLayout(Context context, LayoutInflater inflater, ViewGroup viewGroup, int subContainerId) {
        View v = inflater.inflate(R.layout.dialog_container, viewGroup, false);
        View dialog = v.findViewById(R.id.sub_container);
        dialog.setId(subContainerId);

        float ratio = ((float) w)/h;
        // 3/4 <= ratio <= 4/3
        float newW, newH;
        if(ratio < 3f/4) {

            newW = suitableSize(w);
            newH = newW*4f/3;

        } else if (ratio > 4f/3){
            newH = suitableSize(h);
            newW = newH*4f/3;
        } else {
            newW  = suitableSize(w);
            newH = suitableSize(h);
        }

        ViewGroup.LayoutParams params = dialog.getLayoutParams();
        params.width = (int)(newW*dpUnit);
        params.height = (int)(newH*dpUnit);

        return v;
    }

    private NavigationController mController;

    @Override
    public void created(Bundle savedInstanceState) {
        mShowsDialog = true;//mContainerId == 0;
        if (savedInstanceState != null) {
            mStyle = savedInstanceState.getInt(SAVED_STYLE, STYLE_NORMAL);
            mTheme = savedInstanceState.getInt(SAVED_THEME, 0);
            mCancelable = savedInstanceState.getBoolean(SAVED_CANCELABLE, true);
            mShowsDialog = savedInstanceState.getBoolean(SAVED_SHOWS_DIALOG, mShowsDialog);
            mBackStackId = savedInstanceState.getInt(SAVED_BACK_STACK_ID, -1);
        }
    }

    @Override
    public void destroy() {
        mController = null;
    }


    /**
     * Style for {@link #setStyle(int, int)}: a basic,
     * normal dialog.
     */
    public static final int STYLE_NORMAL = 0;
    /**
     * Style for {@link #setStyle(int, int)}: don't include
     * a title area.
     */
    public static final int STYLE_NO_TITLE = 1;
    /**
     * Style for {@link #setStyle(int, int)}: don't draw
     * any frame at all; the view hierarchy returned by {@link #provideLayout}
     * is entirely responsible for drawing the dialog.
     */
    public static final int STYLE_NO_FRAME = 2;
    /**
     * Style for {@link #setStyle(int, int)}: like
     * {@link #STYLE_NO_FRAME}, but also disables all input to the dialog.
     * The user can not touch it, and its window will not receive input focus.
     */
    public static final int STYLE_NO_INPUT = 3;
    private static final String SAVED_DIALOG_STATE_TAG = "android:savedDialogState";
    private static final String SAVED_STYLE = "android:style";
    private static final String SAVED_THEME = "android:theme";
    private static final String SAVED_CANCELABLE = "android:cancelable";
    private static final String SAVED_SHOWS_DIALOG = "android:showsDialog";
    private static final String SAVED_BACK_STACK_ID = "android:backStackId";
    int mStyle = STYLE_NORMAL;
    int mTheme = 0;
    boolean mCancelable = true;
    boolean mShowsDialog = true;
    int mBackStackId = -1;
    Dialog mDialog;
    boolean mDestroyed;
    boolean mRemoved;
    /**
     * Call to customize the basic appearance and behavior of the
     * fragment's dialog.  This can be used for some common dialog behaviors,
     * taking care of selecting flags, theme, and other options for you.  The
     * same effect can be achieve by manually setting Dialog and Window
     * attributes yourself.  Calling this after the fragment's Dialog is
     * created will have no effect.
     *
     * @param style Selects a standard style: may be {@link #STYLE_NORMAL},
     * {@link #STYLE_NO_TITLE}, {@link #STYLE_NO_FRAME}, or
     * {@link #STYLE_NO_INPUT}.
     * @param theme Optional custom theme.  If 0, an appropriate theme (based
     * on the style) will be selected for you.
     */
    public void setStyle(int style, int theme) {
        mStyle = style;
        if (mStyle == STYLE_NO_FRAME || mStyle == STYLE_NO_INPUT) {
            mTheme = android.R.style.Theme_Panel;
        }
        if (theme != 0) {
            mTheme = theme;
        }
    }

    public Dialog getDialog() {
        return mDialog;
    }
    public int getTheme() {
        return mTheme;
    }
    /**
     * Control whether the shown Dialog is cancelable.  Use this instead of
     * directly calling {@link Dialog#setCancelable(boolean)
     * Dialog.setCancelable(boolean)}, because DialogFragment needs to change
     * its behavior based on this.
     *
     * @param cancelable If true, the dialog is cancelable.  The default
     * is true.
     */
    public void setCancelable(boolean cancelable) {
        mCancelable = cancelable;
        if (mDialog != null) mDialog.setCancelable(cancelable);
    }
    /**
     * Return the current value of {@link #setCancelable(boolean)}.
     */
    public boolean isCancelable() {
        return mCancelable;
    }

    public void setShowsDialog(boolean showsDialog) {
        mShowsDialog = showsDialog;
    }
    /**
     * Return the current value of {@link #setShowsDialog(boolean)}.
     */
    public boolean getShowsDialog() {
        return mShowsDialog;
    }

    @Override
    public LayoutInflater provideLayoutInflater(Bundle savedInstanceState) {
        if (!mShowsDialog) {
            return null;
        }

        mDialog = onCreateDialog(savedInstanceState);
        mDestroyed = false;
        switch (mStyle) {
            case STYLE_NO_INPUT:
                mDialog.getWindow().addFlags(
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                // fall through...
            case STYLE_NO_FRAME:
            case STYLE_NO_TITLE:
                mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        }
        return (LayoutInflater)mDialog.getContext().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
    }


    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new Dialog(mController.getActivity(), getTheme());
    }

    public void onCancel(DialogInterface dialog) {
    }
    public void onDismiss(DialogInterface dialog) {
        if (!mRemoved) {
            // Note: we need to use allowStateLoss, because the dialog
            // dispatches this asynchronously so we can receive the call
            // after the activity is paused.  Worst case, when the user comes
            // back to the activity they see the dialog again.
            //dismissInternal(true);

        }
    }

    @Override
    public void activityCreated(Bundle savedInstanceState) {
        if (!mShowsDialog) {
            return;
        }
        View view = mController.getView();
        if (view != null) {
            if (view.getParent() != null) {
                ((ViewGroup)view.getParent()).removeView(view);
                //throw new IllegalStateException("DialogFragment can not be attached to a container view");
            }
            mDialog.setContentView(view);
        }
        mDialog.setOwnerActivity(mController.getActivity());
        mDialog.setCancelable(mCancelable);
        mDialog.setOnCancelListener(this);
        mDialog.setOnDismissListener(this);
        if (savedInstanceState != null) {
            Bundle dialogState = savedInstanceState.getBundle(SAVED_DIALOG_STATE_TAG);
            if (dialogState != null) {
                mDialog.onRestoreInstanceState(dialogState);
            }
        }
    }
    @Override
    public void start() {
        if (mDialog != null) {
            mRemoved = false;
            mDialog.show();
        }
    }
    @Override
    public void saveState(Bundle outState) {
        if (mDialog != null) {
            Bundle dialogState = mDialog.onSaveInstanceState();
            if (dialogState != null) {
                outState.putBundle(SAVED_DIALOG_STATE_TAG, dialogState);
            }
        }
        if (mStyle != STYLE_NORMAL) {
            outState.putInt(SAVED_STYLE, mStyle);
        }
        if (mTheme != 0) {
            outState.putInt(SAVED_THEME, mTheme);
        }
        if (!mCancelable) {
            outState.putBoolean(SAVED_CANCELABLE, mCancelable);
        }
        if (!mShowsDialog) {
            outState.putBoolean(SAVED_SHOWS_DIALOG, mShowsDialog);
        }
        if (mBackStackId != -1) {
            outState.putInt(SAVED_BACK_STACK_ID, mBackStackId);
        }
    }
    @Override
    public void stop() {
        if (mDialog != null) {
            mDialog.hide();
        }
    }
    /**
     * Remove dialog.
     */
    @Override
    public void destroyView() {
        mDestroyed = true;
        if (mDialog != null) {
            // Set removed here because this dismissal is just to hide
            // the dialog -- we don't want this to cause the fragment to
            // actually be removed.
            mRemoved = true;
            mDialog.dismiss();
            mDialog = null;
        }
    }

    @Override
    public boolean shouldAttachToContainerView() {
        return false;
    }
}
