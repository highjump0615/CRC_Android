/**
 * @author LuYongXing
 * @date 2015.01.13
 * @filename MainActivity.java
 */

package com.camerarental.crc;

import android.app.Activity;
import android.app.LocalActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.aplit.dev.listeners.ReadHttpTaskListener;
import com.aplit.dev.tasks.ReadHttpTask;
import com.camerarental.crc.data.EquipmentData;
import com.camerarental.crc.listener.OnCartChangeListener;
import com.camerarental.crc.listener.OnTabChangeListener;
import com.camerarental.crc.utils.CommonUtils;
import com.camerarental.crc.utils.Constant;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class MainActivity extends Activity
        implements OnCartChangeListener, OnTabChangeListener, ReadHttpTaskListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final String TAB_EQUIPMENT = "Equipment";
    private static final String TAB_CART = "Cart";
    private static final String TAB_PROMOTION = "Promotion";
    private static final String TAB_ABOUT = "About";

    // LocationManager
    private LocalActivityManager mLocalActivityManager;
    private TabHost mTabHost;

    // GCM
    public static final String EXTRA_MESSAGE = "strMessage";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    /**
     * Substitute you own sender ID here. This is the project number you got
     * from the API Console, as described in "Getting Started."
     */
    String SENDER_ID = "1006256645252";

    private GoogleCloudMessaging gcm;
    private AtomicInteger msgId = new AtomicInteger();
    private String mRegistrationId = null;

    /*
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check device for Play Services APK. If check succeeds, proceed with
        //  GCM registration.
        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(this);
            mRegistrationId = getRegistrationId(this);

            Log.i(TAG, "RegistrationId = " + mRegistrationId);

            if (mRegistrationId.isEmpty()) {
                registerInBackground();
            } else {
                sendRegistrationIdToBackend();
            }
        } else {
            Log.i(TAG, "No valid Google Play Services APK found.");
        }

        mTabHost = (TabHost) findViewById(R.id.tabhost); // The activity TabHost
        mLocalActivityManager = new LocalActivityManager(this, false);
        mLocalActivityManager.dispatchCreate(savedInstanceState);
        mTabHost.setup(mLocalActivityManager);

        TabHost.TabSpec spec; // Reusable TabSpec for each tab
        Intent intent; // Reusable Intent for each tab

        mTabHost.getTabWidget().setDividerDrawable(R.drawable.tab_normal_bg);

        // Create an Intent to launch an Activity for the tab (to be reused)
        intent = new Intent().setClass(this, EquipmentActivity.class);
        spec = mTabHost.newTabSpec(TAB_EQUIPMENT)
                .setIndicator(createTabView(TAB_EQUIPMENT))
                .setContent(intent);
        mTabHost.addTab(spec);

        // Do the same for the other tabs
        intent = new Intent().setClass(this, CartActivity.class);
        spec = mTabHost.newTabSpec(TAB_CART)
                .setIndicator(createTabView(TAB_CART))
                .setContent(intent);
        mTabHost.addTab(spec);

        intent = new Intent().setClass(this, PromotionActivity.class);
        spec = mTabHost
                .newTabSpec(TAB_PROMOTION)
                .setIndicator(createTabView(TAB_PROMOTION))
                .setContent(intent);
        mTabHost.addTab(spec);

        intent = new Intent().setClass(this, AboutActivity.class);
        spec = mTabHost
                .newTabSpec(TAB_ABOUT)
                .setIndicator(createTabView(TAB_ABOUT))
                .setContent(intent);
        mTabHost.addTab(spec);

        //set tab which one you want open first time 0 or 1 or 2
        mTabHost.setCurrentTab(0);

        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                //int i = mTabHost.getCurrentTab();
                String tag = mTabHost.getCurrentTabTag();

                if (tag.equals(TAB_EQUIPMENT)) {
                    EquipmentActivity equipmentActivity = (EquipmentActivity) mLocalActivityManager.getActivity(TAB_EQUIPMENT);
                    if (equipmentActivity != null) {
                        equipmentActivity.refreshAllList();
                    }
                } else if (tag.equals(TAB_CART)) {
                    CartActivity cartActivity = (CartActivity) mLocalActivityManager.getActivity(TAB_CART);
                    if (cartActivity != null) {
                        cartActivity.setTabChangeListener(MainActivity.this);
                        cartActivity.onCartChanged(-1);
                    }
                }
            }
        });

        // Initialize all data
        EquipmentData.getInstance().setListener(this);
    }

    private View createTabView(String text) {
        View view = LayoutInflater.from(this).inflate(R.layout.tab_button, null);
        TextView tv = (TextView) view.findViewById(R.id.textView);
        tv.setText(text);

        ImageView iv = (ImageView) view.findViewById(R.id.imageView);

        switch (text) {
            case TAB_EQUIPMENT:
                iv.setImageResource(R.drawable.ic_tab_equipment);
                break;
            case TAB_CART:
                iv.setImageResource(R.drawable.ic_tab_cart);
                break;
            case TAB_PROMOTION:
                iv.setImageResource(R.drawable.ic_tab_promotion);
                break;
            case TAB_ABOUT:
                iv.setImageResource(R.drawable.ic_tab_about);
                break;
        }

        return view;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mLocalActivityManager.dispatchResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mLocalActivityManager.dispatchPause(isFinishing());
    }

    @Override
    public void onCartChanged(int totalCount) {
        View view = mTabHost.getTabWidget().getChildTabViewAt(1);
        TextView tvCount = (TextView) view.findViewById(R.id.text_notification_count);

        tvCount.setBackgroundDrawable(CommonUtils.getDefaultBackground(this, 6));

        if (totalCount > 0) {
            tvCount.setVisibility(View.VISIBLE);
            tvCount.setText(String.valueOf(totalCount));
        } else {
            tvCount.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //ImageLoader.getInstance().clearMemoryCache();
        //ImageLoader.getInstance().clearDiskCache();

        EquipmentData.getInstance().freeData();
    }

    @Override
    public void onTabChanged(int tabIndex) {
        mTabHost.setCurrentTab(tabIndex);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                EquipmentActivity equipmentActivity = (EquipmentActivity) mLocalActivityManager.getActivity(TAB_EQUIPMENT);
                if (equipmentActivity != null) {
                    equipmentActivity.refreshAllList();
                }
            }
        });
    }

    @Override
    public void onObtainedHttpContent(int apiIndex, String result) {
        if (apiIndex == Constant.REGISTER_TOKEN) {
            Log.e(TAG, "register token result = " + result);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //                              GCM setting for Push Notification                             //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                //finish();
            }
            return false;
        }
        return true;
    }

    /**
     * Gets the current registration ID for application on GCM service.
     * <p>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     *         registration ID.
     */
    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences();
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }

        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    /**
     * Stores the registration ID and app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param context application's context.
     * @param regId registration ID
     */
    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGCMPreferences();
        int appVersion = getAppVersion(context);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.apply();
    }

    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGCMPreferences() {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    /**
     * Registers the application with GCM servers asynchronously.
     * <p>
     * Stores the registration ID and app versionCode in the application's
     * shared preferences.
     */
    private void registerInBackground() {
        new AsyncTask<Void, Integer, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg;
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(MainActivity.this);
                    }
                    mRegistrationId = gcm.register(SENDER_ID);
                    msg = "Device registered, registration ID=" + mRegistrationId;

                    // You should send the registration ID to your server over HTTP,
                    // so it can use GCM/HTTP or CCS to send messages to your app.
                    // The request to your server should be authenticated if your app
                    // is using accounts.
                    sendRegistrationIdToBackend();

                    // For this demo: we don't need to send it because the device
                    // will send upstream messages to a server that echo back the
                    // message using the 'from' address in the message.

                    // Persist the regID - no need to register again.
                    storeRegistrationId(MainActivity.this, mRegistrationId);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                //mDisplay.append(msg + "\n");
                Log.d(TAG, msg);
            }
        }.execute();
    }

    /**
     * Sends the registration ID to your server over HTTP, so it can use GCM/HTTP
     * or CCS to send messages to your app. Not needed for this demo since the
     * device sends upstream messages to a server that echoes back the message
     * using the 'from' address in the message.
     */
    private void sendRegistrationIdToBackend() {
        // Your implementation here.
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ArrayList<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("deviceToken", mRegistrationId));
                params.add(new BasicNameValuePair("deviceType", "android"));

                new ReadHttpTask(MainActivity.this, Constant.REGISTER_TOKEN, MainActivity.this, null, params)
                        .execute(Constant.REGISTER_DEVICE_URL);
            }
        });
    }

    public void sendMessage(String message) {
        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {
                String msg;
                try {
                    Bundle data = new Bundle();
                    data.putString(EXTRA_MESSAGE, params[0]);
                    data.putString("my_action", "com.google.android.gcm.demo.app.ECHO_NOW");
                    String id = Integer.toString(msgId.incrementAndGet());

                    gcm.send(SENDER_ID + "@gcm.googleapis.com", id, data);

                    msg = "Sent message";
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {

            }
        }.execute(message);
    }

}
