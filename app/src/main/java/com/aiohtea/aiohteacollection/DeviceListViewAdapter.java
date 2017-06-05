package com.aiohtea.aiohteacollection;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
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

    // Holder class
    private class ViewHolder {
        ImageView   m_itemIcon;
        ImageView   m_detailsButton;
        TextView    m_itemName;
        TextView    m_itemDesc;
        TextView    m_itemStatus;
    }

    // Class to implement onClick behavior when user click on DEVICE'S ICON
    private class ItemIconOnClickListener implements View.OnClickListener {

        private DeviceListItem m_clickedDevice;

        ItemIconOnClickListener(DeviceListItem clickedDevice){
            m_clickedDevice = clickedDevice;
        }

        @Override
        public void onClick(View v) {
            m_clickedDevice.iconClicked(m_context);
        }
    }

    // Class implement onClick behavior when user cliek on 3 DOTS ICON
    private class Item3DotsOnClickListener implements View.OnClickListener {

        private DeviceListItem m_clickedDevice;

        Item3DotsOnClickListener(DeviceListItem clickedDevice){
            m_clickedDevice = clickedDevice;
        }

        @Override
        public void onClick(View v) {

        }
    }

    private MainActivity m_context;

    public DeviceListViewAdapter(MainActivity context, int resource, List<DeviceListItem> listItems) {
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

            holder.m_itemIcon = (ImageView) convertView.findViewById(R.id.item_icon);
            holder.m_detailsButton = (ImageView) convertView.findViewById(R.id.details_button);
            holder.m_itemName = (TextView) convertView.findViewById(R.id.item_name);
            holder.m_itemDesc = (TextView) convertView.findViewById(R.id.item_desc);
            holder.m_itemStatus = (TextView) convertView.findViewById(R.id.item_status);

            holder.m_itemIcon.setOnClickListener(new ItemIconOnClickListener(rowItem));
            holder.m_itemStatus.setOnClickListener(new Item3DotsOnClickListener(rowItem));

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.m_itemName.setText(rowItem.getDeviceName());
        holder.m_itemDesc.setText(rowItem.getDeviceDesc());
        holder.m_itemStatus.setText(rowItem.getDeviceStatusText(m_context));
        holder.m_itemIcon.setImageResource(rowItem.getStatusImgRscId());


        return convertView;
    }
}
