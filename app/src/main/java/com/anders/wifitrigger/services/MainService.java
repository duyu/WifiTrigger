package com.anders.wifitrigger.services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.anders.wifitrigger.G;
import com.anders.wifitrigger.settings.SettingsHelper;

import java.util.ArrayList;

public class MainService extends Service {
    private static final String LOG_TAG = MainService.class.getSimpleName();

    final Messenger mMessenger = new Messenger(new IncomingHandler());

    public static final int MSG_ZERO = 0;
    public static final int MSG_REGISTER_CLIENT = 1;
    public static final int MSG_UNREGISTER_CLIENT = 2;
    public static final int MSG_WIFI_CONNECTED = 3;
    public static final int MSG_WIFI_DISCONNECT = 4;

    ArrayList<Messenger> mClients = new ArrayList<Messenger>();

    public MainService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String action = intent == null ? null : intent.getAction();
        if (action == null)
            return super.onStartCommand(intent, flags, startId);

        if (action.equals(G.ACTION_WIFI_CONNECTED)) {
            final String wifi_id = (String) intent.getExtra("WIFI_ID");
            if (((G) getApplication()).getConfigStatus(wifi_id)) {
                if (G.DEBUG)
                    Log.i(LOG_TAG, "wifi connected: " + wifi_id);
                sendMessage(MSG_WIFI_CONNECTED, wifi_id);
                start_triggered_work(wifi_id, true);
            } else
                Log.i(LOG_TAG, "Wifi configuration not enabled: " + wifi_id);
        } else if (action.equals(G.ACTION_WIFI_DISCONNECT)) {
            final String wifi_id = (String) intent.getExtra("WIFI_ID");
            if (((G) getApplication()).getConfigStatus(wifi_id)) {
                if (G.DEBUG)
                    Log.i(LOG_TAG, "wifi disconnect: " + wifi_id);
                sendMessage(MSG_WIFI_DISCONNECT, wifi_id);
                start_triggered_work(wifi_id, false);
            } else
                Log.i(LOG_TAG, "Wifi configuration not enabled: " + wifi_id);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * When binding to the service, we return an interface to our messenger for
     * sending messages to the service.
     */
    @Override
    public IBinder onBind(Intent intent) {
        if (G.DEBUG)
            Log.e(LOG_TAG, "::onBind");
        return mMessenger.getBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        if (G.DEBUG)
            Log.e(LOG_TAG, "::onUnBind");
        return super.onUnbind(intent);
    }

    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_REGISTER_CLIENT:
                    mClients.add(msg.replyTo);
                    break;
                case MSG_UNREGISTER_CLIENT:
                    mClients.remove(msg.replyTo);
                    break;
                case MSG_WIFI_CONNECTED:
                case MSG_WIFI_DISCONNECT:
                    for (int i = mClients.size() - 1; i >= 0; i--) {
                        try {
                            mClients.get(i).send(
                                    Message.obtain(null, msg.what, msg.obj));
                        } catch (RemoteException e) {
                            // The client is dead.
                            mClients.remove(i);
                        }
                    }
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    private void sendMessage(int what, Object obj) {
        Message msg = Message.obtain(null, what, this.hashCode(), 0);
        msg.obj = obj;
        try {
            mMessenger.send(msg);
        } catch (RemoteException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        }

    }

    /*
    * start a thread to do the triggered work
    * Parameter: key - the specified string we used to store
    * */
    private void start_triggered_work(final String wifi_id, final boolean isConnected) {

        new Thread(new Runnable() {
            @Override
            public void run() {

                /*SoundExecutor.getInstance().execute(getBaseContext(), wifi_id, isConnected);

                BluetoothExecutor.getInstance().execute(getBaseContext(), wifi_id, isConnected);*/

                SettingsHelper.execute_all(getBaseContext(), wifi_id, isConnected);

            }
        }).start();

    }
}
