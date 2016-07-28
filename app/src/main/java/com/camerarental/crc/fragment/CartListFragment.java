/**
 * @author LuYongXing
 * @date 2015.01.23
 * @filename CartListFragment.java
 */

package com.camerarental.crc.fragment;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.camerarental.crc.R;
import com.camerarental.crc.adapter.CartAdapter;
import com.camerarental.crc.data.EquipmentData;
import com.daimajia.swipe.implments.SwipeItemMangerImpl;

public class CartListFragment extends ListFragment {

    private CartAdapter mAdapter;
    private ListView mListCart;

    public static CartListFragment newInstance() {
        return new CartListFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart_list, container, false);

        mAdapter = new CartAdapter(getActivity(), EquipmentData.getInstance().mCartArray);
        mAdapter.setMode(SwipeItemMangerImpl.Mode.Single);

        mListCart = (ListView) view.findViewById(android.R.id.list);
        mListCart.setAdapter(mAdapter);
        //mListCart.setOnItemClickListener(null);

        /*mListCart.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getActivity(), "Click", Toast.LENGTH_SHORT).show();
            }
        });
        mListCart.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.e("ListView", "OnTouch");
                return false;
            }
        });
        mListCart.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getActivity(), "OnItemLongClickListener", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        mListCart.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                Log.e("ListView","onScrollStateChanged");
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });

        mListCart.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.e("ListView", "onItemSelected:" + position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.e("ListView", "onNothingSelected:");
            }
        });*/

        return view;
    }

}
