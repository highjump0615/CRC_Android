package com.camerarental.crc.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class CustomViewPager extends ViewPager {

    private boolean isPagingEnabled;
    private boolean canGoLeft = true;

    private float lastX = 0;
    private int mDuration = 500;


    public CustomViewPager(Context context) {
        super(context, null);
    }

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        // If hardware acceleration is enabled, you should also remove
        // clipping on the pager for its children.
        setClipChildren(false);
        // to avoid fade effect at the end of the page
        setOverScrollMode(View.OVER_SCROLL_NEVER);

        this.isPagingEnabled = true;
    }

    /*@Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (this.isPagingEnabled) {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    lastX = ev.getX();
                    return super.onTouchEvent(ev);

                case MotionEvent.ACTION_MOVE:
                    if (!canGoLeft && lastX < ev.getX()) {
                        lastX = ev.getX();
                        return false;
                    }
                    break;
            }

            lastX = ev.getX();
            return super.onTouchEvent(ev);
        }

        return false;
    }*/

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (this.isPagingEnabled) {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    lastX = ev.getX();
                    return super.onInterceptTouchEvent(ev);

                case MotionEvent.ACTION_MOVE:
                    if (!canGoLeft && lastX < ev.getX()) {
                        lastX = ev.getX();
                        return false;
                    }
                    break;
            }

            lastX = ev.getX();
            return super.onInterceptTouchEvent(ev);
        }

        return false;
    }

    public void setPagingEnabled(boolean isPagingEnabled) {
        this.isPagingEnabled = isPagingEnabled;
    }

    public void setCanGoLeft(boolean canGoLeft) {
        this.canGoLeft = canGoLeft;
    }

}
