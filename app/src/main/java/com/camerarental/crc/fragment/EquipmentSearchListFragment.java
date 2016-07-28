/**
 * @author LuYongXing
 * @date 2015.01.23
 * @filename EquipmentSearchListFragment.java
 */

package com.camerarental.crc.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.camerarental.crc.EquipmentActivity;
import com.camerarental.crc.R;
import com.camerarental.crc.adapter.EquipmentAdapter;
import com.camerarental.crc.data.EquipmentData;
import com.camerarental.crc.listener.OnMyItemClickListener;
import com.camerarental.crc.utils.Constant;
import com.camerarental.crc.widget.SearchPanel;

import java.util.HashMap;

public class EquipmentSearchListFragment extends ListFragment implements
        AdapterView.OnItemClickListener, SearchPanel.SearchListener, OnMyItemClickListener {

    private EquipmentActivity mActivity;

    // Title bar
    private SearchPanel mSearchPanel;

    // List for Equipment
    private EquipmentAdapter mAdapter;
    private ListView mListEquipment;

    // View selection
    private int mSelectedIndex;
    private View mPrevExpandItemView = null;

    private boolean mbEnable;
    protected boolean mIsFront = false;


    public static EquipmentSearchListFragment newInstance() {
        return new EquipmentSearchListFragment();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mActivity = (EquipmentActivity) activity;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        mIsFront = isVisibleToUser;

        // Make sure that we are currently visible
        //if (!this.isVisible()) {
        // If we are becoming invisible, then...
        if (mActivity != null && mAdapter != null) {
            if (isVisibleToUser) {
                mbEnable = true;

                EquipmentData.getInstance().searchWithString(mSearchPanel.getSearchEditText().getText().toString());
                mAdapter.notifyDataSetChanged();
            } else {
                mSelectedIndex = -1;
                mPrevExpandItemView = null;

                mbEnable = false;
                mSearchPanel.hideKeyboard();
            }
        }
        //}
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_equip_search, container, false);

        view.findViewById(R.id.image_back).setOnClickListener(mActivity);

        mSearchPanel = (SearchPanel) view.findViewById(R.id.search_panel);
        mSearchPanel.setSearchListener(this);

        mAdapter = new EquipmentAdapter(mActivity, this, EquipmentData.getInstance().mFilteredArray);
        mActivity.mAdapterSearch = mAdapter;

        mListEquipment = (ListView) view.findViewById(android.R.id.list);
        mListEquipment.setAdapter(mAdapter);
        //mListEquipment.setOnItemClickListener(this);

        return view;
    }

    /**
     * Because ViewPager does support only OnClick event to child views
     */
    @Override
    public void onItemClick(View view, int position) {
        onItemClick(mListEquipment, view, position, mAdapter.getItemId(position));
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (!mbEnable) return;

        // hide ime
        mSearchPanel.hideKeyboard();

        HashMap equipmentMap = (HashMap) mAdapter.getItem(position);

        // process..
        if (mSelectedIndex != position && mSelectedIndex >= 0) {
            HashMap prevMap = (HashMap) mAdapter.getItem(mSelectedIndex);
            int state = (int) prevMap.get(Constant.kEquipmentStateKey);

            if (state == EquipmentAdapter.EquipmentCellStateExpand ||
                    state == EquipmentAdapter.EquipmentCellStateRequestExpand) {
                state = EquipmentAdapter.EquipmentCellStateRequestCollapse;

                if (mPrevExpandItemView != null) {
                    EquipmentAdapter.ViewHolder viewHolder = (EquipmentAdapter.ViewHolder) mPrevExpandItemView.getTag();
                    mAdapter.zoomThumbFromImage(viewHolder, mSelectedIndex);
                }
            }

            EquipmentData.getInstance().setEquipmentState(mSelectedIndex, state);
        }

        mSelectedIndex = position;
        mPrevExpandItemView = view;

        int state = (int) equipmentMap.get(Constant.kEquipmentStateKey);
        EquipmentAdapter.ViewHolder viewHolder = (EquipmentAdapter.ViewHolder) view.getTag();

        switch (state) {
            case EquipmentAdapter.EquipmentCellStateReload:
            case EquipmentAdapter.EquipmentCellStateNone:
            case EquipmentAdapter.EquipmentCellStateNormal:
            case EquipmentAdapter.EquipmentCellStateRequestCollapse:
                mListEquipment.setSelection(position);

                state = EquipmentAdapter.EquipmentCellStateRequestExpand;
                EquipmentData.getInstance().setEquipmentState(position, state);
                mAdapter.zoomImageFromThumb(viewHolder, position);
                return;

            case EquipmentAdapter.EquipmentCellStateExpand:
            case EquipmentAdapter.EquipmentCellStateRequestExpand:
                state = EquipmentAdapter.EquipmentCellStateRequestCollapse;
                EquipmentData.getInstance().setEquipmentState(position, state);
                mAdapter.zoomThumbFromImage(viewHolder, position);
                return;

            case EquipmentAdapter.EquipmentCellStateSelected:
                state = EquipmentAdapter.EquipmentCellStateNormal;
                mActivity.removeFromCart(position);
                break;
        }

        EquipmentData.getInstance().setEquipmentState(position, state);

        mAdapter.notifyDataSetChanged();
        mListEquipment.setSelection(position);
    }

    @Override
    public void onAutoSuggestion(String query) {
        mSelectedIndex = -1;
        mPrevExpandItemView = null;

        EquipmentData.getInstance().searchWithString(query);
        //mAdapter.notifyDataSetChanged();
        mActivity.refreshAllList();
    }

    @Override
    public void onClickSearchResult(String query) {
    }

    @Override
    public void onItemClick(int position) {
    }

    @Override
    public void onClear() {
        onAutoSuggestion("");
    }

    @Override
    public void onSearchAll() {
    }

}
