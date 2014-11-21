package com.anders.wifitrigger;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.anders.wifitrigger.settings.SettingsHelper;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by anders on 14-10-13.
 */
public class G extends Application {
    public static final boolean DEBUG = true;
    public static final String LOG_TAG = "WifiTrigger";

    //wifitrigger used for starting MainService
    public static final String ACTION_WIFI_CONNECTED = "wifi_connected";
    public static final String ACTION_WIFI_DISCONNECT = "wifi_disconnect";

    private static final String KEY_CONFIG_STATUS_POSTFIX = "_config_status";
    private static final String KEY_LAST_CONNECTED_WIFI = "last_connected_wifi";
    private static final String KEY_WIFI_LIST_STORED = "wifi_list_stored";

    public void setLastConnectedWifi(String wifi_id) {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        mPrefs.edit().putString(KEY_LAST_CONNECTED_WIFI, wifi_id).apply();
    }
    public String getLastConnectedWifi() {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        return mPrefs.getString(KEY_LAST_CONNECTED_WIFI, "");
    }

    public boolean getConfigStatus(String wifi_id) {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String key = wifi_id + G.KEY_CONFIG_STATUS_POSTFIX;
        return mPrefs.getBoolean(key, false);
    }

    public void setConfigStatus(String wifi_id, boolean status) {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String key = wifi_id + G.KEY_CONFIG_STATUS_POSTFIX;
        mPrefs.edit().putBoolean(key, status).apply();
    }

    public void setStoredWifiList(Set<String> values) {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        mPrefs.edit().putStringSet(KEY_WIFI_LIST_STORED, values).apply();
    }

    public Set<String> getStoredWifiList() {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        return mPrefs.getStringSet(KEY_WIFI_LIST_STORED, new HashSet<String>());
    }

    public void deleteWifiPreference(String wifi_id) {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        mPrefs.edit().remove(wifi_id + G.KEY_CONFIG_STATUS_POSTFIX).apply();
        SettingsHelper.delWifiData(getBaseContext(), wifi_id);
    }
}
