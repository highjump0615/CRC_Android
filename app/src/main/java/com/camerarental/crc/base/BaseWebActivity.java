/**
 * @author LuYongXing
 * @date 2015.01.07
 * @filename BaseWebActivity.java
 */

package com.camerarental.crc.base;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.camerarental.crc.R;

public class BaseWebActivity extends BaseActivity {

    static final String TAG = BaseWebActivity.class.getSimpleName();

    protected WebView mWebView;

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mLayoutContainer.removeView(mWebView);
        mWebView.removeAllViews();
        mWebView.destroy();
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void initViews() {
        super.initViews();

        mLayoutInflater.inflate(R.layout.layout_webview, mLayoutContainer);

        mWebView = (WebView) mLayoutContainer.findViewById(R.id.webView);
        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        mWebView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);

        initProgressDialog(this);
        showProgress();

        mWebView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.i(TAG, "Processing webview url click...");
                view.loadUrl(url);
                showProgress();
                return true;
            }

            public void onPageFinished(WebView view, String url) {
                Log.i(TAG, "Finished loading URL: " + url);
                dismissProgress();
            }

            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Log.e(TAG, "Error: " + description);
                //Toast.makeText(BaseWebActivity.this, "Oh no! " + description, Toast.LENGTH_SHORT).show();

                AlertDialog alertDialog = new AlertDialog.Builder(BaseWebActivity.this)
                        .setTitle("Error Connection")
                        .setMessage(/*description*/"You are not connected to the internet.\nPlease try again later!")
                        .setPositiveButton(android.R.string.ok, null)
                        .setCancelable(true)
                        .create();
                alertDialog.show();
            }
        });

        //mWebView.loadUrl("http://www.google.com/");
    }

}
