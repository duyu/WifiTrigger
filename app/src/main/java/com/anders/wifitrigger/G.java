package com.anders.wifitrigger;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

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

    public static final String KEY_CONNECTED_SOUND_MODE_POSTFIX = "_connected_sound_mode";
    public static final String KEY_DISCONNECT_SOUND_MODE_POSTFIX = "_disconnect_sound_mode";


    public boolean getConfigStatus(String key){
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        return mPrefs.getBoolean(key, false);
    }

    public void setConfigStatus(String key, boolean status)
    {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        mPrefs.edit().putBoolean(key, status).apply();
    }
}
