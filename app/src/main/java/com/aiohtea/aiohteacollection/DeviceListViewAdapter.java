package com.aiohtea.aiohteacollection;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Nguyen Truong on 3/30/2017.
 */

public class DeviceListViewAdapter extends ArrayAdapter<DeviceListItem> {

    /*private view holder class*/
    private class ViewHolder {
        ImageView   m_itemIcon;
        TextView    m_itemName;
        TextView    m_itemDesc;
    }

    private Context m_context;

    public DeviceListViewAdapter(Context context, int resource, List<DeviceListItem> listItems) {
        super(context, resource, listItems);

        this.m_context = context;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        DeviceListItem rowItem = getItem(position);

        LayoutInflater mInflater =
                (LayoutInflater) m_context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.device_list_item, null);
            holder = new ViewHolder();
            holder.m_itemName = (TextView) convertView.findViewById(R.id.item_name);
            holder.m_itemDesc = (TextView) convertView.findViewById(R.id.item_desc);
            holder.m_itemIcon = (ImageView) convertView.findViewById(R.id.item_icon);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        holder.m_itemName.setText(rowItem.getDeviceName());
        holder.m_itemDesc.setText(rowItem.getDeviceDesc());
        holder.m_itemIcon.setImageResource(rowItem.getStatusImgRscId());

        return convertView;
    }
}
