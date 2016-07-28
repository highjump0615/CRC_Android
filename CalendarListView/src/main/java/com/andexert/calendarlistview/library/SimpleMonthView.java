/***********************************************************************************
 * The MIT License (MIT)

 * Copyright (c) 2014 Robin Chutaux

 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:

 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.

 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 ***********************************************************************************/
package com.andexert.calendarlistview.library;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.view.MotionEvent;
import android.view.View;

import java.security.InvalidParameterException;
import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

class SimpleMonthView extends View {

    public static final String VIEW_PARAMS_HEIGHT = "height";
    public static final String VIEW_PARAMS_MONTH = "month";
    public static final String VIEW_PARAMS_YEAR = "year";
    public static final String VIEW_PARAMS_SELECTED_BEGIN_DAY = "selected_begin_day";
    public static final String VIEW_PARAMS_SELECTED_LAST_DAY = "selected_last_day";
    public static final String VIEW_PARAMS_SELECTED_BEGIN_MONTH = "selected_begin_month";
    public static final String VIEW_PARAMS_SELECTED_LAST_MONTH = "selected_last_month";
    public static final String VIEW_PARAMS_SELECTED_BEGIN_YEAR = "selected_begin_year";
    public static final String VIEW_PARAMS_SELECTED_LAST_YEAR = "selected_last_year";
    public static final String VIEW_PARAMS_WEEK_START = "week_start";

    private static final int SELECTED_CIRCLE_ALPHA = 128;
    protected static int DEFAULT_HEIGHT = 32;
    protected static final int DEFAULT_NUM_ROWS = 6;
	protected static int DAY_SELECTED_CIRCLE_SIZE;
	protected static int DAY_SEPARATOR_WIDTH = 1;
	protected static int MINI_DAY_NUMBER_TEXT_SIZE;
	protected static int MIN_HEIGHT = 10;
	protected static int MONTH_DAY_LABEL_TEXT_SIZE;
	protected static int MONTH_HEADER_SIZE;
	protected static int MONTH_LABEL_TEXT_SIZE;

    private String mDayOfWeekTypeface;
    private String mMonthTitleTypeface;

    protected Paint mMonthTitlePaint;
    protected Paint mMonthDayLabelPaint;
    protected Paint mMonthNumPaint;
    protected Paint mMonthNumBgPaint;
    protected Paint mSelectedCirclePaint;

    protected int mCurrentDayBgColor;
    protected int mCurrentDayTextColor;
    protected int mMonthTextBgColor;
    protected int mMonthTextColor;
    protected int mDayTextBgColor;
    protected int mDayTextColor;
    protected int mDayNumBgColor;
    protected int mDayNumTextColor;
    protected int mPreviousDayBgColor;
    protected int mPreviousDayColor;
    protected int mSelectedDaysBgColor;
    protected int mSelectedDaysColor;
    protected int mSelectedBeginDayBgColor;
    protected int mSelectedBeginDayTextColor;

    private final StringBuilder mStringBuilder;

    protected boolean mHasToday = false;
    protected boolean mIsPrev = false;
    protected int mSelectedBeginDay = -1;
    protected int mSelectedLastDay = -1;
    protected int mSelectedBeginMonth = -1;
    protected int mSelectedLastMonth = -1;
    protected int mSelectedBeginYear = -1;
    protected int mSelectedLastYear = -1;
    protected int mToday = -1;
    protected int mWeekStart = 1;
    protected int mNumDays = 7;
    protected int mNumCells = mNumDays;
    private int mDayOfWeekStart = 0;
    protected int mMonth;
    protected Boolean mDrawRect;
    protected int mRowHeight = DEFAULT_HEIGHT;

    protected int mWidth;
    protected int mPadding = 0;
    protected int mCellWidth;
    protected int mCellHeight;

    protected int mYear;
    final Time today;
    final Time expireDay;

	private final Calendar mCalendar;
	private final Calendar mDayLabelCalendar;
    private final Boolean isPrevDayEnabled;

    private int mNumRows = DEFAULT_NUM_ROWS;

	private DateFormatSymbols mDateFormatSymbols = new DateFormatSymbols();

    private OnDayClickListener mOnDayClickListener;

    public SimpleMonthView(Context context, TypedArray typedArray) {
        super(context);

        Resources resources = context.getResources();

        mDayLabelCalendar = Calendar.getInstance();
        mCalendar = Calendar.getInstance();
        today = new Time(Time.getCurrentTimezone());
        today.setToNow();

        int month = mCalendar.get(Calendar.MONTH);
        int year = mCalendar.get(Calendar.YEAR);
        if ((month + 6) / 12 > 0) {
            year++;
            month = (month + 6) % 12;
        } else {
            month += 6;
        }
        expireDay = new Time(Time.getCurrentTimezone());
        expireDay.set(mCalendar.get(Calendar.DAY_OF_MONTH), month, year);

        mDayOfWeekTypeface = resources.getString(R.string.sans_serif);
        mMonthTitleTypeface = resources.getString(R.string.sans_serif);

        mCurrentDayBgColor = typedArray.getColor(R.styleable.DayPickerView_colorCurrentDayBackground, resources.getColor(R.color.selected_day_background));
        mCurrentDayTextColor = typedArray.getColor(R.styleable.DayPickerView_colorCurrentDay, resources.getColor(R.color.normal_day));
        mMonthTextBgColor = typedArray.getColor(R.styleable.DayPickerView_colorMonthNameBackground, Color.WHITE);
        mMonthTextColor = typedArray.getColor(R.styleable.DayPickerView_colorMonthName, resources.getColor(R.color.normal_day));
        mDayTextBgColor = typedArray.getColor(R.styleable.DayPickerView_colorDayNameBackground, Color.WHITE);
        mDayTextColor = typedArray.getColor(R.styleable.DayPickerView_colorDayName, resources.getColor(R.color.normal_day));
        mDayNumBgColor = typedArray.getColor(R.styleable.DayPickerView_colorNormalDayBackground, Color.WHITE);
        mDayNumTextColor = typedArray.getColor(R.styleable.DayPickerView_colorNormalDay, resources.getColor(R.color.normal_day));
        mPreviousDayBgColor = typedArray.getColor(R.styleable.DayPickerView_colorPreviousDayBackground, Color.LTGRAY);
        mPreviousDayColor = typedArray.getColor(R.styleable.DayPickerView_colorPreviousDay, resources.getColor(R.color.normal_day));
        mSelectedDaysBgColor = typedArray.getColor(R.styleable.DayPickerView_colorSelectedDayBackground, resources.getColor(R.color.selected_day_background));
        mSelectedDaysColor = typedArray.getColor(R.styleable.DayPickerView_colorSelectedDayText, resources.getColor(R.color.selected_day_text));
        mSelectedBeginDayBgColor = typedArray.getColor(R.styleable.DayPickerView_colorSelectedBeginDayBackground, resources.getColor(R.color.selected_day_background));
        mSelectedBeginDayTextColor = typedArray.getColor(R.styleable.DayPickerView_colorSelectedBeginDay, resources.getColor(R.color.selected_day_text));

        mDrawRect = typedArray.getBoolean(R.styleable.DayPickerView_drawRoundRect, false);

        mStringBuilder = new StringBuilder(50);

        MINI_DAY_NUMBER_TEXT_SIZE = typedArray.getDimensionPixelSize(R.styleable.DayPickerView_textSizeDay, resources.getDimensionPixelSize(R.dimen.text_size_day));
        MONTH_LABEL_TEXT_SIZE = typedArray.getDimensionPixelSize(R.styleable.DayPickerView_textSizeMonth, resources.getDimensionPixelSize(R.dimen.text_size_month));
        MONTH_DAY_LABEL_TEXT_SIZE = typedArray.getDimensionPixelSize(R.styleable.DayPickerView_textSizeDayName, resources.getDimensionPixelSize(R.dimen.text_size_day_name));
        MONTH_HEADER_SIZE = typedArray.getDimensionPixelOffset(R.styleable.DayPickerView_headerMonthHeight, resources.getDimensionPixelOffset(R.dimen.header_month_height));
        DAY_SELECTED_CIRCLE_SIZE = typedArray.getDimensionPixelSize(R.styleable.DayPickerView_selectedDayRadius, resources.getDimensionPixelOffset(R.dimen.selected_day_radius));

        mRowHeight = ((typedArray.getDimensionPixelSize(R.styleable.DayPickerView_calendarHeight, resources.getDimensionPixelOffset(R.dimen.calendar_height)) - MONTH_HEADER_SIZE) / 6);

        isPrevDayEnabled = typedArray.getBoolean(R.styleable.DayPickerView_enablePreviousDay, true);

        initView();
    }

    private int calculateNumRows() {
        int offset = findDayOffset();
        int dividend = (offset + mNumCells) / mNumDays;
        int remainder = (offset + mNumCells) % mNumDays;
        return (dividend + (remainder > 0 ? 1 : 0));
	}

	private void drawMonthDayLabels(Canvas canvas) {
        int y = MONTH_HEADER_SIZE - (MONTH_DAY_LABEL_TEXT_SIZE / 2);
        int dayWidthHalf = (mWidth - mPadding * 2) / (mNumDays * 2);

        for (int i = 0; i < mNumDays; i++) {
            int calendarDay = (i + mWeekStart) % mNumDays;
            int x = (2 * i + 1) * dayWidthHalf + mPadding;
            mDayLabelCalendar.set(Calendar.DAY_OF_WEEK, calendarDay);
            canvas.drawText(mDateFormatSymbols.getShortWeekdays()[mDayLabelCalendar.get(Calendar.DAY_OF_WEEK)].toUpperCase(Locale.getDefault()), x, y, mMonthDayLabelPaint);
        }
	}

	private void drawMonthTitle(Canvas canvas) {
        mMonthTitlePaint.setColor(mMonthTextBgColor);

        // Draw background
        RectF rectF = new RectF(0, 0, mWidth, MONTH_HEADER_SIZE);
        canvas.drawRoundRect(rectF, 0.0f, 0.0f, mMonthTitlePaint);

        mMonthTitlePaint.setColor(mMonthTextColor);
        //int x = (mWidth + 2 * mPadding) / 2;
        //int y = (MONTH_HEADER_SIZE - MONTH_DAY_LABEL_TEXT_SIZE) / 2 + (MONTH_LABEL_TEXT_SIZE / 3);
        int x = 24;
        int y = MONTH_HEADER_SIZE / 2 + (MONTH_LABEL_TEXT_SIZE / 2);
        StringBuilder stringBuilder = new StringBuilder(getMonthAndYearString().toLowerCase());
        stringBuilder.setCharAt(0, Character.toUpperCase(stringBuilder.charAt(0)));
        canvas.drawText(stringBuilder.toString(), x, y, mMonthTitlePaint);
	}

	private int findDayOffset() {
        return (mDayOfWeekStart < mWeekStart ? (mDayOfWeekStart + mNumDays) : mDayOfWeekStart) - mWeekStart;
	}

	private String getMonthAndYearString() {
        int flags = DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_NO_MONTH_DAY;
        mStringBuilder.setLength(0);
        long millis = mCalendar.getTimeInMillis();
        return DateUtils.formatDateRange(getContext(), millis, millis, flags);
    }

	private void onDayClick(SimpleMonthAdapter.CalendarDay calendarDay) {
		if (mOnDayClickListener != null && (isPrevDayEnabled || !((calendarDay.month == today.month) && (calendarDay.year == today.year) && calendarDay.day < today.monthDay))) {
			mOnDayClickListener.onDayClick(this, calendarDay);
        }
	}

	private boolean sameDay(int monthDay, Time time) {
		return (mYear == time.year) && (mMonth == time.month) && (monthDay == time.monthDay);
	}

    private boolean prevDay(int monthDay, Time time) {
        return ((mYear < time.year)) || (mYear == time.year && mMonth < time.month) || ( mMonth == time.month && monthDay < time.monthDay);
    }

    private boolean afterDay(int monthDay, Time time) {
        return ((mYear > time.year)) || (mYear == time.year && mMonth > time.month) || ( mMonth == time.month && monthDay > time.monthDay);
    }

	protected void drawMonthNums(Canvas canvas) {
		int y = (mRowHeight + MINI_DAY_NUMBER_TEXT_SIZE) / 2 - DAY_SEPARATOR_WIDTH + MONTH_HEADER_SIZE;
		int paddingDay = (mWidth - 2 * mPadding) / (2 * mNumDays);
		int dayOffset = findDayOffset();
		int day = 1;

        // Fill first empty part
        if (!isPrevDayEnabled && (prevDay(day, today) || afterDay(day, expireDay))) {
            mMonthNumBgPaint.setColor(mPreviousDayBgColor);
        }
        else {
            mMonthNumBgPaint.setColor(mDayNumBgColor);
        }

        int x = paddingDay * (1 + dayOffset * 2) + mPadding;
        RectF rectF = new RectF(
                0, (y - MINI_DAY_NUMBER_TEXT_SIZE / 3) - mCellHeight / 2,
                x - mCellWidth / 2, (y - MINI_DAY_NUMBER_TEXT_SIZE / 3) + mCellHeight / 2);
        canvas.drawRoundRect(rectF, 0.0f, 0.0f, mMonthNumBgPaint);
        //

		while (day <= mNumCells) {

            if (mHasToday && (mToday == day)) {
                mMonthNumPaint.setColor(mCurrentDayTextColor);
                mMonthNumPaint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                mMonthNumBgPaint.setColor(mCurrentDayBgColor);
            } else {
				mMonthNumPaint.setColor(mDayNumTextColor);
                mMonthNumPaint.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                mMonthNumBgPaint.setColor(mDayNumBgColor);
            }

            if ((mMonth == mSelectedBeginMonth && mSelectedBeginDay == day && mSelectedBeginYear == mYear) ||
                    (mMonth == mSelectedLastMonth && mSelectedLastDay == day && mSelectedLastYear == mYear)) {
                mMonthNumPaint.setColor(mSelectedBeginDayTextColor);
                mMonthNumBgPaint.setColor(mSelectedBeginDayBgColor);
            }

            if ((mSelectedBeginDay != -1 && mSelectedLastDay != -1 && mSelectedBeginYear == mSelectedLastYear &&
                    mSelectedBeginMonth == mSelectedLastMonth &&
                    mSelectedBeginDay == mSelectedLastDay &&
                    day == mSelectedBeginDay &&
                    mMonth == mSelectedBeginMonth &&
                    mYear == mSelectedBeginYear)) {
                mMonthNumPaint.setColor(mSelectedBeginDayTextColor);
                mMonthNumBgPaint.setColor(mSelectedBeginDayBgColor);
            }

            if ((mSelectedBeginDay != -1 && mSelectedLastDay != -1 && mSelectedBeginYear == mSelectedLastYear && mSelectedBeginYear == mYear) &&
                    (((mMonth == mSelectedBeginMonth && mSelectedLastMonth == mSelectedBeginMonth) && ((mSelectedBeginDay < mSelectedLastDay && day > mSelectedBeginDay && day < mSelectedLastDay) || (mSelectedBeginDay > mSelectedLastDay && day < mSelectedBeginDay && day > mSelectedLastDay))) ||
                    ((mSelectedBeginMonth < mSelectedLastMonth && mMonth == mSelectedBeginMonth && day > mSelectedBeginDay) || (mSelectedBeginMonth < mSelectedLastMonth && mMonth == mSelectedLastMonth && day < mSelectedLastDay)) ||
                    ((mSelectedBeginMonth > mSelectedLastMonth && mMonth == mSelectedBeginMonth && day < mSelectedBeginDay) || (mSelectedBeginMonth > mSelectedLastMonth && mMonth == mSelectedLastMonth && day > mSelectedLastDay))))
            {
                mMonthNumPaint.setColor(mSelectedDaysColor);
                mMonthNumBgPaint.setColor(mSelectedDaysBgColor);
            }

            if ((mSelectedBeginDay != -1 && mSelectedLastDay != -1 && mSelectedBeginYear != mSelectedLastYear && ((mSelectedBeginYear == mYear && mMonth == mSelectedBeginMonth) || (mSelectedLastYear == mYear && mMonth == mSelectedLastMonth)) &&
                    (((mSelectedBeginMonth < mSelectedLastMonth && mMonth == mSelectedBeginMonth && day < mSelectedBeginDay) || (mSelectedBeginMonth < mSelectedLastMonth && mMonth == mSelectedLastMonth && day > mSelectedLastDay)) ||
                    ((mSelectedBeginMonth > mSelectedLastMonth && mMonth == mSelectedBeginMonth && day > mSelectedBeginDay) || (mSelectedBeginMonth > mSelectedLastMonth && mMonth == mSelectedLastMonth && day < mSelectedLastDay)))))
            {
                mMonthNumPaint.setColor(mSelectedDaysColor);
                mMonthNumBgPaint.setColor(mSelectedDaysBgColor);
            }

            if ((mSelectedBeginDay != -1 && mSelectedLastDay != -1 && mSelectedBeginYear == mSelectedLastYear && mYear == mSelectedBeginYear) &&
                    ((mMonth > mSelectedBeginMonth && mMonth < mSelectedLastMonth && mSelectedBeginMonth < mSelectedLastMonth) ||
                     (mMonth < mSelectedBeginMonth && mMonth > mSelectedLastMonth && mSelectedBeginMonth > mSelectedLastMonth)))
            {
                mMonthNumPaint.setColor(mSelectedDaysColor);
                mMonthNumBgPaint.setColor(mSelectedDaysBgColor);
            }

            if ((mSelectedBeginDay != -1 && mSelectedLastDay != -1 && mSelectedBeginYear != mSelectedLastYear) &&
                    ((mSelectedBeginYear < mSelectedLastYear && ((mMonth > mSelectedBeginMonth && mYear == mSelectedBeginYear) || (mMonth < mSelectedLastMonth && mYear == mSelectedLastYear))) ||
                     (mSelectedBeginYear > mSelectedLastYear && ((mMonth < mSelectedBeginMonth && mYear == mSelectedBeginYear) || (mMonth > mSelectedLastMonth && mYear == mSelectedLastYear)))))
            {
                mMonthNumPaint.setColor(mSelectedDaysColor);
                mMonthNumBgPaint.setColor(mSelectedDaysBgColor);
            }

            if (!isPrevDayEnabled && (prevDay(day, today) || afterDay(day, expireDay)))
            {
                mMonthNumPaint.setColor(mPreviousDayColor);
                mMonthNumPaint.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                mMonthNumBgPaint.setColor(mPreviousDayBgColor);
            }

            x = paddingDay * (1 + dayOffset * 2) + mPadding;

            //if ((mMonth == mSelectedBeginMonth && mSelectedBeginDay == day && mSelectedBeginYear == mYear) || (mMonth == mSelectedLastMonth && mSelectedLastDay == day && mSelectedLastYear == mYear)) {
                if (mDrawRect) {
//                    RectF rectF = new RectF(
//                            x - DAY_SELECTED_CIRCLE_SIZE, (y  - MINI_DAY_NUMBER_TEXT_SIZE / 3) - DAY_SELECTED_CIRCLE_SIZE,
//                            x + DAY_SELECTED_CIRCLE_SIZE, (y  - MINI_DAY_NUMBER_TEXT_SIZE / 3) + DAY_SELECTED_CIRCLE_SIZE);
                    rectF = new RectF(
                            x - mCellWidth / 2, (y - MINI_DAY_NUMBER_TEXT_SIZE / 3) - mCellHeight / 2,
                            x + mCellWidth / 2, (y - MINI_DAY_NUMBER_TEXT_SIZE / 3) + mCellHeight / 2);
                    canvas.drawRoundRect(rectF, 0.0f, 0.0f, mMonthNumBgPaint);
                }
                else {
                    canvas.drawCircle(x, y - MINI_DAY_NUMBER_TEXT_SIZE / 3, DAY_SELECTED_CIRCLE_SIZE, mSelectedCirclePaint);
                }
            //}

            canvas.drawText(String.format("%d", day), x, y, mMonthNumPaint);

			dayOffset++;
			if (dayOffset == mNumDays) {
				dayOffset = 0;
				y += mRowHeight;
			}
			day++;
		}

        // Fill last empty part
        if (!isPrevDayEnabled && (prevDay(day, today) || afterDay(day, expireDay))) {
            mMonthNumBgPaint.setColor(mPreviousDayBgColor);
        } else {
            mMonthNumBgPaint.setColor(mDayNumBgColor);
        }

        rectF = new RectF(
                x + mCellWidth / 2, (y - MINI_DAY_NUMBER_TEXT_SIZE / 3) - mCellHeight / 2,
                mWidth, (y - MINI_DAY_NUMBER_TEXT_SIZE / 3) + mCellHeight / 2);
        canvas.drawRoundRect(rectF, 0.0f, 0.0f, mMonthNumBgPaint);
        //
	}

	public SimpleMonthAdapter.CalendarDay getDayFromLocation(float x, float y) {
		int padding = mPadding;
		if ((x < padding) || (x > mWidth - mPadding)) {
			return null;
		}

		int yDay = (int) (y - MONTH_HEADER_SIZE) / mRowHeight;
		int day = 1 + ((int) ((x - padding) * mNumDays / (mWidth - padding - mPadding)) - findDayOffset()) + yDay * mNumDays;

        if (mMonth > 11 || mMonth < 0 || CalendarUtils.getDaysInMonth(mMonth, mYear) < day || day < 1)
            return null;

		return new SimpleMonthAdapter.CalendarDay(mYear, mMonth, day);
	}

	protected void initView() {
        mMonthTitlePaint = new Paint();
        //mMonthTitlePaint.setFakeBoldText(true);
        mMonthTitlePaint.setAntiAlias(true);
        mMonthTitlePaint.setTextSize(MONTH_LABEL_TEXT_SIZE);
        mMonthTitlePaint.setTypeface(Typeface.create(mMonthTitleTypeface, Typeface.NORMAL));
        mMonthTitlePaint.setColor(mMonthTextColor);
        mMonthTitlePaint.setTextAlign(Align.LEFT);
        mMonthTitlePaint.setStyle(Style.FILL);

        mSelectedCirclePaint = new Paint();
        mSelectedCirclePaint.setFakeBoldText(true);
        mSelectedCirclePaint.setAntiAlias(true);
        mSelectedCirclePaint.setColor(mSelectedDaysColor);
        mSelectedCirclePaint.setTextAlign(Align.CENTER);
        mSelectedCirclePaint.setStyle(Style.FILL);
        mSelectedCirclePaint.setAlpha(SELECTED_CIRCLE_ALPHA);

        mMonthDayLabelPaint = new Paint();
        mMonthDayLabelPaint.setFakeBoldText(true);
        mMonthDayLabelPaint.setAntiAlias(true);
        mMonthDayLabelPaint.setTextSize(MONTH_DAY_LABEL_TEXT_SIZE);
        mMonthDayLabelPaint.setColor(mDayTextColor);
        mMonthDayLabelPaint.setTypeface(Typeface.create(mDayOfWeekTypeface, Typeface.NORMAL));
        mMonthDayLabelPaint.setStyle(Style.FILL);
        mMonthDayLabelPaint.setTextAlign(Align.CENTER);

        mMonthNumPaint = new Paint();
        mMonthNumPaint.setAntiAlias(true);
        mMonthNumPaint.setTextSize(MINI_DAY_NUMBER_TEXT_SIZE);
        mMonthNumPaint.setStyle(Style.FILL);
        mMonthNumPaint.setTextAlign(Align.CENTER);
        mMonthNumPaint.setFakeBoldText(false);

        mMonthNumBgPaint = new Paint();
        mMonthNumBgPaint.setAntiAlias(true);
        mMonthNumBgPaint.setStyle(Style.FILL);
        mMonthNumBgPaint.setTextAlign(Align.CENTER);
	}

	protected void onDraw(Canvas canvas) {
		drawMonthTitle(canvas);
		//drawMonthDayLabels(canvas);
		drawMonthNums(canvas);
	}

	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // LuYongXing, 20150127
        {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            mCellWidth = width / 7;
            mCellHeight = mCellWidth * 3 / 4;

            mRowHeight = MONTH_HEADER_SIZE = mCellHeight;
        }

		setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), mRowHeight * mNumRows + MONTH_HEADER_SIZE);
	}

	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		mWidth = w;
	}

	public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            SimpleMonthAdapter.CalendarDay calendarDay = getDayFromLocation(event.getX(), event.getY());
            if (calendarDay != null) {
                onDayClick(calendarDay);
            }
        }
        return true;
	}

	public void reuse() {
        mNumRows = DEFAULT_NUM_ROWS;
		requestLayout();
	}

	public void setMonthParams(HashMap<String, Integer> params) {
        if (!params.containsKey(VIEW_PARAMS_MONTH) && !params.containsKey(VIEW_PARAMS_YEAR)) {
            throw new InvalidParameterException("You must specify month and year for this view");
        }
		setTag(params);

        if (params.containsKey(VIEW_PARAMS_HEIGHT)) {
            mRowHeight = params.get(VIEW_PARAMS_HEIGHT);
            if (mRowHeight < MIN_HEIGHT) {
                mRowHeight = MIN_HEIGHT;
            }
        }
        if (params.containsKey(VIEW_PARAMS_SELECTED_BEGIN_DAY)) {
            mSelectedBeginDay = params.get(VIEW_PARAMS_SELECTED_BEGIN_DAY);
        }
        if (params.containsKey(VIEW_PARAMS_SELECTED_LAST_DAY)) {
            mSelectedLastDay = params.get(VIEW_PARAMS_SELECTED_LAST_DAY);
        }
        if (params.containsKey(VIEW_PARAMS_SELECTED_BEGIN_MONTH)) {
            mSelectedBeginMonth = params.get(VIEW_PARAMS_SELECTED_BEGIN_MONTH);
        }
        if (params.containsKey(VIEW_PARAMS_SELECTED_LAST_MONTH)) {
            mSelectedLastMonth = params.get(VIEW_PARAMS_SELECTED_LAST_MONTH);
        }
        if (params.containsKey(VIEW_PARAMS_SELECTED_BEGIN_YEAR)) {
            mSelectedBeginYear = params.get(VIEW_PARAMS_SELECTED_BEGIN_YEAR);
        }
        if (params.containsKey(VIEW_PARAMS_SELECTED_LAST_YEAR)) {
            mSelectedLastYear = params.get(VIEW_PARAMS_SELECTED_LAST_YEAR);
        }

        mMonth = params.get(VIEW_PARAMS_MONTH);
        mYear = params.get(VIEW_PARAMS_YEAR);

        mHasToday = false;
        mToday = -1;

		mCalendar.set(Calendar.MONTH, mMonth);
		mCalendar.set(Calendar.YEAR, mYear);
		mCalendar.set(Calendar.DAY_OF_MONTH, 1);
		mDayOfWeekStart = mCalendar.get(Calendar.DAY_OF_WEEK);

        if (params.containsKey(VIEW_PARAMS_WEEK_START)) {
            mWeekStart = params.get(VIEW_PARAMS_WEEK_START);
        } else {
            mWeekStart = mCalendar.getFirstDayOfWeek();
        }

        mNumCells = CalendarUtils.getDaysInMonth(mMonth, mYear);
        for (int i = 0; i < mNumCells; i++) {
            final int day = i + 1;
            if (sameDay(day, today)) {
                mHasToday = true;
                mToday = day;
            }

            mIsPrev = prevDay(day, today);
        }

        mNumRows = calculateNumRows();
	}

	public void setOnDayClickListener(OnDayClickListener onDayClickListener) {
		mOnDayClickListener = onDayClickListener;
	}

	public static abstract interface OnDayClickListener {
		public abstract void onDayClick(SimpleMonthView simpleMonthView, SimpleMonthAdapter.CalendarDay calendarDay);
	}

}