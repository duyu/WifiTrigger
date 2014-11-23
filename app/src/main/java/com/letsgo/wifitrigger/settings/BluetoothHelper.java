package com.letsgo.wifitrigger.settings;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.os.AsyncTask;
import android.preference.Preference;

import com.letsgo.wifitrigger.R;
import com.letsgo.wifitrigger.preferences.IconListPreference;

/**
 * Created by anders on 14-11-18.
 */
public class BluetoothHelper extends SettingsHelper {
    private static final String KEY_CONNECTED_BT_MODE_POSTFIX = "_connected_bt_mode";
    private static final String KEY_DISCONNECT_BT_MODE_POSTFIX = "_disconnect_bt_mode";

    @Override
    protected String getKey(String wifi_id, boolean isConnected) {
        final String key;
        if (isConnected)
            key = wifi_id + KEY_CONNECTED_BT_MODE_POSTFIX;
        else
            key = wifi_id + KEY_DISCONNECT_BT_MODE_POSTFIX;
        return key;
    }

    @Override
    protected Preference getPreference(Context context, String wifi_id, boolean isConnected) {
        final String key = getKey(wifi_id, isConnected);
        // test the new preference
        IconListPreference preference = new IconListPreference(context);
        preference.setTitle(R.string.preference_bt_mode_title);
        preference.setKey(key);
        preference.setEntries(R.array.preferences_bluetooth_entries);
        preference.setEntryIcons(R.array.preferences_bluetooth_entry_icons);
        preference.setEntryValues(R.array.preferences_bluetooth_entry_values);
        preference.setDefaultValue(String.valueOf(DEFAULT_MODE));
        preference.setSummary("%s");
        return preference;
    }

    @Override
    protected void execute(Context context, String wifi_id, boolean isConnected) {
        final int state = getCurrentMode(context, wifi_id, isConnected);

        if( state == DEFAULT_MODE )
            return;

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... args) {
                BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if(mBluetoothAdapter.isEnabled()) {
                    if( state == 0)
                        mBluetoothAdapter.disable();
                } else {
                    if( state == 1)
                        mBluetoothAdapter.enable();
                }
                return null;
            }
        }.execute();
    }
}
