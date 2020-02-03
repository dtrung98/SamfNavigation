package com.ldt.navigation;

import android.view.ViewTreeObserver;

public interface EffectView {
  public static class EffectSaver {
    public float yFraction = 0;
    public float xFraction = 0;
    public ViewTreeObserver.OnPreDrawListener preDrawListener = null;
  }
  
  EffectSaver getEffectSaver();
    
    void setAlpha(float value);
    
    void setTranslationX(float value);
    void setTranslationY(float value);
    
    void setScaleX(float value);
    void setScaleY(float value);
    
    void setRotationX(float value);
    void setRotationY(float value);
    void setRotation(float value);
    
    void setPivotY(float value);
    void setPivotX(float value);
    
    int getWidth();
    int getHeight();
    
    ViewTreeObserver getViewTreeObserver();
    
    /*
    public EffectViee(Context context) {
        super(context);
    }

    public EffectView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EffectView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public EffectView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
    */

    default void setAccordionPivotZero(float value) {
        setAlpha(1.0f);
        setScaleX(value);
        setPivotX(0);
    }

    default void setAccordionPivotWidth(float value) {
        setAlpha(1.0f);
        setScaleX(value);
        setPivotX(getWidth());
    }

    default void setAccordionVerticalPivotZero(float value) {
        setAlpha(1.0f);
        setScaleY(value);
        setPivotY(0);
    }

    default void setAccordionPivotHeight(float value) {
        setAlpha(1.0f);
        setScaleY(value);
        setPivotY(getHeight());
    }

    default void setCube(float fraction) {
        float translationX = getWidth() * fraction;
        setTranslationX(translationX);
        setRotationY(90 * fraction);
        setPivotX(0);
        setPivotY(getHeight() / 2);
    }

    default void setCubeVertical(float fraction) {
        float translationY = getHeight() * fraction;
        setTranslationY(translationY);
        setRotationX(-90 * fraction);
        setPivotY(0);
        setPivotX(getWidth() / 2);
    }

    default void setCubeBack(float fraction) {
        float translationX = getWidth() * fraction;
        setTranslationX(translationX);
        setRotationY(90 * fraction);
        setPivotY(getHeight() / 2);
        setPivotX(getWidth());
    }

    default void setCubeVerticalBack(float fraction) {
        float translationY = getHeight() * fraction;
        setTranslationY(translationY);
        setRotationX(-90 * fraction);
        setPivotX(getWidth() / 2);
        setPivotY(getHeight());
    }

    default void setGlide(float fraction) {
        float translationX = getWidth() * fraction;
        setTranslationX(translationX);
        setRotationY(90 * fraction);
        setPivotX(0);
    }

    default void setGlideBack(float fraction) {
        float translationX = getWidth() * fraction;
        setTranslationX(translationX);
        setRotationY(90 * fraction);
        setPivotX(0);
        setPivotY(getHeight() / 2);
    }

    default void setRotateDown(float fraction) {
        float translationX = getWidth() * fraction;
        setTranslationX(translationX);
        setRotation(20 * fraction);
        setPivotY(getHeight());
        setPivotX(getWidth() / 2);
    }

    default void setRotateUp(float fraction) {
        float translationX = getWidth() * fraction;
        setTranslationX(translationX);
        setRotation(-20 * fraction);
        setPivotY(0);
        setPivotX(getWidth() / 2);
    }

    default void setRotateLeft(float fraction) {
        float translationY = getHeight() * fraction;
        setTranslationY(translationY);
        setRotation(20 * fraction);
        setPivotX(0);
        setPivotY(getHeight() / 2);
    }

    default void setRotateRight(float fraction) {
        float translationY = getHeight() * fraction;
        setTranslationY(translationY);
        setRotation(-20 * fraction);
        setPivotX(getWidth());
        setPivotY(getHeight() / 2);
    }

    default void setYFraction(float fraction) {
        getEffectSaver().yFraction = fraction;
        if (getHeight() == 0) {
            if (getEffectSaver().preDrawListener == null) {
                getEffectSaver().preDrawListener = new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        getViewTreeObserver().removeOnPreDrawListener(
                                getEffectSaver().preDrawListener);
                        setYFraction(getEffectSaver().yFraction);
                        return true;
                    }
                };
                getViewTreeObserver().addOnPreDrawListener(getEffectSaver().preDrawListener);
            }
            return;
        }
        float translationY = getHeight() * fraction;
        setTranslationY(translationY);
    }

    default void setXFraction(float fraction) {
        getEffectSaver().xFraction = fraction;
        if (getWidth() == 0) {
            if (getEffectSaver().preDrawListener == null) {
                getEffectSaver().preDrawListener = new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        getViewTreeObserver().removeOnPreDrawListener(
                                getEffectSaver().preDrawListener);
                        setXFraction(getEffectSaver().xFraction);
                        return true;
                    }
                };
                getViewTreeObserver().addOnPreDrawListener(getEffectSaver().preDrawListener);
            }
            return;
        }
        float translationX = getWidth() * fraction;
        setTranslationX(translationX);
    }

    default void setTableHorizontalPivotZero(float fraction) {
        setRotationY(90 * fraction);
        setPivotX(0);
        setPivotY(getHeight() / 2);
    }

    default void setTableHorizontalPivotWidth(float fraction) {
        setRotationY(-90 * fraction);
        setPivotX(getWidth());
        setPivotY(getHeight() / 2);
    }

    default void setTableVerticalPivotZero(float fraction) {
        setRotationX(-90 * fraction);
        setPivotX(getWidth() / 2);
        setPivotY(0);
    }

    default void setTableVerticalPivotHeight(float fraction) {
        setRotationX(90 * fraction);
        setPivotX(getWidth() / 2);
        setPivotY(getHeight());
    }

    default void setZoomFromCornerPivotHG(float fraction) {
        setScaleX(fraction);
        setScaleY(fraction);
        setPivotX(getWidth());
        setPivotY(getHeight());
    }

    default void setZoomFromCornerPivotZero(float fraction) {
        setScaleX(fraction);
        setScaleY(fraction);
        setPivotX(0);
        setPivotY(0);
    }

    default void setZoomFromCornerPivotWidth(float fraction) {
        setScaleX(fraction);
        setScaleY(fraction);
        setPivotX(getWidth());
        setPivotY(0);
    }

    default void setZoomFromCornerPivotHeight(float fraction) {
        setScaleX(fraction);
        setScaleY(fraction);
        setPivotX(0);
        setPivotY(getHeight());
    }

    default void setZoomSlideHorizontal(float fraction) {
        setTranslationX(getWidth() * fraction);
        setPivotX(getWidth() / 2);
        setPivotY(getHeight() / 2);
    }

    default void setZoomSlideVertical(float fraction) {
        setTranslationY(getHeight() * fraction);
        setPivotX(getWidth() / 2);
        setPivotY(getHeight() / 2);
    }
}
