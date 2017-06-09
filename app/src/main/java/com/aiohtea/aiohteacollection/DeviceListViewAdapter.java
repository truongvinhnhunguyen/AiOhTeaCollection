package com.aiohtea.aiohteacollection;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.List;

/**
 * Created by Nguyen Truong on 3/30/2017.
 */

public class DeviceListViewAdapter extends ArrayAdapter<DeviceListItem> {

    private MainActivity m_activity;

    // Holder class
    private class ViewHolder {
        ImageView   m_itemIcon;
        ImageView   m_detailsButton;
        TextView    m_itemName;
        TextView    m_itemDesc;
        TextView    m_itemStatus;
        TextView    m_onEvery;
        TextView    m_offEvery;
        TextView    m_onAt;
        TextView    m_offAt;
        ImageView   m_timerButton;
    }

    // Class to implement onClick behavior when user click on DEVICE'S ICON
    private class ItemIconOnClickListener implements View.OnClickListener {

        private DeviceListItem m_clickedDevice;

        ItemIconOnClickListener(DeviceListItem clickedDevice){
            m_clickedDevice = clickedDevice;
        }

        @Override
        public void onClick(View v) {
            m_clickedDevice.iconClicked(m_activity);
        }
    }

    // Class to implement onClick behavior when user click on 3 DOTS ICON
    private class Item3DotsOnClickListener implements View.OnClickListener {

        private DeviceListItem m_clickedDevice;

        Item3DotsOnClickListener(DeviceListItem clickedDevice){
            m_clickedDevice = clickedDevice;
        }

        @Override
        public void onClick(View v) {

        }
    }

    // Class to implement onClick behavior when user click on TIMER VALUE AREA
    private class ItemTimerValueOnClickListener implements View.OnClickListener {

        private DeviceListItem m_clickedDevice;

        ItemTimerValueOnClickListener (DeviceListItem clickedDevice){
            m_clickedDevice = clickedDevice;
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(m_activity, TimerSettingsActivity.class);
            intent.putExtra("HW_SETTINGS", m_clickedDevice.getHwSettingPayloadString());
            intent.putExtra("HW_SETTINGS_DEV_NAME", m_clickedDevice.getDeviceName());

            m_activity.startActivityForResult(intent, 3);
        }
    }

    // Class to implement onClick behavior when user click on TIMER BUTTON
    private class ItemTimerButtonOnClickListener implements View.OnClickListener {

        private DeviceListItem m_clickedDevice;
        private View m_view;

        private class Xxx implements DialogInterface.OnClickListener{
            Xxx (View v){
                m_view = v;
            }

            public void onClick(DialogInterface dialog, int id) {
                TimePicker tp = (TimePicker) m_view.findViewById(R.id.time_picker);

                String s = tp.getHour() + ":" + tp.getMinute();

                m_activity.myToast(m_activity, s);

                // User clicked OK button
            }
        }

        ItemTimerButtonOnClickListener(DeviceListItem clickedDevice){
            m_clickedDevice = clickedDevice;
        }

        @Override
        public void onClick(View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(m_activity);
            // builder.setMessage("Dialog Message");
            // builder.setTitle("Dialog Title");

            LayoutInflater inflater = m_activity.getLayoutInflater();
            View v1 = inflater.inflate(R.layout.timer_settings, null);
            builder.setView(v1);

            // Add the buttons
            builder.setPositiveButton("OK", new Xxx(v1));

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User cancelled the dialog
                }
            });



            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }



    public DeviceListViewAdapter(MainActivity activity, int resource, List<DeviceListItem> listItems) {
        super(activity, resource, listItems);

        this.m_activity = activity;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        DeviceListItem rowItem = getItem(position);

        LayoutInflater mInflater =
                (LayoutInflater) m_activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.switch_list_item, null);
            holder = new ViewHolder();

            holder.m_itemIcon = (ImageView) convertView.findViewById(R.id.item_icon);
            holder.m_detailsButton = (ImageView) convertView.findViewById(R.id.details_button);
            holder.m_itemName = (TextView) convertView.findViewById(R.id.item_name);
            holder.m_itemDesc = (TextView) convertView.findViewById(R.id.item_desc);
            holder.m_itemStatus = (TextView) convertView.findViewById(R.id.item_status);
            holder.m_onEvery = (TextView) convertView.findViewById(R.id.on_every_value);
            holder.m_offEvery = (TextView) convertView.findViewById(R.id.off_every_value);
            holder.m_onAt = (TextView) convertView.findViewById(R.id.on_at_value);
            holder.m_offAt = (TextView) convertView.findViewById(R.id.off_at_value);
            holder.m_timerButton = (ImageView) convertView.findViewById(R.id.timer_button);

            // Register onClickListener
            holder.m_itemIcon.setOnClickListener(new ItemIconOnClickListener(rowItem));
            holder.m_detailsButton.setOnClickListener(new Item3DotsOnClickListener(rowItem));
            holder.m_timerButton.setOnClickListener(new ItemTimerButtonOnClickListener(rowItem));

            holder.m_onEvery.setOnClickListener(new ItemTimerValueOnClickListener (rowItem));
            holder.m_offEvery.setOnClickListener(new ItemTimerValueOnClickListener (rowItem));
            holder.m_onAt.setOnClickListener(new ItemTimerValueOnClickListener (rowItem));
            holder.m_offAt.setOnClickListener(new ItemTimerValueOnClickListener (rowItem));

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.m_itemIcon.setImageResource(rowItem.getStatusImgRscId());
        holder.m_itemName.setText(rowItem.getDeviceName());
        holder.m_itemDesc.setText(rowItem.getDeviceDesc());
        holder.m_itemStatus.setText(rowItem.getDeviceStatusText(m_activity));

        holder.m_onEvery.setText(rowItem.getOnEveyText());
        holder.m_offEvery.setText(rowItem.getOffEveyText());
        holder.m_onAt.setText(rowItem.getOnAtText());
        holder.m_offAt.setText(rowItem.getOffAtText());

        return convertView;
    }
}
