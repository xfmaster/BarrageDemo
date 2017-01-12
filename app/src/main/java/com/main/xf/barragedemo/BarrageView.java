package com.main.xf.barragedemo;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.animation.LinearInterpolator;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by xf on 2017/1/12 0012.
 */

public class BarrageView extends RelativeLayout {
    private int color = Color.parseColor("#eeeeee");
    private int size = 16;
    private int validHeightSpace;
    private int linesCount = 7;
    private Set<Integer> existMarginValues = new HashSet<Integer>();
    private OnBarrageInterface mbarrageInterface;
    private int index = 0;
    private final static int START_BARRAGE = 1;
    private boolean isRepeat;
    private List<ValueAnimator> list = new ArrayList<>();

    public BarrageView(Context context) {
        super(context);
    }

    public BarrageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BarrageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private TextView getItemView(String content) {
        TextView textView = new TextView(getContext());
        textView.setPadding(10, 0, 0, 0);
        textView.setTextSize(size);
        textView.setLines(1);
        if (content != null)
            textView.setText(content.trim());
        textView.setTextColor(color);
        return textView;
    }

    public void setBarrageTextColor(int color) {
        this.color = color;
    }

    public void setBarrageTextSize(int size) {
        this.size = size;
    }

    private void createBarrage(String content) {
        int leftMargin = this.getRight() - this.getLeft()
                - this.getPaddingLeft();
        final TextView item = getItemView(content);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        // 计算本条弹幕的topMargin(随机值，但是与屏幕中已有的不重复)
        int verticalMargin = getRandomTopMargin();
        item.setTag(verticalMargin);
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        params.topMargin = verticalMargin;
        ValueAnimator anim = AnimationHelper.createTranslateAnim(item, (Activity) getContext(),
                leftMargin, -this.getMeasuredWidth());
        item.setLayoutParams(params);
//        if (isRepeat)
        anim.setRepeatCount(Integer.MAX_VALUE);
//        else {
//            if (mbarrageInterface != null) {
//                if (mbarrageInterface.getBarrageCount() % linesCount == 0) {
//                    anim.setRepeatCount(mbarrageInterface.getBarrageCount() / linesCount);
//                } else {
//                    anim.setRepeatCount(mbarrageInterface.getBarrageCount() / linesCount + 1);
//                }
//            }
//        }
        anim.setInterpolator(new LinearInterpolator());
        anim.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator arg0) {

            }

            @Override
            public void onAnimationRepeat(Animator arg0) {
//                if (isRepeat) {
                index++;
                if (mbarrageInterface.getBarrageCount() == 0 || index == mbarrageInterface.getBarrageCount() || index > mbarrageInterface.getBarrageCount()) {
                    index = 0;
                }
//                } else {
//                    if (index == mbarrageInterface.getBarrageCount()) {
//                        return;
//                    }
//                }
                if (mbarrageInterface.getBarrageText(index) != null) {
                    item.setText(mbarrageInterface.getBarrageText(index));
                }
            }

            @Override
            public void onAnimationEnd(Animator arg0) {

            }

            @Override
            public void onAnimationCancel(Animator arg0) {

            }
        });
        list.add(anim);
        anim.start();
        this.addView(item);
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case START_BARRAGE:
                    createBarrage("");
                    break;
            }
            super.handleMessage(msg);
        }
    };

    // 每2s自动添加一条弹幕
    private class CreateTanmuThread implements Runnable {
        @Override
        public void run() {
            if (mbarrageInterface != null)
//                if (mbarrageInterface.getBarrageCount() < linesCount) {
//                    for (int i = 0; i < mbarrageInterface.getBarrageCount(); i++) {
//                        mHandler.obtainMessage(START_BARRAGE, i, 0).sendToTarget();
//                    }
//                } else {
                for (int i = 0; i < linesCount; i++) {
                    mHandler.obtainMessage(START_BARRAGE, i, 0).sendToTarget();
                    SystemClock.sleep(1000);
                }
//                }
        }
    }

    public void setMbarrageInterface(OnBarrageInterface mbarrageInterface) {
        this.mbarrageInterface = mbarrageInterface;
    }

    public void startBarrage() {
        if (mbarrageInterface != null) {
            new Thread(new CreateTanmuThread()).start();
        } else {
            throw new RuntimeException("请注册OnBarrageInterface监听");
        }

    }

    /**
     * 设置弹幕是否从新播放
     *
     * @param isRepeat
     */
    public void setIsRepeat(boolean isRepeat) {
        this.isRepeat = isRepeat;
    }

    public void onPause() {
        for (ValueAnimator valueAnimator : list) {
            valueAnimator.pause();
        }
    }

    public void onDestory() {
        for (ValueAnimator valueAnimator : list) {
            valueAnimator.removeAllListeners();
            valueAnimator.cancel();
        }
        list.clear();
    }

    private int getRandomTopMargin() {
        // 计算用于弹幕显示的空间高度
        if (validHeightSpace == 0) {
            validHeightSpace = getMeasuredHeight();
        }
        // 计算可用的行数
        if (linesCount == 0) {
            linesCount = (int) (validHeightSpace
                    / size
                    * (1 + 1.5f));
            if (linesCount == 0) {
                throw new RuntimeException("Not enough space to show text.");
            }
        }
        // 检查重叠
        while (true) {
            int randomIndex = (int) (Math.random() * linesCount);
            int marginValue = randomIndex * (validHeightSpace / linesCount);

            if (!existMarginValues.contains(marginValue)) {
                existMarginValues.add(marginValue);
                return marginValue;
            }
        }
    }
}
