package com.main.xf.barragedemo;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.View;

public class AnimationHelper {
    private static DisplayMetrics outMetrics = new DisplayMetrics();

    /**
     * 创建平移动画
     */
    public static ValueAnimator createTranslateAnim(View view, Activity context,
                                                    int fromX, int toX) {
        context.getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        int width = outMetrics.widthPixels;
        long duration = (long) (Math.abs(toX - fromX) * 1.0f
                / width * 3000);
        PropertyValuesHolder propertyValuesHolder = PropertyValuesHolder
                .ofFloat("translationX", fromX, toX);
        // TranslateAnimation tlAnim = new TranslateAnimation(fromX, toX, 0, 0);
        ValueAnimator valueAnimator = ObjectAnimator.ofPropertyValuesHolder(
                view, propertyValuesHolder).setDuration(duration);
        return valueAnimator;
    }

}