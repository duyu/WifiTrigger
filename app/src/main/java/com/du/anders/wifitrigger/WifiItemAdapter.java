package com.du.anders.wifitrigger;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by anders on 14-10-13.
 */
public class WifiItemAdapter extends ArrayAdapter<WifiConfiguration> {

    private static class ViewHolder {
        TextView name;
        TextView status;
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
            viewHolder.status = (TextView) convertView.findViewById(R.id.is_configured);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // Lookup view for data population
        // Populate the data into the template view using the data object
        viewHolder.name.setText(wifiConfiguration.SSID.replaceAll("^\"|\"$", ""));
        viewHolder.status.setText(wifiConfiguration.networkId + "");
        // Return the completed view to render on screen
        return convertView;
    }
}
