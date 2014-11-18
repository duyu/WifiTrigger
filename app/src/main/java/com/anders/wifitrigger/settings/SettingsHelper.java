package com.anders.wifitrigger.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anders on 14-11-18.
 */
public abstract class SettingsHelper {
    private static final String LOG_TAG = SettingsHelper.class.getSimpleName();

    protected static final int DEFAULT_MODE = -1;

    private static final ArrayList<Class <? extends SettingsHelper>> SETTINGS_CLASS_LIST = new ArrayList<Class<? extends SettingsHelper>>();
    static {
        SETTINGS_CLASS_LIST.add(SoundHelper.class);
        SETTINGS_CLASS_LIST.add(BluetoothHelper.class);
    }
    private static final ArrayList<SettingsHelper> LOADED_SETTINGS = new ArrayList<SettingsHelper>();

    private static void loadSettings() {
        if(LOADED_SETTINGS.isEmpty()) {
            Log.e(LOG_TAG, "Load Settings");
            synchronized (LOADED_SETTINGS) {
                if (LOADED_SETTINGS.isEmpty()) {
                    for (Class<? extends SettingsHelper> aSettingsList : SETTINGS_CLASS_LIST) {
                        try {
                            LOADED_SETTINGS.add(aSettingsList.newInstance());
                        } catch (Exception e) {
                            Log.e(LOG_TAG, "Error loading settings: " + aSettingsList.getSimpleName());
                        }
                    }
                }
            }
        }
    }
    public static List<Preference> getPreferences(Context context, String wifi_id, boolean isConnected) {
        loadSettings();
        List<Preference> preference_list = new ArrayList<Preference>();
        for(SettingsHelper item : LOADED_SETTINGS) {
            preference_list.add(item.getPreference(context, wifi_id, isConnected));
        }
        return preference_list;
    }
    public static void execute_all(Context context, String wifi_id, boolean isConnected) {
        loadSettings();
        for(SettingsHelper item : LOADED_SETTINGS) {
            item.execute(context, wifi_id, isConnected);
        }
    }


    protected int getCurrentMode(Context context, String wifi_id, boolean isConnected) {
        final String key = getKey(wifi_id, isConnected);
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        return Integer.parseInt(mPrefs.getString(key, String.valueOf(DEFAULT_MODE)));
    }

    protected abstract String getKey(String wifi_id, boolean isConnected);
    protected abstract Preference getPreference(Context context, String wifi_id, boolean isConnected);
    protected abstract void execute(Context context, String wifi_id, boolean isConnected);
}
