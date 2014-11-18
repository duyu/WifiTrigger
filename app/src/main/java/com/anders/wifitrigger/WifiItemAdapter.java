package com.anders.wifitrigger;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import java.util.List;

/**
 * Created by anders on 14-10-13.
 */
public class WifiItemAdapter extends ArrayAdapter<WifiConfiguration> {
    private static final String LOG_TAG = WifiItemAdapter.class.getSimpleName();

    private static class ViewHolder {
        TextView name;
        //        TextView status;
        Switch toggleButton;
    }

    Context mContext;
    int mLayoutResourceId;


    public WifiItemAdapter(Context mContext, int layoutResourceId, List<WifiConfiguration> wifiList) {
        super(mContext, layoutResourceId, wifiList);
        this.mLayoutResourceId = layoutResourceId;
        this.mContext = mContext;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        WifiConfiguration wifiConfiguration = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            convertView = inflater.inflate(mLayoutResourceId, parent, false);
            viewHolder.name = (TextView) convertView.findViewById(R.id.network_ssid);
//            viewHolder.status = (TextView) convertView.findViewById(R.id.is_configured);
            viewHolder.toggleButton = (Switch) convertView.findViewById(R.id.togglebutton);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // Lookup view for data population
        // Populate the data into the template view using the data object
        final String wifi_id = wifiConfiguration.SSID.replaceAll("^\"|\"$", "");
        final String key = wifi_id + G.KEY_CONFIG_STATUS_POSTFIX;
        viewHolder.name.setText(wifi_id);
//        viewHolder.status.setText(wifiConfiguration.networkId + "");
        viewHolder.toggleButton.setChecked(((G) ((Activity) mContext).getApplication()).getConfigStatus(key));
        viewHolder.toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // do something, the isChecked will be
                // true if the switch is in the On position
                ((G) ((Activity) mContext).getApplication()).setConfigStatus(key, isChecked);
            }
        });
        // Return the completed view to render on screen
        return convertView;
    }
}
