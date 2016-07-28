/**
 * @author LuYongXing
 * @date 2015.01.13
 * @filename SplashActivity.java
 */

package com.camerarental.crc;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.aplit.dev.listeners.ReadHttpTaskListener;
import com.aplit.dev.tasks.ReadHttpTask;
import com.camerarental.crc.utils.CommonUtils;
import com.camerarental.crc.utils.Constant;

public class SplashActivity extends Activity implements ReadHttpTaskListener {

    static final String TAG = SplashActivity.class.getSimpleName();

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        CommonUtils.clearBadge(this);

        new ReadHttpTask(this, Constant.VERSION_CHECK, this, null).execute(Constant.EQUIPMENT_VERSION_URL);
        Log.d(TAG, TAG + " was created!");
    }

    @Override
    public void onObtainedHttpContent(int apiIndex, String result) {
        if (apiIndex == Constant.VERSION_CHECK) {
            if (TextUtils.isEmpty(result) || "Connection timeout.".equals(result)) {
                CommonUtils.createErrorAlertDialog(this, "No Internet!",
                        "No working internet connection is found.\nIf Wi-Fi is enabled, try disabling Wi-Fi or try another Wi-Fi hotspot.").show();
                return;
            }

            String currentVersion = CommonUtils.getAppVersion(this) + "\n";

            if (currentVersion.equals(result)) {
                CommonUtils.moveNextActivity(this, MainActivity.class, true);
            } else {
                CommonUtils.moveNextActivity(this, UpdateActivity.class, true);
            }
        }
    }

}
