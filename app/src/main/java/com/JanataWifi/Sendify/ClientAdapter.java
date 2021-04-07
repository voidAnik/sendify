package com.JanataWifi.Sendify;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ClientAdapter extends BaseAdapter {
    Context context;
    LayoutInflater inflater;
    ArrayList<ScanResult> wifiList;

    public ClientAdapter(Context context, ArrayList<ScanResult> wifiList) {
        this.context = context;
        this.wifiList = wifiList;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return wifiList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        View view = convertView;

        if(view==null){
            view = inflater.inflate(R.layout.listview_hosts, null);
            holder = new Holder();
            holder.tvWifiDetails = (TextView) view.findViewById(R.id.tv_wifiName);
            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
        }
        holder.tvWifiDetails.setText(wifiList.get(position).SSID);

        return view;
    }

    private static class Holder {
        TextView tvWifiDetails;
    }
}
