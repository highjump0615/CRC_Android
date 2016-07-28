/**
 * @author LuYongXing
 * @date 2015.01.27
 * @filename CartReportFragment.java
 */

package com.camerarental.crc.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.camerarental.crc.R;
import com.camerarental.crc.utils.Constant;

public class CartReportFragment extends Fragment {

    public static CartReportFragment newInstance() {
        return new CartReportFragment();
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart_report, container, false);

        WebView webView = (WebView) view.findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebChromeClient(new WebChromeClient() {
        });
        // disable scroll on touch
        webView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return (event.getAction() == MotionEvent.ACTION_MOVE);
            }
        });
        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);

        final String mimeType = "text/html";
        final String encoding = "UTF-8";
        String html = Constant.BOOKING_HTML;

        webView.loadDataWithBaseURL("", html, mimeType, encoding, "");

        return view;
    }

}
