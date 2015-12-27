package com.cocolover2.lis.view;

import android.content.Context;
import android.graphics.Rect;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;

public class BounceBackViewPager extends ViewPager {

    private int currentPosition = 0;
    private Rect mRect = new Rect();//用来记录初始位置
    private boolean handleDefault = true;
    private float preX = 0f;
    private static final float RATIO = 0.5f;//摩擦系数
    private static final float SCROLL_WIDTH = 10f;//滑动阈值

    public BounceBackViewPager(Context context) {
        super(context);
    }

    public BounceBackViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                preX = ev.getX();//记录起点
                currentPosition = getCurrentItem();
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_MOVE:
                if (actionMove(ev)) return true;
                break;
            case MotionEvent.ACTION_UP:
                onTouchActionUp();
                break;
        }
        return super.onTouchEvent(ev);
    }

    private boolean actionMove(MotionEvent ev) {
        if (getAdapter().getCount() == 1) {
            float nowX = ev.getX();
            float offset = nowX - preX;
            preX = nowX;
            if (Math.abs(offset) > SCROLL_WIDTH) {//手指滑动的距离大于设定值
                whetherConditionIsRight(offset);
            } else if (!handleDefault) {//这种情况是已经出现缓冲区域了，手指慢慢恢复的情况
                if (getLeft() + (int) (offset * RATIO) != mRect.left) {
                    layout(getLeft() + (int) (offset * RATIO), getTop(), getRight() + (int) (offset * RATIO), getBottom());
                }
            }
        } else if ((currentPosition == 0 || currentPosition == getAdapter().getCount() - 1)) {
            float nowX = ev.getX();
            float offset = nowX - preX;
            preX = nowX;

            if (currentPosition == 0) {
                if (offset > SCROLL_WIDTH) {//手指滑动的距离大于设定值
                    whetherConditionIsRight(offset);
                } else if (!handleDefault) {//这种情况是已经出现缓冲区域了，手指慢慢恢复的情况
                    if (getLeft() + (int) (offset * RATIO) >= mRect.left) {
                        layout(getLeft() + (int) (offset * RATIO), getTop(), getRight() + (int) (offset * RATIO), getBottom());
                    }
                }
            } else {
                if (offset < -SCROLL_WIDTH) {
                    whetherConditionIsRight(offset);
                } else if (!handleDefault) {
                    if (getRight() + (int) (offset * RATIO) <= mRect.right) {
                        layout(getLeft() + (int) (offset * RATIO), getTop(), getRight() + (int) (offset * RATIO), getBottom());
                    }
                }
            }
        } else {
            handleDefault = true;
        }

        if (!handleDefault) {
            return true;
        }
        return false;
    }

    private void whetherConditionIsRight(float offset) {
        if (mRect.isEmpty()) {
            mRect.set(getLeft(), getTop(), getRight(), getBottom());
        }
        handleDefault = false;
        layout(getLeft() + (int) (offset * RATIO), getTop(), getRight() + (int) (offset * RATIO), getBottom());
    }

    private void onTouchActionUp() {
        if (!mRect.isEmpty()) {
            recoveryPosition();
        }
    }

    private void recoveryPosition() {
        TranslateAnimation ta = new TranslateAnimation(getLeft(), mRect.left, 0, 0);
        ta.setDuration(150);
        ta.setInterpolator(new DecelerateInterpolator());
        startAnimation(ta);
        layout(mRect.left, mRect.top, mRect.right, mRect.bottom);
        mRect.setEmpty();
        handleDefault = true;
    }

}