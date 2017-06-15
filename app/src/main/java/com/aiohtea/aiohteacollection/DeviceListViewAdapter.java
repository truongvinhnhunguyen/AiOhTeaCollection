package com.aiohtea.aiohteacollection;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

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


    // Class to implement onClick behavior when user click on TIMER VALUE AREA
    private class DeviceListItemOnClickListener implements View.OnClickListener {

        private String m_clickedDeviceId;

        DeviceListItemOnClickListener (String clickedDeviceId){
            m_clickedDeviceId = clickedDeviceId;
        }

        @Override
        public void onClick(View v) {

            int id = v.getId();



            // 3 Dots icon clicked
            if(id == R.id.details_button){
                PopupMenu popupMenu = new PopupMenu(m_activity, v);
                popupMenu.getMenuInflater().inflate(R.menu.dev_gen_popup_menu, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        DeviceListItem clickedDevice = m_activity.getDeviveByDeviceId(m_clickedDeviceId);
                        switch(item.getItemId()) {
                            case R.id.device_popup_1:
                                MainActivity.myToast(m_activity, "Under implementation");
                                break;

                            case R.id.device_popup_2:
                                String msg = m_activity.getString(R.string.confirm_3)
                                        + clickedDevice.getDeviceName() + "?";
                                MyConfirmDialog ask = new MyConfirmDialog(m_activity, msg, clickedDevice);
                                ask.show();
                                break;
                        }
                        return true;
                    }
                });

                popupMenu.show();

                return;
            }

            DeviceListItem clickedDevice = m_activity.getDeviveByDeviceId(m_clickedDeviceId);

            int devStatus = clickedDevice.getDeviceStatus();

            if((devStatus == DeviceListItem.DEV_NOT_SYNCED)
                    ||(devStatus == DeviceListItem.DEV_OFFLINE)){
                MainActivity.myToast(m_activity, m_activity.getString(R.string.dev_offline_notsynced));
                return;
            }



            switch (id) {
                // Device icon clicked
                case R.id.item_icon:
                    clickedDevice.iconClicked(m_activity);
                    break;

                // Timer value areas clicked
                case R.id.on_every_value:
                case R.id.off_every_value:
                case R.id.on_at_value:
                case R.id.off_at_value:

                    Intent intent = new Intent(m_activity, TimerSettingsActivity.class);
                    intent.putExtra("HW_SETTINGS", clickedDevice.getHwSettingPayloadString());
                    intent.putExtra("HW_SETTINGS_DEV_ID", clickedDevice.getDeviceId());

                    m_activity.startActivityForResult(intent, 3);
                    break;

                // Timer button clicked
                case R.id.timer_button:
                    byte[] cmd = {'2'};
                    String msg = m_activity.getString(R.string.confirm_1);

                    if(clickedDevice.isTimerActive()){
                        cmd[0] = '3';
                        msg = m_activity.getString(R.string.confirm_2);
                    }

                    MyConfirmDialog ask = new MyConfirmDialog(m_activity, msg, clickedDevice, cmd);
                    ask.show();

                    break;
            }
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
            holder.m_itemIcon.setOnClickListener(new DeviceListItemOnClickListener(rowItem.getDeviceId()));
            holder.m_detailsButton.setOnClickListener(new DeviceListItemOnClickListener(rowItem.getDeviceId()));
            holder.m_timerButton.setOnClickListener(new DeviceListItemOnClickListener(rowItem.getDeviceId()));

            holder.m_onEvery.setOnClickListener(new DeviceListItemOnClickListener (rowItem.getDeviceId()));
            holder.m_offEvery.setOnClickListener(new DeviceListItemOnClickListener (rowItem.getDeviceId()));
            holder.m_onAt.setOnClickListener(new DeviceListItemOnClickListener (rowItem.getDeviceId()));
            holder.m_offAt.setOnClickListener(new DeviceListItemOnClickListener (rowItem.getDeviceId()));

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.m_itemIcon.setImageResource(rowItem.getDevStatusImgRscId());
        holder.m_itemName.setText(rowItem.getDeviceName());
        holder.m_itemDesc.setText(rowItem.getDeviceDesc());
        holder.m_itemStatus.setText(rowItem.getDeviceStatusText(m_activity));

        holder.m_onEvery.setText(rowItem.getOnEveyText());
        holder.m_offEvery.setText(rowItem.getOffEveyText());
        holder.m_onAt.setText(rowItem.getOnAtText());
        holder.m_offAt.setText(rowItem.getOffAtText());
        holder.m_timerButton.setImageResource(rowItem.getTimerStatusImgRscId());

        return convertView;
    }
}
