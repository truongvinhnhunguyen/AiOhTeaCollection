package com.aiohtea.aiohteacollection;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import org.eclipse.paho.client.mqttv3.IMqttToken;

/**
 * Created by Nguyen Truong on 3/30/2017.
 */

abstract public class DeviceListItem {

    // Constants
    public static final int APP_NOT_CONNECTED = -1;
    public static final int SWITCH_DEV_TYPE = 1;

    protected int m_deviceType;



    // Strored Device parameter
    protected String m_deviceName;
    protected String m_deviceDesc;
    protected String m_connnName;

    protected int m_deviceStatus;





    public DeviceListItem(String deviceName){
        m_deviceName = deviceName;

        this.m_deviceStatus = APP_NOT_CONNECTED;
    }

    public DeviceListItem(String deviceName, String devicceDesc,
                          String connnName){

        this.m_deviceName = deviceName;
        this.m_deviceDesc = devicceDesc;
        this.m_connnName = connnName;

        this.m_deviceStatus = APP_NOT_CONNECTED;
    }

    public int getM_deviceType() {
        return m_deviceType;
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


    // Methods for device list
    abstract int getStatusImgRscId();
    abstract String getDeviceStatusText(MainActivity mainActivity);
    abstract void onClick(MainActivity mainActivity);

    // Methods for saving/loading class
    void deviceStore(MainActivity mainActivity){
        SharedPreferences settings = mainActivity.getSharedPreferences
                (mainActivity.getString(R.string.app_name) , Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = settings.edit();

        editor.putString(m_deviceName + "_m_deviceName", m_deviceName);
        editor.putString(m_deviceName + "_m_deviceDesc", m_deviceDesc);
        editor.putString(m_deviceName + "_m_connnName", m_connnName);

        editor.commit();
    }

    void deviceLoad(MainActivity mainActivity) {
        SharedPreferences settings = mainActivity.getSharedPreferences
                (mainActivity.getString(R.string.app_name), Context.MODE_PRIVATE);

        m_deviceDesc = settings.getString(m_deviceName + "_m_deviceDesc", "");
        m_connnName = settings.getString(m_deviceName + "_m_connnName", "");


        Log.d("DevListItem.deviceLoad", "Loading...");
        Log.d("LOAD", m_deviceName);
        Log.d("LOAD", m_deviceDesc);
        Log.d("LOAD", m_connnName);

    }
}
