package com.du.anders.wifitrigger.services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.du.anders.wifitrigger.G;

import java.util.ArrayList;

public class MainService extends Service {
    final Messenger mMessenger = new Messenger(new IncomingHandler());

    public static final int MSG_ZERO = 0;
    public static final int MSG_REGISTER_CLIENT = 1;
    public static final int MSG_UNREGISTER_CLIENT = 2;
    public static final int MSG_CONDITION_MET = 3;

    ArrayList<Messenger> mClients = new ArrayList<Messenger>();

    public MainService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String action = intent == null ? null : intent.getAction();
        if (action != null && action.equals(G.ACTION_WIFI_CHANGED)) {
            final String wifi_id = (String) intent.getExtra("WIFI_ID");
            if(((G)getApplication()).getConfigStatus(wifi_id+G.KEY_CONFIG_STATUS_POSTFIX)){
                Log.i(G.LOG_TAG, "Condition met: " + wifi_id);
                sendMessage(MSG_CONDITION_MET, wifi_id);
            }
            else
                Log.i(G.LOG_TAG, "Wifi configuration not enabled: " + wifi_id);
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
            Log.e(G.LOG_TAG, "::onBind");
        return mMessenger.getBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        if (G.DEBUG)
            Log.e(G.LOG_TAG, "::onUnBind");
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
                case MSG_CONDITION_MET:
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
            Log.e(G.LOG_TAG, e.getMessage(), e);
        }

    }
}
