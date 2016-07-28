/**
 * @author LuYongXing
 * @date 2015.01.14
 * @filename CartActivity.java
 */

package com.camerarental.crc;

import android.support.v4.view.ViewPager;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import com.camerarental.crc.adapter.CartFragmentAdapter;
import com.camerarental.crc.base.BaseActivity;
import com.camerarental.crc.data.EquipmentData;
import com.camerarental.crc.listener.OnCartChangeListener;
import com.camerarental.crc.listener.OnSubmitListener;
import com.camerarental.crc.listener.OnTabChangeListener;
import com.camerarental.crc.utils.CommonUtils;
import com.camerarental.crc.widget.CustomViewPager;
import com.camerarental.crc.widget.FixedSpeedScroller;

import java.lang.reflect.Field;

public class CartActivity extends BaseActivity implements OnCartChangeListener {

    private static final String TAG = CartActivity.class.getSimpleName();

    public static final int CartViewStyleEdit = 0;
    public static final int CartViewStyleSelectDate = 1;
    public static final int CartViewStyleSubmit = 2;
    public static final int CartViewStyleDone = 3;

    private View mLayoutEmptyBasket;
    private View mLayoutButtonBar;

    private TextView mTextPrev;
    private TextView mTextNext;

    public CustomViewPager mPager;

    private int mCurPage = 0;

    public boolean mbSendingMail = false;

    public Time mStartDay = null;
    public Time mEndDay = null;

    private OnSubmitListener mSubmitListener;
    private OnTabChangeListener mTabChangeListener;


    @Override
    protected void initViews() {
        super.initViews();

        Log.d(TAG, "CartActivity was created!");

        mTextTitle.setText(R.string.rental_cart);
        mImageSearch.setVisibility(View.GONE);

        mLayoutInflater.inflate(R.layout.layout_cart, mLayoutContainer);

        mLayoutEmptyBasket = mLayoutContainer.findViewById(R.id.layout_empty);
        mLayoutEmptyBasket.setVisibility(View.INVISIBLE);

        mLayoutButtonBar = mLayoutContainer.findViewById(R.id.layout_button_bar);
        mTextPrev = (TextView) mLayoutContainer.findViewById(R.id.text_prev);
        mTextPrev.setOnClickListener(this);
        mTextNext = (TextView) mLayoutContainer.findViewById(R.id.text_next);
        mTextNext.setOnClickListener(this);

        CartFragmentAdapter adapter = new CartFragmentAdapter(getSupportFragmentManager());
        mPager = (CustomViewPager) mLayoutContainer.findViewById(R.id.pager);
        mPager.setPagingEnabled(false);
        mPager.setAdapter(adapter);

        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                mCurPage = position;

                switch (mCurPage) {
                    case CartViewStyleEdit:
                        mTextNext.setText(R.string.next);
                        break;

                    case CartViewStyleDone:
                        mTextPrev.setVisibility(View.GONE);
                        mTextNext.setText(R.string.done);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        mCurPage = 0;

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
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        switch (v.getId()) {
            case R.id.text_prev:
                mCurPage--;
                if (mCurPage < CartViewStyleEdit) mCurPage = CartViewStyleEdit;

                if (mCurPage == CartViewStyleEdit) {
                    mTextPrev.setVisibility(View.GONE);
                }

                mTextNext.setText(R.string.next);
                mPager.setCurrentItem(mCurPage, true);
                break;

            case R.id.text_next:
                switch (mCurPage) {
                    case CartViewStyleSelectDate:
                        if (mStartDay == null) {
                            CommonUtils.createErrorAlertDialog(this, "", "Please select the date(s) required.").show();
                            return;
                        }
                        break;

                    case CartViewStyleSubmit:
                        if (mSubmitListener != null)
                            mSubmitListener.onSubmit();
                        return;

                    case CartViewStyleDone:
                        if (mTabChangeListener != null) {
                            mTabChangeListener.onTabChanged(0);
                        }
                        break;
                }

                mCurPage++;
                if (mCurPage > CartFragmentAdapter.FRAGMENT_COUNT - 1)
                    mCurPage = CartFragmentAdapter.FRAGMENT_COUNT - 1;

                if (mCurPage < CartFragmentAdapter.FRAGMENT_COUNT - 1) {
                    mTextPrev.setVisibility(View.VISIBLE);
                } else {
                    mTextNext.setText(R.string.submit);
                }

                mPager.setCurrentItem(mCurPage, true);
                break;
        }
    }

    @Override
    public void onCartChanged(int totalCount) {
        if (mCurPage > 0) {
            mPager.setCurrentItem(0, false);
            mCurPage = 0;
            mTextNext.setText(R.string.next);
            mTextPrev.setVisibility(View.GONE);
        }

        if (EquipmentData.getInstance().cartCount() == 0) {
            mPager.setVisibility(View.GONE);
            mLayoutButtonBar.setVisibility(View.GONE);
            mLayoutEmptyBasket.setVisibility(View.VISIBLE);
        } else {
            mPager.setVisibility(View.VISIBLE);
            mLayoutButtonBar.setVisibility(View.VISIBLE);
            mLayoutEmptyBasket.setVisibility(View.GONE);
        }
    }

    public void setSubmitListener(OnSubmitListener listener) {
        mSubmitListener = listener;
    }

    public void setTabChangeListener(OnTabChangeListener listener) {
        mTabChangeListener = listener;
    }

}
