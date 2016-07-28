/**
 * @author LuYongXing
 * @date 2015.01.14
 * @filename EquipmentActivity.java
 */

package com.camerarental.crc.base;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.camerarental.crc.R;

public class BaseActivity extends FragmentActivity implements View.OnClickListener {

    protected TextView mTextTitle;
    protected ImageView mImageSearch;

    protected FrameLayout mLayoutContainer;
    protected LayoutInflater mLayoutInflater;

    private Dialog mProgressDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_base);
        initViews();
    }

    protected void initViews() {
        mLayoutInflater = LayoutInflater.from(this);

        mTextTitle = (TextView) findViewById(R.id.text_title);
        mImageSearch = (ImageView) findViewById(R.id.image_search);
        mImageSearch.setOnClickListener(this);

        mLayoutContainer = (FrameLayout) findViewById(R.id.layout_container);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_search:
                break;
        }
    }

    /* FullScreen Progress bar */
    /**
     * Initialize progress dialog
     */
    public void initProgressDialog(Context context) {
        mProgressDialog = new Dialog(context, android.R.style.Theme_Translucent);
        mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mProgressDialog.setContentView(R.layout.loading);
        mProgressDialog.setCancelable(true);
        mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                onBackPressed();
            }
        });
    }

    /**
     * Show progress dialog
     */
    public void showProgress() {
        if (mProgressDialog != null && !mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
    }

    /**
     * Dismiss progress dialog
     */
    public void dismissProgress() {
        if (mProgressDialog != null)
            mProgressDialog.dismiss();
    }

}
