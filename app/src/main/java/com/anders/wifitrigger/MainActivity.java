package com.anders.wifitrigger;

import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.anders.wifitrigger.services.MainService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class MainActivity extends ListActivity {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    boolean mIsBound;
    final Messenger mMessenger = new Messenger(new IncomingHandler());
    Messenger mService = null;

    private ListView mListView;
    private TextView mTextView;
    private ProgressBar mSpinner;

    private WifiManager mWifiManager;

    private List<String> mWifiList = new ArrayList<String>();
    private boolean bPaused;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mListView = getListView();
        mTextView = (TextView) findViewById(R.id.text_view);

        mSpinner = (ProgressBar) findViewById(R.id.spinner);

        mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        bPaused = false;

        new LoadWifiListTask().execute();
    }

    private void loadWifiListView() {
        mTextView.setVisibility(View.GONE);
        mSpinner.setVisibility(View.GONE);
        if (mWifiList.isEmpty()) {
            if (mWifiManager.isWifiEnabled()) {
                // wifi is on, but no available wifi stored, show the message
                mTextView.setText(R.string.no_wifi_remembered);
                mTextView.setVisibility(View.VISIBLE);
                return;
            } else {
                // show message to turn the wifi on
                mTextView.setText(R.string.wifi_not_enabled);
                mTextView.setVisibility(View.VISIBLE);
                mTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // register a wifi enabled listener
                        MainActivity.this.registerReceiver(
                                new BroadcastReceiver() {
                                    @Override
                                    public void onReceive(Context context, Intent intent) {
                                        int extraWifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE,
                                                WifiManager.WIFI_STATE_UNKNOWN);
                                        if (extraWifiState == WifiManager.WIFI_STATE_ENABLED) {
                                            if (mWifiList.isEmpty())
                                                new LoadWifiListTask().execute();
                                            context.unregisterReceiver(this);
                                        }
                                    }
                                },
                                new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION));
                        mTextView.setVisibility(View.GONE);
                        mSpinner.setVisibility(View.VISIBLE);
                        mWifiManager.setWifiEnabled(true);
                    }
                });
                return;
            }
        }

        WifiItemAdapter adapter = new WifiItemAdapter(this, R.layout.wifi_list_item, mWifiList);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                String network = (String) parent.getItemAtPosition(position);
                Intent i = new Intent(view.getContext(), ConfigureActivity.class);
                i.putExtra(ConfigureActivity.EXTRA_NETWORK, network);
                startActivity(i);
            }

        });

        mTextView.setVisibility(View.GONE);
        mSpinner.setVisibility(View.GONE);
        mListView.setAdapter(adapter);

    }


    @Override
    protected void onResume() {
        if (!mIsBound)
            doBindService();
        if (bPaused)
            loadWifiListView();
        super.onResume();
    }

    @Override
    protected void onPause() {
        bPaused = true;
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        doUnbindService();
        super.onDestroy();
    }

    void doBindService() {
        if (G.DEBUG)
            Log.d(LOG_TAG, "bind main service");
        bindService(new Intent(this, MainService.class),
                mServiceConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    ServiceConnection mServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName name, IBinder service) {
            if (G.DEBUG)
                Log.d(LOG_TAG, "::onServiceConnected");
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
            if (G.DEBUG)
                Log.d(LOG_TAG, "::onServiceDisconnected");
            mService = null;
            mIsBound = false;
        }
    };

    void doUnbindService() {
        if (G.DEBUG)
            Log.d(LOG_TAG, "::doUnbindService");
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
        }
    }

    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MainService.MSG_WIFI_CONNECTED:
                    final String wifi_id = msg.obj.toString();
                    if (G.DEBUG)
                        Log.e(LOG_TAG, wifi_id + "");
                default:
                    break;
            }
        }
    }

    private class LoadWifiListTask extends AsyncTask<Void, Void, List<String>> {

        @Override
        protected void onPreExecute() {
            mSpinner.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<String> doInBackground(Void... params) {
            final List<String> wifiList = new ArrayList<String>();
            G gApp = (G) getApplicationContext();

            Set<String> storedWifiSet = new HashSet<String>(gApp.getStoredWifiList());
            boolean bUpdateNeeded = false;
            if (mWifiManager.isWifiEnabled()) {
                for (WifiConfiguration item : mWifiManager.getConfiguredNetworks()) {
                    // initialize the wifiList, and also update the stored preferences
                    String wifi_id = item.SSID.replaceAll("^\"|\"$", "");
                    wifiList.add(wifi_id);
                    if (!storedWifiSet.contains(wifi_id)) {
                        storedWifiSet.add(wifi_id);
                        bUpdateNeeded = true;
                    }
                }
            }

            if (wifiList.isEmpty()) {
                // get wifi list from old records
                wifiList.addAll(storedWifiSet);
            } else if (storedWifiSet.size() > wifiList.size()) {
                // if the stored wifi set is bigger than the wifiList we just got
                // there's some wifi deleted by user already, we need to delete it also here
                for (String item : storedWifiSet) {
                    if (!wifiList.contains(item)) {
                        gApp.deleteWifiPreference(item);
                        bUpdateNeeded = true;
                    }
                }
            }

            if (bUpdateNeeded)
                gApp.setStoredWifiList(new HashSet<String>(wifiList));

            return wifiList;
        }

        @Override
        protected void onPostExecute(List<String> wifiList) {
            mWifiList.clear();
            mWifiList = wifiList;
            loadWifiListView();
        }
    }
}
