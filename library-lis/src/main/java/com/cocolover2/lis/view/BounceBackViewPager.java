package com.cocolover2.lis.view;

import android.content.Context;
import android.graphics.Rect;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;

public class BounceBackViewPager extends ViewPager {

    private Rect mRect = new Rect();//用来记录初始位置
    private boolean handleDefault = true;//处理Viewpager默认情况标志
    private float preX = 0f;
    private static final float RATIO = 0.8f;//摩擦系数


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
        final float nowX = ev.getX();
        //偏移量
        final float offset = preX - nowX;
        preX = nowX;
        if (getAdapter().getCount() > 0) {
            if (handleDefault) {//手指滑动的距离大于设定值
                whetherIsCollided(offset);
            } else {//这种情况是已经出现缓冲区域了，手指慢慢恢复的情况
                scrollBy((int) (offset * RATIO), 0);
            }
        } else {
            handleDefault = true;
        }
        return !handleDefault;
    }

    private void whetherIsCollided(float offset) {
        if (mRect.isEmpty()) {
            mRect.set(getLeft(), getTop(), getRight(), getBottom());
        }
        handleDefault = false;
        scrollBy((int) (offset * RATIO), 0);
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