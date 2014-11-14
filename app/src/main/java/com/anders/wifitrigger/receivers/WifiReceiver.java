package com.anders.wifitrigger.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.anders.wifitrigger.G;
import com.anders.wifitrigger.services.MainService;

public class WifiReceiver extends BroadcastReceiver {
    private static final String LOG_TAG = WifiReceiver.class.getSimpleName();

    private static String last_connected_wifi_id = "";

    public WifiReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        String action = intent.getAction();

        if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {

            NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            NetworkInfo.State state = networkInfo.getState();

            String wifi_id = "";
            String trigger_action = "";
            if (state == NetworkInfo.State.CONNECTED && last_connected_wifi_id.isEmpty()) {
                WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                wifi_id = manager.getConnectionInfo().getSSID().replace("\"", "");
                trigger_action = G.ACTION_WIFI_CONNECTED;
                last_connected_wifi_id = wifi_id;
            } else if (state == NetworkInfo.State.DISCONNECTED && !last_connected_wifi_id.isEmpty()) {
                wifi_id = last_connected_wifi_id;
                trigger_action = G.ACTION_WIFI_DISCONNECT;
                last_connected_wifi_id = "";
                WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                Log.i(LOG_TAG, manager.toString());
            }

            Log.i(LOG_TAG, wifi_id + " - " + trigger_action);

            if (!trigger_action.isEmpty()
                    && ((G) context.getApplicationContext()).getConfigStatus(wifi_id + G.KEY_CONFIG_STATUS_POSTFIX)) {
                Intent schedulerServiceIntent = new Intent(context, MainService.class);
                schedulerServiceIntent.setAction(trigger_action);
                schedulerServiceIntent.putExtra("WIFI_ID", wifi_id);
                context.startService(schedulerServiceIntent);
            }
        }


    }
}
