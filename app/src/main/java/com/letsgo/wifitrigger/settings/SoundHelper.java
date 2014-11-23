package com.letsgo.wifitrigger.settings;

import android.content.Context;
import android.media.AudioManager;
import android.preference.Preference;

import com.letsgo.wifitrigger.R;
import com.letsgo.wifitrigger.preferences.IconListPreference;

/**
 * Created by anders on 14-11-18.
 */
public class SoundHelper extends SettingsHelper {
    private static final String KEY_CONNECTED_SOUND_MODE_POSTFIX = "_connected_sound_mode";
    private static final String KEY_DISCONNECT_SOUND_MODE_POSTFIX = "_disconnect_sound_mode";

    @Override
    protected String getKey(String wifi_id, boolean isConnected) {
        final String key;
        if (isConnected)
            key = wifi_id + KEY_CONNECTED_SOUND_MODE_POSTFIX;
        else
            key = wifi_id + KEY_DISCONNECT_SOUND_MODE_POSTFIX;
        return key;
    }

    @Override
    protected Preference getPreference(Context context, String wifi_id, boolean isConnected) {
        final String key = getKey(wifi_id, isConnected);
        // test the new preference
        IconListPreference preference = new IconListPreference(context);
        preference.setTitle(R.string.preference_sound_mode_title);
        preference.setKey(key);
        preference.setEntries(R.array.preferences_sound_mode_entries);
        preference.setEntryIcons(R.array.preferences_sound_mode_entry_icons);
        preference.setEntryValues(R.array.preferences_sound_mode_entry_values);
        preference.setDefaultValue(String.valueOf(DEFAULT_MODE));
        preference.setSummary("%s");
        return preference;
    }


    @Override
    protected void execute(Context context, String wifi_id, boolean isConnected) {
        int state = getCurrentMode(context, wifi_id, isConnected);
        AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int mVibrateSetting, mRingerMode;
        switch (state) {
            case -1:
                return;
            case 0:
                mVibrateSetting = AudioManager.VIBRATE_SETTING_OFF;
                mRingerMode = AudioManager.RINGER_MODE_SILENT;
                break;
            case 1:
                mVibrateSetting = AudioManager.VIBRATE_SETTING_ONLY_SILENT;
                mRingerMode = AudioManager.RINGER_MODE_VIBRATE;
                break;
            case 2:
                mVibrateSetting = AudioManager.VIBRATE_SETTING_ONLY_SILENT;
                mRingerMode = AudioManager.RINGER_MODE_NORMAL;
                break;
            case 4:
                mVibrateSetting = AudioManager.VIBRATE_SETTING_ON;
                mRingerMode = AudioManager.RINGER_MODE_NORMAL;
                break;
            default:
                return;
        }
        mAudioManager.setVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER, mVibrateSetting);
        mAudioManager.setRingerMode(mRingerMode);
    }
}
