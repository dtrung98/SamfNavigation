package com.ldt.navigation;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

public interface EffectView {
  public static class EffectSaver {
    public float yFraction = 0;
    public float xFraction = 0;
    public VieeTreeObserver.OnPreDrawListener preDrawListener = null;
  }
  
  EffectSaver getEffectSaver();
    
    void setAlpha(float value);
    
    void setTranslationX(float value);
    void setTranslationY(float value);
    
    void setScaleX(float value);
    void setScaleY(float value);
    
    void setRotationX(float value);
    void setRotationY(float value);
    
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

    public void setAccordionPivotZero(float value) {
        setAlpha(1.0f);
        setScaleX(value);
        setPivotX(0);
    }

    public void setAccordionPivotWidth(float value) {
        setAlpha(1.0f);
        setScaleX(value);
        setPivotX(getWidth());
    }

    public void setAccordionVerticalPivotZero(float value) {
        setAlpha(1.0f);
        setScaleY(value);
        setPivotY(0);
    }

    public void setAccordionPivotHeight(float value) {
        setAlpha(1.0f);
        setScaleY(value);
        setPivotY(getHeight());
    }

    public void setCube(float fraction) {
        float translationX = getWidth() * fraction;
        setTranslationX(translationX);
        setRotationY(90 * fraction);
        setPivotX(0);
        setPivotY(getHeight() / 2);
    }

    public void setCubeVertical(float fraction) {
        float translationY = getHeight() * fraction;
        setTranslationY(translationY);
        setRotationX(-90 * fraction);
        setPivotY(0);
        setPivotX(getWidth() / 2);
    }

    public void setCubeBack(float fraction) {
        float translationX = getWidth() * fraction;
        setTranslationX(translationX);
        setRotationY(90 * fraction);
        setPivotY(getHeight() / 2);
        setPivotX(getWidth());
    }

    public void setCubeVerticalBack(float fraction) {
        float translationY = getHeight() * fraction;
        setTranslationY(translationY);
        setRotationX(-90 * fraction);
        setPivotX(getWidth() / 2);
        setPivotY(getHeight());
    }

    public void setGlide(float fraction) {
        float translationX = getWidth() * fraction;
        setTranslationX(translationX);
        setRotationY(90 * fraction);
        setPivotX(0);
    }

    public void setGlideBack(float fraction) {
        float translationX = getWidth() * fraction;
        setTranslationX(translationX);
        setRotationY(90 * fraction);
        setPivotX(0);
        setPivotY(getHeight() / 2);
    }

    public void setRotateDown(float fraction) {
        float translationX = getWidth() * fraction;
        setTranslationX(translationX);
        setRotation(20 * fraction);
        setPivotY(getHeight());
        setPivotX(getWidth() / 2);
    }

    public void setRotateUp(float fraction) {
        float translationX = getWidth() * fraction;
        setTranslationX(translationX);
        setRotation(-20 * fraction);
        setPivotY(0);
        setPivotX(getWidth() / 2);
    }

    public void setRotateLeft(float fraction) {
        float translationY = getHeight() * fraction;
        setTranslationY(translationY);
        setRotation(20 * fraction);
        setPivotX(0);
        setPivotY(getHeight() / 2);
    }

    public void setRotateRight(float fraction) {
        float translationY = getHeight() * fraction;
        setTranslationY(translationY);
        setRotation(-20 * fraction);
        setPivotX(getWidth());
        setPivotY(getHeight() / 2);
    }

    public void setYFraction(float fraction) {
        this.getEffectSaver().yFraction = fraction;
        if (getHeight() == 0) {
            if (preDrawListener == null) {
                preDrawListener = new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        getViewTreeObserver().removeOnPreDrawListener(
                                preDrawListener);
                        setYFraction(this.getEffectSaver().yFraction);
                        return true;
                    }
                };
                getViewTreeObserver().addOnPreDrawListener(preDrawListener);
            }
            return;
        }
        float translationY = getHeight() * fraction;
        setTranslationY(translationY);
    }

    public void setXFraction(float fraction) {
        this.getEffectSaver().xFraction = fraction;
        if (getWidth() == 0) {
            if (getEffectSaver().preDrawListener == null) {
                getEffectSaver().preDrawListener = new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        getViewTreeObserver().removeOnPreDrawListener(
                                preDrawListener);
                        setXFraction(this.getEffectSaver().xFraction);
                        return true;
                    }
                };
                getViewTreeObserver().addOnPreDrawListener(preDrawListener);
            }
            return;
        }
        float translationX = getWidth() * fraction;
        setTranslationX(translationX);
    }

    public void setTableHorizontalPivotZero(float fraction) {
        setRotationY(90 * fraction);
        setPivotX(0);
        setPivotY(getHeight() / 2);
    }

    public void setTableHorizontalPivotWidth(float fraction) {
        setRotationY(-90 * fraction);
        setPivotX(getWidth());
        setPivotY(getHeight() / 2);
    }

    public void setTableVerticalPivotZero(float fraction) {
        setRotationX(-90 * fraction);
        setPivotX(getWidth() / 2);
        setPivotY(0);
    }

    public void setTableVerticalPivotHeight(float fraction) {
        setRotationX(90 * fraction);
        setPivotX(getWidth() / 2);
        setPivotY(getHeight());
    }

    public void setZoomFromCornerPivotHG(float fraction) {
        setScaleX(fraction);
        setScaleY(fraction);
        setPivotX(getWidth());
        setPivotY(getHeight());
    }

    public void setZoomFromCornerPivotZero(float fraction) {
        setScaleX(fraction);
        setScaleY(fraction);
        setPivotX(0);
        setPivotY(0);
    }

    public void setZoomFromCornerPivotWidth(float fraction) {
        setScaleX(fraction);
        setScaleY(fraction);
        setPivotX(getWidth());
        setPivotY(0);
    }

    public void setZoomFromCornerPivotHeight(float fraction) {
        setScaleX(fraction);
        setScaleY(fraction);
        setPivotX(0);
        setPivotY(getHeight());
    }

    public void setZoomSlideHorizontal(float fraction) {
        setTranslationX(getWidth() * fraction);
        setPivotX(getWidth() / 2);
        setPivotY(getHeight() / 2);
    }

    public void setZoomSlideVertical(float fraction) {
        setTranslationY(getHeight() * fraction);
        setPivotX(getWidth() / 2);
        setPivotY(getHeight() / 2);
    }
}
