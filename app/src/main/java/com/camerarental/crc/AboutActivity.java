/**
 * @author LuYongXing
 * @date 2015.01.12
 * @filename AboutActivity.java
 */

package com.camerarental.crc;

import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.aplit.dev.listeners.ReadHttpTaskListener;
import com.aplit.dev.tasks.ReadHttpTask;
import com.camerarental.crc.base.BaseActivity;
import com.camerarental.crc.utils.Config;
import com.camerarental.crc.utils.Constant;
import com.camerarental.crc.widget.WorkaroundMapFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class AboutActivity extends BaseActivity implements ReadHttpTaskListener {

    private static final String TAG = AboutActivity.class.getSimpleName();

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setUpMapIfNeeded();
    }

    @Override
    protected void initViews() {
        super.initViews();

        Log.d(TAG, "AboutActivity was created!");

        mTextTitle.setText(R.string.about);
        mLayoutInflater.inflate(R.layout.layout_about, mLayoutContainer);

        mLayoutContainer.findViewById(R.id.layout_address).setOnClickListener(this);
        mLayoutContainer.findViewById(R.id.layout_url).setOnClickListener(this);
        mLayoutContainer.findViewById(R.id.layout_email).setOnClickListener(this);
        mLayoutContainer.findViewById(R.id.layout_phone).setOnClickListener(this);

        TextView textView = (TextView) mLayoutContainer.findViewById(R.id.text_faq2);
        textView.setText(Html.fromHtml("If you do not receive an email reply to your booking enquiry within 24 hours, please call/SMS us at <font color=\"blue\"><u>96504158</u></font> or email to \"<font color=\"blue\"><u>info@camerarental.biz</u></font>\" so that we can check on the status of your request."));

        final ScrollView scrollView = (ScrollView) mLayoutContainer.findViewById(R.id.scrollView);
        ((WorkaroundMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_map)).setListener(new WorkaroundMapFragment.OnTouchListener() {
            @Override
            public void onTouch() {
                scrollView.requestDisallowInterceptTouchEvent(true);
            }
        });
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        switch (v.getId()) {
            case R.id.layout_address:
                setUpMap();
                break;

            case R.id.layout_url:
                onBrowser();
                break;

            case R.id.layout_email:
                onEmail();
                break;

            case R.id.layout_phone:
                onCall();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            final SupportMapFragment mapView = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_map);

            mMap = mapView.getMap();
            mapView.getView().getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override public void onGlobalLayout() {
                    View squareView = mapView.getView();
                    LinearLayout.LayoutParams layout = (LinearLayout.LayoutParams) squareView.getLayoutParams();
                    layout.height = squareView.getWidth() * 9 / 16;
                    squareView.setLayoutParams(layout);
                    squareView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            });

            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        //mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));

        try {
            initProgressDialog(this);
            showProgress();

            new ReadHttpTask(this, 0, this, null).execute(
                    String.format(Constant.GEOCODING_SERVICE_API, URLEncoder.encode(getString(R.string.our_company_address), "UTF-8")));
        } catch (UnsupportedEncodingException e) {
            if (Config.DEBUG) e.printStackTrace();
            dismissProgress();
        }
    }

    @Override
    public void onObtainedHttpContent(int i, String result) {
        dismissProgress();

        if (TextUtils.isEmpty(result)) {
            Log.e(TAG, "could not get latitude and longitude");
            return;
        }

        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray = jsonObject.getJSONArray("results");

            if (jsonArray != null && jsonArray.length() > 0) {
                jsonObject = jsonArray.getJSONObject(0);

                if (jsonObject != null) {
                    jsonObject = jsonObject.getJSONObject("geometry");

                    if (jsonObject != null) {
                        jsonObject = jsonObject.getJSONObject("location");

                        if (jsonObject != null) {
                            double latitude = jsonObject.getDouble("lat");
                            double longitude = jsonObject.getDouble("lng");

                            LatLng markerLocation = new LatLng(latitude, longitude);

                            mMap.addMarker(new MarkerOptions()
                                    .position(markerLocation)
                                    .title(getString(R.string.our_company_address))
                                    //.snippet(getString(R.string.our_company_address))
                            );

                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerLocation, 16));
                            // ! you can query Projection object here
                            Point markerScreenPosition = mMap.getProjection().toScreenLocation(markerLocation);
                            System.out.println(markerScreenPosition);
                        }
                    }
                }
            }
        } catch (JSONException e) {
            if (Config.DEBUG) e.printStackTrace();
        }
    }

    private void onBrowser() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://" + getString(R.string.our_company_url)));
        startActivity(browserIntent);
    }

    private void onCall() {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + getString(R.string.our_company_phone)));
        startActivity(callIntent);
    }

    private void onEmail() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        String[] recipients = {getString(R.string.our_company_email)};
        intent.putExtra(Intent.EXTRA_EMAIL, recipients);
        intent.setType("text/html");
        startActivity(Intent.createChooser(intent, "Send mail"));
    }

}
