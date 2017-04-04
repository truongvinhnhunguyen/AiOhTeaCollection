package com.aiohtea.aiohteacollection;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;

/**
 * Created by Nguyen Truong on 3/30/2017.
 */

abstract public class DeviceListItem {

    // MainActivity
    MainActivity m_mainActivity;

    // Device parameter
    protected String m_deviceName;
    protected String m_deviceDesc;
    protected int m_deviceStatus;

    // MQTT params
    protected String m_mqttServerUri;
    protected String m_mqttUser;
    protected String m_mqttPassword;



    public DeviceListItem(MainActivity mainActivity, String deviceName, String devicceDesc,
                          String mqttServerUri, String mqttUser, String mqttPassword){

        this.m_mainActivity = mainActivity;
        this.m_deviceName = deviceName;
        this.m_deviceDesc = devicceDesc;
        this.m_deviceStatus = 2;
        this.m_mqttServerUri = mqttServerUri;
        this.m_mqttUser = mqttUser;
        this.m_mqttPassword = mqttPassword;
    }

    public String getDeviceName() {
        return m_deviceName;
    }

    public String getDeviceDesc() {
        return m_deviceDesc;
    }

    public int getDeviceStatus() {
        return m_deviceStatus;
    }

    public void setDeviceStatus(int deviceStatus) {
        this.m_deviceStatus = deviceStatus;
    }


    abstract int getStatusImgRscId();
    abstract String getDeviceStatusText();
    abstract void onClick(Context ctx, AdapterView<?> parent, View view, int position, long id);
}
