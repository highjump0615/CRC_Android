/**
 * @author LuYongXing
 * @date 2015.01.23
 * @filename EquipmentFilterListFragment.java
 */

package com.camerarental.crc.fragment;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aplit.dev.listeners.ReadHttpTaskListener;
import com.aplit.dev.tasks.ReadHttpTask;
import com.camerarental.crc.EquipmentActivity;
import com.camerarental.crc.R;
import com.camerarental.crc.adapter.EquipmentAdapter;
import com.camerarental.crc.data.EquipmentData;
import com.camerarental.crc.listener.OnMyItemClickListener;
import com.camerarental.crc.utils.CommonUtils;
import com.camerarental.crc.utils.Config;
import com.camerarental.crc.utils.Constant;
import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class EquipmentFilterListFragment extends Fragment implements
        AdapterView.OnItemClickListener, View.OnClickListener,
        ReadHttpTaskListener, OnMyItemClickListener, View.OnTouchListener {

    private static final String TAG = EquipmentFilterListFragment.class.getSimpleName();

    private static final int SWIPE_MIN_DISTANCE = 10;
    private static final int SWIPE_MAX_OFF_PATH = 100;
    private static final int SWIPE_THRESHOLD_VELOCITY = 20;

    private static final int X_MARGIN = 16;
    private static final int Y_MARGIN = 4;
    private static final int BUTTON_MARGIN = 4;
    private static final int BUTTON_HEIGHT = 32;
    private static final int MINIMUM_HEIGHT = 17;

    private static final int BUTTON_RADIUS = 12;

    private EquipmentActivity mActivity;
    private LayoutInflater mLayoutInflater;

    // Title bar
    private TextView mTextTitle;
    private ImageView mImageSearch;

    // List for Equipment
    private SwipeLayout mSwipeLayout;
    private ImageView mImageArrow;
    private RelativeLayout mLayoutFilter;

    private PullToRefreshListView mPullRefreshListView;
    private EquipmentAdapter mAdapter;

    // GestureDetector
    private GestureDetector gestureDetector;

    // View selection
    private int mSelectedIndex;
    private View mPrevExpandItemView = null;

    private int mScreenWidth;
    private boolean mbInit = true;
    private boolean mbReloading;
    private boolean mbEnable = false;
    protected boolean mIsFront = false;

    // FILTER
    private ArrayList<HashMap> mFilter = new ArrayList<>();
    private TextView mTextFilter = null;
    private String mCurrentInfo = "";

    private ArrayList mFilterButtons = new ArrayList();

    public static EquipmentFilterListFragment newInstance() {
        return new EquipmentFilterListFragment();
    }

    /**
     * GestureDetector to manage swipe in Title Bar
     */
    class MyGestureDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            /*try {
                if (Math.abs(e1.getX() - e2.getX()) > SWIPE_MAX_OFF_PATH)
                    return false;
                // right to left swipe
                if (e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                    if (mSwipeLayout.getOpenStatus() == SwipeLayout.Status.Open)
                        mSwipeLayout.close();
                } else if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                    if (mSwipeLayout.getOpenStatus() == SwipeLayout.Status.Close && mFilter.size() != 0)
                        mSwipeLayout.open();
                }
            } catch (Exception e) {
                // nothing
            }*/
            return false;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            if (!(mSwipeLayout.getOpenStatus() == SwipeLayout.Status.Close && mFilter.size() == 0)) {
                mSwipeLayout.toggle();
            }
            return true;
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mActivity = (EquipmentActivity) activity;
        mbInit = true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mLayoutInflater = inflater;
        mScreenWidth = CommonUtils.pixelToDip(mActivity, CommonUtils.getScreenSize(mActivity).x);

        View view = inflater.inflate(R.layout.fragment_equip_filter, container, false);

        // Title bar
        mTextTitle = (TextView) view.findViewById(R.id.text_title);
        mTextTitle.setText(R.string.camera_rental);

        mImageSearch = (ImageView) view.findViewById(R.id.image_search);
        mImageSearch.setOnClickListener(mActivity);

        // SwipeLayout
        mSwipeLayout = (SwipeLayout) view.findViewById(R.id.swipe_content);

        // Arrow
        mImageArrow = (ImageView) view.findViewById(R.id.image_swipe_arrow);
        mImageArrow.setOnTouchListener(this);

        // Filter
        mLayoutFilter = (RelativeLayout) view.findViewById(R.id.layout_filter);

        // List
        // Search by given filter string.
        mAdapter = new EquipmentAdapter(mActivity, this, EquipmentData.getInstance().mFilteredArray);
        mActivity.mAdapterFilter = mAdapter;

        mPullRefreshListView = (PullToRefreshListView) view.findViewById(R.id.list_equipment);
        mPullRefreshListView.setAdapter(mAdapter);

        mPullRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {

            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                if (mbInit)
                    downloadFilter();
                else
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mPullRefreshListView.onRefreshComplete();
                        }
                    }, 500);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
            }
        });
        mPullRefreshListView.getRefreshableView().setOnTouchListener(this);
        //mPullRefreshListView.setOnItemClickListener(this);

        // Swipe Layout
        mSwipeLayout.setDragEdge(SwipeLayout.DragEdge.Top);
        mSwipeLayout.setDragDistance(SWIPE_MIN_DISTANCE);
        mSwipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
        mSwipeLayout.addSwipeListener(new SimpleSwipeListener() {
            @Override
            public void onOpen(SwipeLayout layout) {
                mImageArrow.setImageResource(R.drawable.up);
            }

            @Override
            public void onClose(SwipeLayout layout) {
                mImageArrow.setImageResource(R.drawable.down);
            }
        });
        mSwipeLayout.addSwipeDenier(new SwipeLayout.SwipeDenier() {
            @Override
            public boolean shouldDenySwipe(MotionEvent motionEvent) {
                Rect rect = new Rect();
                int x = (int) motionEvent.getRawX();
                int y = (int) motionEvent.getRawY();

                mPullRefreshListView.getGlobalVisibleRect(rect);
                return rect.contains(x, y);
            }
        });
        view.findViewById(R.id.layout_surface).addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                Rect rect = new Rect();
                ((ViewGroup) mImageArrow.getParent()).getGlobalVisibleRect(rect);

                rect = new Rect(0, rect.height(), right, bottom - top);
                //Log.e(TAG, "SurfaceView rect = " + rect.toShortString());

                mPullRefreshListView.layout(rect.left, rect.top, rect.right, rect.bottom);
            }
        });

        // Gesture detection
        gestureDetector = new GestureDetector(mActivity, new MyGestureDetector());
        View.OnTouchListener gestureListener = new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        };

        // Header
        View layoutHeader = view.findViewById(R.id.layout_header);
        layoutHeader.setOnTouchListener(gestureListener);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        enableControls(false);
        mPullRefreshListView.setRefreshing(true);
    }

    @Override
    public void onClick(View v) {
        // when filter button was pressed
        /*if (v instanceof RadioButton) {
            onFilterChanged((RadioButton) v);
            return;
        }*/

        switch (v.getId()) {
            case R.id.image_swipe_arrow:
                if (mSwipeLayout.getOpenStatus() == SwipeLayout.Status.Close && mFilter.size() == 0)
                    return;
                mSwipeLayout.toggle();
                break;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // when filter button was pressed
        if (v instanceof RadioButton && event.getAction() == MotionEvent.ACTION_DOWN) {
            onFilterChanged((RadioButton) v);
            return true;
        }

        switch (v.getId()) {
            case R.id.image_swipe_arrow:
                if (event.getAction() == MotionEvent.ACTION_DOWN ||
                        event.getAction() == MotionEvent.ACTION_MOVE) {
                    if (mSwipeLayout.getOpenStatus() == SwipeLayout.Status.Close && mFilter.size() == 0)
                        return false;
                    mSwipeLayout.toggle();
                    return true;
                }
                break;
        }

        if (v.equals(mPullRefreshListView.getRefreshableView())) {
            if (event.getAction() == MotionEvent.ACTION_MOVE) {
                Rect rect = new Rect();
                mImageArrow.getGlobalVisibleRect(rect);

                if (rect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    if (mSwipeLayout.getOpenStatus() == SwipeLayout.Status.Open) {
                        mSwipeLayout.close();
                        return true;
                    }
                }
            }
        }

        return false;
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
                EquipmentData.getInstance().searchWithFilter(searchFilter());
                mAdapter.notifyDataSetChanged();

                mbEnable = true;
            } else {
                mSelectedIndex = -1;
                mPrevExpandItemView = null;

                mbEnable = false;
            }
        }
        //}
    }

    /**
     * Because ViewPager does support only OnClick event to child views
     */
    @Override
    public void onItemClick(View view, int position) {
        onItemClick(mPullRefreshListView.getRefreshableView(), view, position, mAdapter.getItemId(position));
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (!mbEnable) return;

        // hide filter..
        mSwipeLayout.close();

        // Because this is pull to refresh list view, first row is just loading progress view.
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
                mPullRefreshListView.getRefreshableView().setSelection(position + 1);

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
        mPullRefreshListView.getRefreshableView().setSelection(position + 1);
    }

    @Override
    public void onObtainedHttpContent(int apiIdentifier, String result) {
        if (TextUtils.isEmpty(result) || "Connection timeout.".equals(result)) {
            enableControls(true);
            mPullRefreshListView.onRefreshComplete();

            CommonUtils.createErrorAlertDialog(mActivity, "Internet connection fail!",
                    "Try again after network state check.").show();
            return;
        }

        try {
            JSONArray jsonArray = new JSONArray(result);

            switch (apiIdentifier) {
                case Constant.FILTER_DOWNLOAD:
                    processFilter(jsonArray);
                    downloadData();
                    break;

                case Constant.DATA_DOWNLOAD:
                    processData(jsonArray);
                    break;
            }
        } catch (JSONException e) {
            if (Config.DEBUG) e.printStackTrace();
            mPullRefreshListView.onRefreshComplete();
        }
    }


    private void enableControls(boolean enabled) {
        mImageSearch.setEnabled(enabled);
    }

    /**
     * Search Filter
     */
    private void onFilterChanged(RadioButton button) {
        if (button != null)
            mCurrentInfo = (String) button.getTag();
        else
            mCurrentInfo = "";

        buildFilter();

        EquipmentData.getInstance().searchWithFilter(searchFilter());
        mSelectedIndex = -1;

        mbReloading = true;
        //mAdapter.notifyDataSetChanged();
        mActivity.refreshAllList();
    }

    private void setFilters(ArrayList filters) {
        mFilter = filters;

        onFilterChanged(null);
    }

    private ArrayList _searchFilter(ArrayList filtersArray, int depth) {
        ArrayList filter = new ArrayList();

        if (filtersArray == null || filtersArray.size() == 0) {
            return filter;
        }

        String[] strings = mCurrentInfo.split("\\.");

        if (strings.length > depth && !TextUtils.isEmpty(strings[depth]) && TextUtils.isDigitsOnly(strings[depth])) {
            int index = Integer.parseInt(strings[depth]);

            if (index > 0) {
                HashMap params = (HashMap) filtersArray.get(index - 1);
                filter.add(params.get(Constant.kFilterNameKey).toString().toLowerCase());

                ArrayList subFilter = _searchFilter((ArrayList) params.get(Constant.kFilterSubFilterKey), depth + 1);

                if (subFilter.size() > 0)
                    filter.addAll(subFilter);
            }
        }

        return filter;
    }

    private ArrayList searchFilter() {
        return _searchFilter(mFilter, 0);
    }

    /**
     * Create Filter button
     */
    private RadioButton createButton(String title, String info) {
        title = title.toUpperCase();

        RadioButton button = (RadioButton) mLayoutInflater.inflate(R.layout.filter_button, null);
        button.setText(title);
        button.setTag(info);

        if (mCurrentInfo.startsWith(info)) {
            String[] currentKinds = mCurrentInfo.split("\\.");
            String[] kinds = info.split("\\.");

            int depth;
            if (currentKinds.length > kinds.length)
                depth = kinds.length;
            else
                depth = currentKinds.length;

            String composeKey = "";
            for (int i = 0; i < depth - 1; i++) {
                composeKey += (currentKinds[i] + ".");
            }
            composeKey += currentKinds[depth - 1];

            if (composeKey.equals(info))
                button.setChecked(true);
        }

        //button.setOnClickListener(this);
        button.setOnTouchListener(this);

        return button;
    }

    /**
     * Create Filter view
     *
     * @return height of new Filter view
     */
    private ArrayList _buildFilterView(Object filtersArrayObj, int depth, int[] heightRef) {
        boolean bSelected = false;
        int leftMargin = X_MARGIN;

        ArrayList subButtons = new ArrayList();
        ArrayList<HashMap> filtersArray = (ArrayList<HashMap>) filtersArrayObj;

        // create Filter Label..
        if (mTextFilter == null) {
            mTextFilter = new TextView(mActivity);
            mTextFilter.setText(R.string.filter);
            mTextFilter.setTextColor(Color.BLACK);
            mTextFilter.setTextSize(12);
            mTextFilter.setGravity(Gravity.CENTER);

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
            );
            params.height = CommonUtils.dipToPixels(mActivity, BUTTON_HEIGHT/* + BUTTON_MARGIN * 2*/);
            params.leftMargin = CommonUtils.dipToPixels(mActivity, leftMargin);
            params.topMargin = CommonUtils.dipToPixels(mActivity, heightRef[0]);
            mTextFilter.setLayoutParams(params);
        }

        if (depth == 0) {
            mTextFilter.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            leftMargin += CommonUtils.pixelToDip(mActivity, mTextFilter.getMeasuredWidth()) + BUTTON_MARGIN;
        }

        // create buttons..
        if (filtersArray != null) {
            for (HashMap map : filtersArray) {
                String title = (String) map.get(Constant.kFilterDisplayNameKey);
                String info = (String) map.get(Constant.kFilterIDKey);

                RadioButton button = createButton(title, info);

                subButtons.add(button);
                bSelected |= button.isChecked();
            }
        }

        if (subButtons.size() == 0) return null;

        String allInfo = (String) ((RadioButton) subButtons.get(0)).getTag();
        String[] infoArray = allInfo.split("\\.");
        String lastPath = infoArray[infoArray.length - 1];

        StringBuilder builder = new StringBuilder(allInfo);
        builder.setCharAt(allInfo.length() - lastPath.length(), '0');
        allInfo = builder.toString();

        RadioButton allButton = createButton("ALL", allInfo);
        if (!bSelected)
            allButton.setChecked(true);

        subButtons.add(0, allButton);

        // layout buttons
        //heightRef[0] += BUTTON_MARGIN;
        for (Object buttonObj : subButtons) {
            RadioButton button = (RadioButton) buttonObj;
            button.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            int buttonWidth = CommonUtils.pixelToDip(mActivity, button.getMeasuredWidth());

            if ((buttonWidth + leftMargin + X_MARGIN) > mScreenWidth) {
                leftMargin = X_MARGIN;
                heightRef[0] += BUTTON_HEIGHT/* + BUTTON_MARGIN * 2 + BUTTON_MARGIN*/;
            }

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
            );
            params.leftMargin = CommonUtils.dipToPixels(mActivity, leftMargin + BUTTON_MARGIN);
            params.topMargin = CommonUtils.dipToPixels(mActivity, heightRef[0]);
            button.setLayoutParams(params);

            leftMargin += (buttonWidth + BUTTON_MARGIN);
        }
        heightRef[0] += (BUTTON_HEIGHT + BUTTON_MARGIN * 2/* + BUTTON_MARGIN*/);

        // sub buttons..
        infoArray = mCurrentInfo.split("\\.");

        if (mCurrentInfo.length() > 0 && infoArray.length > depth) {
            int index = Integer.parseInt(infoArray[depth]) - 1;

            if (index >= 0 && index < filtersArray.size()) {
                // separate line
                View line = new View(mActivity);
                line.setBackgroundColor(getResources().getColor(R.color.line_color));

                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
                );
                params.width = CommonUtils.dipToPixels(mActivity, mScreenWidth);
                params.height = 1;
                params.topMargin = CommonUtils.dipToPixels(mActivity, heightRef[0] - BUTTON_MARGIN);
                line.setLayoutParams(params);

                HashMap<String, Objects> map = filtersArray.get(index);
                ArrayList subArray = _buildFilterView(map.get(Constant.kFilterSubFilterKey), depth + 1, heightRef);

                // sub buttons..
                if (subArray != null && subArray.size() > 0) {
                    subButtons.addAll(subArray);

                    if (subArray.size() > 0) {
                        subButtons.add(line);
                    }
                }
            }
        }

        if (depth == 0 && mTextFilter != null) {
            subButtons.add(mTextFilter);
        }

        return subButtons;
    }

    private void buildFilter() {
        int heightRef[] = new int[]{Y_MARGIN};

        ArrayList newButtons = _buildFilterView(mFilter, 0, heightRef);

        heightRef[0] -= BUTTON_MARGIN;
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                CommonUtils.dipToPixels(mActivity, heightRef[0]));
        mLayoutFilter.setLayoutParams(params);

        for (Object buttonObj : mFilterButtons) {
            View button = (View) buttonObj;
            ((ViewGroup) button.getParent()).removeView(button);
        }

        mFilterButtons.clear();
        mFilterButtons = newButtons;

        if (mFilterButtons != null) {
            for (Object button : mFilterButtons) {
                mLayoutFilter.addView((View) button);
            }
        }
    }

    /**
     * Process data received from server
     */
    private ArrayList _precessFilter(ArrayList<JSONArray> data, String key, int depth) {
        ArrayList filters = new ArrayList();
        ArrayList<JSONArray> subFilters = new ArrayList<>();
        ArrayList<JSONArray> matchFilters = new ArrayList<>();
        String[] kinds;

        for (JSONArray subFilter : data) {
            try {
                if (subFilter.length() != 3) continue;

                String subKey = subFilter.getString(0);
                if (TextUtils.isEmpty(subKey)) continue;

                kinds = subKey.split("\\.");
                String composeKey = "";
                for (int i = 0; i < depth; i++) {
                    composeKey += (kinds[i] + ".");
                }
                composeKey += kinds[depth];

                if (kinds.length < (depth + 1) || (!TextUtils.isEmpty(key) && !subKey.startsWith(key + ".")) && !composeKey.equals(key))
                    continue;

                if (kinds.length > (depth + 1))
                    subFilters.add(subFilter);
                else
                    matchFilters.add(subFilter);
            } catch (JSONException e) {
                if (Config.DEBUG) e.printStackTrace();
            }
        }

        for (JSONArray subArray : matchFilters) {
            try {
                String subKey = subArray.getString(0);
                if (TextUtils.isEmpty(subKey)) continue;

                if (!TextUtils.isEmpty(key) && !subKey.startsWith(key))
                    continue;

                HashMap<String, Object> params = new HashMap<>();
                params.put(Constant.kFilterIDKey, subArray.getString(0));
                params.put(Constant.kFilterDisplayNameKey, subArray.getString(1));
                params.put(Constant.kFilterNameKey, subArray.getString(2));

                if (subFilters.size() > 0) {
                    ArrayList<HashMap<String, Object>> subFilter = _precessFilter(subFilters, subKey, depth + 1);

                    if (subFilter.size() > 0)
                        params.put(Constant.kFilterSubFilterKey, subFilter);
                }

                filters.add(params);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return filters;
    }

    private void processFilter(JSONArray data) {
        ArrayList<JSONArray> dataArray = new ArrayList<>();
        int size = data.length();

        for (int i = 0; i < size; i++) {
            try {
                dataArray.add(data.getJSONArray(i));
            } catch (JSONException e) {
                if (Config.DEBUG) e.printStackTrace();
            }
        }

        setFilters(_precessFilter(dataArray, null, 0));
        buildFilter();
    }

    private void processData(JSONArray data) {
        EquipmentData.getInstance().initWithData(data);

        mbReloading = true;
        mAdapter.notifyDataSetChanged();

        //any UI refresh
        enableControls(true);

        mPullRefreshListView.onRefreshComplete();
        mbInit = false;
        mbEnable = true;
    }

    /**
     * Download filter and data from internet..
     */
    private void downloadFilter() {
        new ReadHttpTask(mActivity, Constant.FILTER_DOWNLOAD, this, null).execute(Constant.EQUIPMENT_FILTER_URL);
    }

    private void downloadData() {
        new ReadHttpTask(mActivity, Constant.DATA_DOWNLOAD, this, null).execute(Constant.EQUIPMENT_DOWNLOAD_URL);
    }

}
