/**
 * @author LuYongXing
 * @date 2015.01.23
 * @filename CartFragmentAdapter.java
 */

package com.camerarental.crc.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.camerarental.crc.fragment.CartCalendarFragment;
import com.camerarental.crc.fragment.CartListFragment;
import com.camerarental.crc.fragment.CartOrderFragment;
import com.camerarental.crc.fragment.CartReportFragment;

public class CartFragmentAdapter extends FragmentPagerAdapter {

    private static final int CART_LIST = 0;
    private static final int CART_CALENDAR = 1;
    private static final int CART_ORDER = 2;
    private static final int CART_REPORT = 3;

    public static final int FRAGMENT_COUNT = 4;

    public CartFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case CART_LIST:
                return CartListFragment.newInstance();

            case CART_CALENDAR:
                return CartCalendarFragment.newInstance();

            case CART_ORDER:
                return CartOrderFragment.newInstance();

            case CART_REPORT:
                return CartReportFragment.newInstance();

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return FRAGMENT_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "";
    }

}
