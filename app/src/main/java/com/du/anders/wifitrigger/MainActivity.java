package com.du.anders.wifitrigger;

import android.app.ListActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.du.anders.wifitrigger.services.MainService;

import java.util.List;


public class MainActivity extends ListActivity {

    boolean mIsBound;
    final Messenger mMessenger = new Messenger(new IncomingHandler());
    Messenger mService = null;

    private ListView mListView;

    WifiManager mWifiManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // Create a progress bar to display while the list loads
        /*ProgressBar progressBar = new ProgressBar(this);
        progressBar.setLayoutParams(new AbsListView.LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT, Gravity.CENTER));
        progressBar.setIndeterminate(true);*/

        mListView = getListView();

//        mListView.setEmptyView(progressBar);


        mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        loadWifiList();
    }

    private void loadWifiList() {
        final List<WifiConfiguration> wifiList = mWifiManager.getConfiguredNetworks();

        if (wifiList == null)
            return;
        Log.e(G.LOG_TAG, "::wifiList returned: " + wifiList.size());

        WifiItemAdapter adapter = new WifiItemAdapter(this, R.layout.wifi_list_item, wifiList);

        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                Log.i(G.LOG_TAG, "list item clicked.");
                final String network = wifiList.get(position).SSID.replaceAll("^\"|\"$", "");
                //String notes = (String) arg1.getTag();
                //String version = ((TextView) arg1.findViewById(R.id.update_version)).getText().toString();

                Intent i = new Intent(view.getContext(), ConfigureActivity.class);
                i.putExtra(ConfigureActivity.EXTRA_NETWORK, network);
                //i.putExtra(ReleaseNoteActivity.KEY_BODY, (notes));
                //i.putExtra(ReleaseNoteActivity.KEY_TITLE, version);
                startActivity(i);
            }

        });
    }


    @Override
    protected void onResume() {
        if (!mIsBound)
            doBindService();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        doUnbindService();
        super.onDestroy();
    }

    void doBindService() {
        if (G.DEBUG)
            Log.d(G.LOG_TAG, "bind main service");
        bindService(new Intent(this, MainService.class),
                mServiceConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    ServiceConnection mServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(G.LOG_TAG, "::onServiceConnected");
            try {
                // Register with the service
                mService = new Messenger(service);
                Message msg = Message.obtain(null, MainService.MSG_REGISTER_CLIENT);
                msg.replyTo = mMessenger;
                mService.send(msg);
            } catch (RemoteException e) {
                // In this case the service has crashed before we could even
                // do anything with it; we can count on soon being
                // disconnected (and then reconnected if it can be restarted)
                // so there is no need to do anything here.
            }
        }

        public void onServiceDisconnected(ComponentName name) {
            Log.d(G.LOG_TAG, "::onServiceDisconnected");
            mService = null;
            mIsBound = false;
        }
    };

    void doUnbindService() {
        if (G.DEBUG)
            Log.d(G.LOG_TAG, "::doUnbindService");
        if (mIsBound) {
            if (mService != null) {
                try {
                    Message msg = Message.obtain(null,
                            MainService.MSG_UNREGISTER_CLIENT);
                    msg.replyTo = mMessenger;
                    mService.send(msg);
                } catch (RemoteException e) {
                    // There is nothing special we need to do if the service has
                    // crashed.
                    e.printStackTrace();
                }
            }
            unbindService(mServiceConnection);
            mIsBound = false;
            Log.d(G.LOG_TAG, " Service unbinding");
        }
    }

    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MainService.MSG_CONDITION_MET:
                    int networkid = Integer.parseInt(msg.obj.toString());
                    Log.e(G.LOG_TAG, networkid + "");
                default:
                    break;
            }
        }
    }
}
