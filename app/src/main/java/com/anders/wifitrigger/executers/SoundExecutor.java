package com.anders.wifitrigger.executers;


import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.preference.Preference;
import android.preference.PreferenceManager;

import com.anders.wifitrigger.R;
import com.anders.wifitrigger.preferences.IconListPreference;

public class SoundExecutor extends BaseExecutor {

    private static final String LOG_TAG = SoundExecutor.class.getSimpleName();

    private static volatile SoundExecutor INSTANCE = null;

    private static int DEFAULT_SOUND_MODE = -1;

    private final Ringer mSilentRinger = new Ringer(false, AudioManager.VIBRATE_SETTING_OFF,
            AudioManager.RINGER_MODE_SILENT, false);
    private final Ringer mVibrateRinger = new Ringer(true, AudioManager.VIBRATE_SETTING_ONLY_SILENT,
            AudioManager.RINGER_MODE_VIBRATE, true);
    private final Ringer mSoundRinger = new Ringer(true, AudioManager.VIBRATE_SETTING_ONLY_SILENT,
            AudioManager.RINGER_MODE_NORMAL, false);
    private final Ringer mSoundVibrateRinger = new Ringer(true, AudioManager.VIBRATE_SETTING_ON,
            AudioManager.RINGER_MODE_NORMAL, true);
    private final Ringer[] mRingers = new Ringer[]{
            mSilentRinger, mVibrateRinger, mSoundRinger, mSoundVibrateRinger
    };


    private AudioManager mAudioManager;

    public SoundExecutor() {
    }

    //thread safe and performance  promote 
    public static SoundExecutor getInstance() {
        if (INSTANCE == null) {
            synchronized (SoundExecutor.class) {
                //when more than two threads run into the first null check same time, to avoid instanced more than one time, it needs to be checked again.
                if (INSTANCE == null) {
                    INSTANCE = new SoundExecutor();
                }
            }
        }
        return INSTANCE;
    }

    public static Preference getPreference(Context context, String key) {
        // test the new preference
        IconListPreference preference = new IconListPreference(context);
        preference.setTitle("Sound Mode");
        preference.setKey(key);
        preference.setEntries(R.array.preferences_sound_mode_entries);
        preference.setEntryIcons(R.array.preferences_sound_mode_entry_icons);
        preference.setEntryValues(R.array.preferences_sound_mode_entry_values);
        preference.setDefaultValue(String.valueOf(DEFAULT_SOUND_MODE));
        preference.setSummary("%s");
        return preference;
    }

    public static int getSoundMode(Context context, String key) {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        return Integer.parseInt(mPrefs.getString(key, String.valueOf(DEFAULT_SOUND_MODE)));
    }

    @Override
    public void execute(Context context, int state) {
        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (state >= 0 && state < mRingers.length)
            mRingers[state].execute(context);
    }

    private class Ringer {
        final boolean mVibrateInSilent;
        final int mVibrateSetting;
        final int mRingerMode;
        final boolean mDoHapticFeedback;

        Ringer(boolean vibrateInSilent, int vibrateSetting, int ringerMode, boolean doHapticFeedback) {
            mVibrateInSilent = vibrateInSilent;
            mVibrateSetting = vibrateSetting;
            mRingerMode = ringerMode;
            mDoHapticFeedback = doHapticFeedback;
        }

        void execute(Context context) {
            /*ContentResolver resolver = context.getContentResolver();
            Settings.System.putInt(resolver, Settings.System.VIBRATE_IN_SILENT,
                    (mVibrateInSilent ? 1 : 0));*/
            mAudioManager.setVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER, mVibrateSetting);
            mAudioManager.setRingerMode(mRingerMode);
        }
    }

}
