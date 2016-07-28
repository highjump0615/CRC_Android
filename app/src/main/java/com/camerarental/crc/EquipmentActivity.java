/**
 * @author LuYongXing
 * @date 2015.01.14
 * @filename EquipmentActivity.java
 */

package com.camerarental.crc;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.camerarental.crc.adapter.EquipmentAdapter;
import com.camerarental.crc.adapter.EquipmentFragmentAdapter;
import com.camerarental.crc.data.EquipmentData;
import com.camerarental.crc.listener.OnOrderChangeListener;
import com.camerarental.crc.widget.CustomViewPager;
import com.camerarental.crc.widget.FixedSpeedScroller;

import java.lang.reflect.Field;
import java.util.HashMap;

public class EquipmentActivity extends FragmentActivity implements
        View.OnClickListener, OnOrderChangeListener {

    private static final String TAG = EquipmentActivity.class.getSimpleName();

    private static final int STRING_SEARCH = 0;
    private static final int FILTER_SEARCH = 1;

    private CustomViewPager mPager;

    public EquipmentAdapter mAdapterFilter = null;
    public EquipmentAdapter mAdapterSearch = null;

    /**
     * FragmentActivity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equipment);

        Log.d(TAG, "EquipmentActivity was created!");

        EquipmentFragmentAdapter mAdapter = new EquipmentFragmentAdapter(getSupportFragmentManager());
        mPager = (CustomViewPager) findViewById(R.id.pager);
        mPager.setPagingEnabled(false);
        mPager.setAdapter(mAdapter);

        // Change transition time of ViewPager
        try {
            Field mScroller;
            DecelerateInterpolator sInterpolator = new DecelerateInterpolator();

            mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            FixedSpeedScroller scroller = new FixedSpeedScroller(mPager.getContext(), sInterpolator);
            // scroller.setFixedDuration(5000);
            mScroller.set(mPager, scroller);
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
            Log.e(TAG, e.getMessage());
        }

        mPager.setCurrentItem(FILTER_SEARCH, false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_search:
                mPager.setCurrentItem(STRING_SEARCH, true);
                break;

            case R.id.image_back:
                mPager.setCurrentItem(FILTER_SEARCH, true);
                break;
        }
    }

    @Override
    public void addToCart(int index) {
        EquipmentData.getInstance().addToCartWithIndex(index);
    }

    @Override
    public void removeFromCart(int index) {
        HashMap map = EquipmentData.getInstance().equipmentDataWithIndex(index);
        EquipmentData.getInstance().removeFromCart(map);
    }

    @Override
    public void clearCart(int index) {
        //mPullRefreshListView.getRefreshableView().setSelection(0);
    }

    public void refreshAllList() {
        if (mAdapterFilter != null) mAdapterFilter.notifyDataSetChanged();
        if (mAdapterSearch != null) mAdapterSearch.notifyDataSetChanged();
    }

    /*
     *
     * Hold a reference to the current animator, so that it can be canceled mid-way.
     *//*
    private Animator mCurrentAnimator;

    *//**
     * The system "short" animation time duration, in milliseconds. This duration is ideal for
     * subtle animations or animations that occur very frequently.
     *//*
    private int mShortAnimationDuration;


    // Retrieve and cache the system's default "short" animation time.
    mShortAnimationDuration = getResources().getInteger(android.R.integer.config_longAnimTime);


    private void showSearchFrame() {
        // If there's an animation in progress, cancel it immediately and proceed with this one.
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }

        mbEnable = false;

        float screenWidthInPixel = CommonUtils.dipToPixels(this, mScreenWidth);

        Rect imageSearchBounds = new Rect();
        Rect textTitleBounds = new Rect();
        Rect swipeLayoutBounds = new Rect();

        Rect imageBackBounds = new Rect();
        Rect searchPanelBounds = new Rect();

        mImageSearch.getGlobalVisibleRect(imageSearchBounds);
        mTextTitle.getGlobalVisibleRect(textTitleBounds);
        mSwipeLayout.getGlobalVisibleRect(swipeLayoutBounds);

        mImageBack.getGlobalVisibleRect(imageBackBounds);
        mSearchPanel.getGlobalVisibleRect(searchPanelBounds);

        imageBackBounds.left = -CommonUtils.dipToPixels(this, mScreenWidth - 4);
        searchPanelBounds.left = -CommonUtils.dipToPixels(this, mScreenWidth - 44);

        mImageBack.setVisibility(View.VISIBLE);
        mSearchPanel.setVisibility(View.VISIBLE);
        mListSearch.setVisibility(View.VISIBLE);

        AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(mImageSearch, View.X, imageSearchBounds.left, imageSearchBounds.left + screenWidthInPixel))
                .with(ObjectAnimator.ofFloat(mTextTitle, View.X, textTitleBounds.left, textTitleBounds.left + screenWidthInPixel))
                .with(ObjectAnimator.ofFloat(mSwipeLayout, View.X, 0, screenWidthInPixel))
                .with(ObjectAnimator.ofFloat(mImageBack, View.X, imageBackBounds.left, imageBackBounds.left + screenWidthInPixel))
                .with(ObjectAnimator.ofFloat(mSearchPanel, View.X, searchPanelBounds.left, searchPanelBounds.left + screenWidthInPixel))
                .with(ObjectAnimator.ofFloat(mListSearch, View.X, -screenWidthInPixel, 0));
        set.setDuration(mShortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mCurrentAnimator = null;
                mbEnable = true;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mCurrentAnimator = null;
                mbEnable = true;
            }
        });
        set.start();
        mCurrentAnimator = set;
    }

    private void showFilterFrame() {
        // If there's an animation in progress, cancel it immediately and proceed with this one.
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }

        mbEnable = false;
        float screenWidthInPixel = CommonUtils.dipToPixels(this, mScreenWidth);

        Rect imageSearchBounds = new Rect();
        Rect textTitleBounds = new Rect();
        Rect swipeLayoutBounds = new Rect();

        Rect imageBackBounds = new Rect();
        Rect searchPanelBounds = new Rect();
        Rect listSearchBounds = new Rect();

        mImageSearch.getGlobalVisibleRect(imageSearchBounds);
        mTextTitle.getGlobalVisibleRect(textTitleBounds);
        mSwipeLayout.getGlobalVisibleRect(swipeLayoutBounds);

        imageSearchBounds.left = CommonUtils.dipToPixels(this, 4);
        textTitleBounds.left = (int) ((screenWidthInPixel - textTitleBounds.width()) / 2);

        mImageBack.getGlobalVisibleRect(imageBackBounds);
        mSearchPanel.getGlobalVisibleRect(searchPanelBounds);
        mListSearch.getGlobalVisibleRect(listSearchBounds);

        AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(mImageSearch, View.X, imageSearchBounds.left + screenWidthInPixel, imageSearchBounds.left))
                .with(ObjectAnimator.ofFloat(mTextTitle, View.X, textTitleBounds.left + screenWidthInPixel, textTitleBounds.left))
                .with(ObjectAnimator.ofFloat(mSwipeLayout, View.X, screenWidthInPixel, 0))
                .with(ObjectAnimator.ofFloat(mImageBack, View.X, imageBackBounds.left, imageBackBounds.left - screenWidthInPixel))
                .with(ObjectAnimator.ofFloat(mSearchPanel, View.X, searchPanelBounds.left, searchPanelBounds.left - screenWidthInPixel))
                .with(ObjectAnimator.ofFloat(mListSearch, View.X, 0, -screenWidthInPixel));
        set.setDuration(mShortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mCurrentAnimator = null;
                mbEnable = true;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mCurrentAnimator = null;
                mbEnable = true;
            }
        });
        set.start();
        mCurrentAnimator = set;
    }*/

}
