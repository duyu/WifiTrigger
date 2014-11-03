package com.du.anders.wifitrigger.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.du.anders.wifitrigger.G;
import com.du.anders.wifitrigger.services.MainService;

public class WifiReceiver extends BroadcastReceiver {
    public WifiReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
        if (info != null) {
            if (info.isConnected()) {
                //To check the Network Name or other info:
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                final String wifi_id = wifiInfo.getSSID().replaceAll("^\"|\"$", "");
                Log.i(G.LOG_TAG, "wifi connected: " + wifi_id);
                Intent schedulerServiceIntent = new Intent(context, MainService.class);
                schedulerServiceIntent.setAction(G.ACTION_WIFI_CHANGED);
                schedulerServiceIntent.putExtra("WIFI_ID", wifi_id);
                context.startService(schedulerServiceIntent);
            }
        }
    }
}
