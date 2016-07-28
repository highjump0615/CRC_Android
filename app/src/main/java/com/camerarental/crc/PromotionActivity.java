/**
 * @author LuYongXing
 * @date 2015.01.14
 * @filename PromotionActivity.java
 */

package com.camerarental.crc;

import android.util.Log;

import com.camerarental.crc.base.BaseWebActivity;
import com.camerarental.crc.utils.Constant;

public class PromotionActivity extends BaseWebActivity {

    private static final String TAG = PromotionActivity.class.getSimpleName();

    @Override
    protected void initViews() {
        super.initViews();

        Log.d(TAG, "PromotionActivity was created!");

        mTextTitle.setText(R.string.promotion);

        mWebView.loadUrl(/*"http://docs.google.com/gview?embedded=true&url=" + */Constant.EQUIPMENT_PROMOTION_URL);
    }

}
