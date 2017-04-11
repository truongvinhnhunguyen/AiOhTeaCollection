package com.aiohtea.aiohteacollection;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;

/**
 * Created by Nguyen Truong on 4/10/2017.
 * This class is for displaying a message in case there is no item in the device list
 */

public class EmptyListItem extends DeviceListItem {

    public EmptyListItem (MainActivity mainActivity, String deviceName){
        super(mainActivity, deviceName);
        m_deviceName = m_mainActivity.getString(R.string.guide_title);
        m_deviceDesc = m_mainActivity.getString(R.string.guide_description);
    }

    @Override
    int getStatusImgRscId() {
        return R.mipmap.aiohtea_info_item;
    }

    @Override
    String getDeviceStatusText() {
        return m_mainActivity.getString(R.string.guide);
    }

    @Override
    void onClick(Context ctx, AdapterView<?> parent, View view, int position, long id) {
        MainActivity.myToast(m_mainActivity, m_mainActivity.getString(R.string.guide));
    }
}
