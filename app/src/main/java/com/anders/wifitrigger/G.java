package com.anders.wifitrigger;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by anders on 14-10-13.
 */
public class G extends Application {
    public static final boolean DEBUG = true;
    public static final String LOG_TAG = "WifiTrigger";

    //wifitrigger used for starting MainService
    public static final String ACTION_WIFI_CONNECTED = "wifi_connected";
    public static final String ACTION_WIFI_DISCONNECT = "wifi_disconnect";

    public static final String KEY_CONFIG_STATUS_POSTFIX = "_config_status";
    private static final String KEY_LAST_CONNECTED_WIFI = "last_connected_wifi";


    public void setLastConnectedWifi(String wifi_id) {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        mPrefs.edit().putString(KEY_LAST_CONNECTED_WIFI, wifi_id).apply();
    }
    public String getLastConnectedWifi() {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        return mPrefs.getString(KEY_LAST_CONNECTED_WIFI, "");
    }

    public boolean getConfigStatus(String key) {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        return mPrefs.getBoolean(key, false);
    }

    public void setConfigStatus(String key, boolean status) {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        mPrefs.edit().putBoolean(key, status).apply();
    }
}
