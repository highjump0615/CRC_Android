/**
 * @author LuYongXing
 * @date 2015.01.23
 * @filename EquipmentFragmentAdapter.java
 */

package com.camerarental.crc.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.camerarental.crc.fragment.EquipmentFilterListFragment;
import com.camerarental.crc.fragment.EquipmentSearchListFragment;

public class EquipmentFragmentAdapter extends FragmentPagerAdapter {

    private static final int STRING_SEARCH = 0;
    private static final int FILTER_SEARCH = 1;

    public static final int FRAGMENT_COUNT = 2;

    public EquipmentFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case STRING_SEARCH:
                return EquipmentSearchListFragment.newInstance();

            case FILTER_SEARCH:
                return EquipmentFilterListFragment.newInstance();

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
