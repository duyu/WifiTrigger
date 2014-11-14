
package com.anders.wifitrigger.executers;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.media.AudioManager;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;

import com.anders.wifitrigger.R;

public class SoundExecutor extends BaseExecutor {

    private static final String LOG_TAG = SoundExecutor.class.getSimpleName();

    private static volatile SoundExecutor INSTANCE = null;

    private static CharSequence[] SOUND_MODE_ENTRIES = { "NO CHANGE(Default)", "SILENT", "Only Vibrate", "Only Sound", "Vibrate & Sound" };
    private static CharSequence[] SOUND_MODE_ENTRYVALUES = {"-1", "0", "1", "2", "3"};
    private static int DEFAULT_SOUND_MODE = -1;

    private final Ringer mSilentRinger = new Ringer(false, AudioManager.VIBRATE_SETTING_OFF,
            AudioManager.RINGER_MODE_SILENT, false);
    private final Ringer mVibrateRinger = new Ringer(true, AudioManager.VIBRATE_SETTING_ONLY_SILENT,
            AudioManager.RINGER_MODE_VIBRATE, true);
    private final Ringer mSoundRinger = new Ringer(true, AudioManager.VIBRATE_SETTING_ONLY_SILENT,
            AudioManager.RINGER_MODE_NORMAL, false);
    private final Ringer mSoundVibrateRinger = new Ringer(true, AudioManager.VIBRATE_SETTING_ON,
            AudioManager.RINGER_MODE_NORMAL, true);
    private final Ringer[] mRingers = new Ringer[] {
            mSilentRinger, mVibrateRinger, mSoundRinger, mSoundVibrateRinger
    };


    private AudioManager mAudioManager;

    public SoundExecutor() {
    }

    //thread safe and performance  promote 
    public static SoundExecutor getInstance() {
        if(INSTANCE == null){
            synchronized(SoundExecutor.class){
                //when more than two threads run into the first null check same time, to avoid instanced more than one time, it needs to be checked again.
                if(INSTANCE == null){
                    INSTANCE = new SoundExecutor();
                }
            }
        }
        return INSTANCE;
    }

    public static Preference getPreference(Context context, String key) {
        Resources res = context.getResources();

        ListPreference listPreference = new ListPreference(context);

        listPreference.setTitle(res.getString(R.string.preference_sound_mode_title));
        listPreference.setDialogTitle(res.getString(R.string.preference_sound_mode_title));
        listPreference.setKey(key);

        listPreference.setEntries(SOUND_MODE_ENTRIES);
        listPreference.setEntryValues(SOUND_MODE_ENTRYVALUES);
        listPreference.setDefaultValue(String.valueOf(DEFAULT_SOUND_MODE));
        listPreference.setSummary("%s");//this will show the selected option as summary

        return listPreference;
    }

    public static int getSoundMode(Context context, String key) {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        return Integer.parseInt(mPrefs.getString(key, String.valueOf(DEFAULT_SOUND_MODE)));
    }

    @Override
    public void execute(Context context, int state) {
        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
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
