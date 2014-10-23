package com.du.anders.wifitrigger;

/**
 * Created by anders on 14-10-13.
 */
public class G {
    public static final boolean DEBUG = true;
    public static final String LOG_TAG = "WifiTrigger";

    //wifitrigger used for starting MainService
    public static final String ACTION_CONDITION_MET = "condition_met";


    public static final String KEY_CONNECTED_SOUND_POSTFIX = "connected_sound";
    public static final String KEY_CONNECTED_VIBRATE_POSTFIX = "connected_vibrator";

    public static final String KEY_DISCONNECT_SOUND_POSTFIX = "disconnect_sound";
    public static final String KEY_DISCONNECT_VIBRATE_POSTFIX = "disconnect_vibrator";

    public static final int PREFERENCE_CONNECTED_VIBRATE_ON = 0;
    public static final int PREFERENCE_CONNECTED_VIBRATE_OFF = 1;
    public static final int PREFERENCE_CONNECTED_VIBRATE_NO_CHANGE = 2;
}
