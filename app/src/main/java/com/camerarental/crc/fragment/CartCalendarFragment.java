/**
 * @author LuYongXing
 * @date 2015.01.23
 * @filename CartCalendarFragment.java
 */

package com.camerarental.crc.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.andexert.calendarlistview.library.DatePickerController;
import com.andexert.calendarlistview.library.DayPickerView;
import com.andexert.calendarlistview.library.SimpleMonthAdapter;
import com.camerarental.crc.CartActivity;
import com.camerarental.crc.R;
import com.camerarental.crc.utils.Config;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Locale;

public class CartCalendarFragment extends Fragment implements DatePickerController {

    private static final String TAG = CartCalendarFragment.class.getSimpleName();

    private CartActivity mActivity;
    private DayPickerView mDayPickerView;

    public static CartCalendarFragment newInstance() {
        return new CartCalendarFragment();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mActivity = (CartActivity) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart_calendar, container, false);

        mDayPickerView = (DayPickerView) view.findViewById(R.id.pickerView);
        mDayPickerView.setController(this);

        Calendar calendar = Calendar.getInstance();
        DateFormatSymbols dateFormatSymbols = new DateFormatSymbols();

        String[] dayOfWeeks = dateFormatSymbols.getShortWeekdays();
        int firstDayIndex = calendar.getFirstDayOfWeek();
        for (int i = 0; i < 7; i++) {
            int textViewId = mActivity.getResources().getIdentifier("text_day_of_week" + (i + 1), "id", mActivity.getPackageName());
            TextView textView = (TextView) view.findViewById(textViewId);
            textView.setText(dayOfWeeks[(firstDayIndex + 7 + i - 1) % 7 + 1].toUpperCase(Locale.getDefault()));
        }

        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        //mIsFront = isVisibleToUser;

        // Make sure that we are currently visible
        //if (!this.isVisible()) {
        // If we are becoming invisible, then...
        if (mActivity != null && mDayPickerView != null) {
            if (isVisibleToUser && mActivity.mStartDay == null && mActivity.mEndDay == null) {
                mDayPickerView.resetSelectedDays();
            }
        }
        //}
    }

    @Override
    public int getMaxYear() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.YEAR);
    }

    @Override
    public void onDayOfMonthSelected(int year, int month, int day) {
        Log.e(TAG, year + " / " + (month + 1) + " / " + day);

        Time time = new Time(Time.getCurrentTimezone());
        time.set(day, month, year);

        if ((mActivity.mStartDay == null && mActivity.mEndDay == null) ||
                (mActivity.mStartDay != null && mActivity.mEndDay != null)) {
            mActivity.mStartDay = time;
            mActivity.mEndDay = null;
        } else {
            if (time.after(mActivity.mStartDay)) {
                mActivity.mEndDay = time;
            } else {
                mActivity.mEndDay = mActivity.mStartDay;
                mActivity.mStartDay = time;
            }
        }
    }

    @Override
    public void onDateRangeSelected(SimpleMonthAdapter.SelectedDays<SimpleMonthAdapter.CalendarDay> calendarDaySelectedDays) {
        if (Config.DEBUG)
            Log.e(TAG, calendarDaySelectedDays.getFirst().toString() + " --> " + calendarDaySelectedDays.getLast().toString());
    }

}
