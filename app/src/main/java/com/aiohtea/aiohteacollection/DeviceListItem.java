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

    // MainActivity
    MainActivity m_mainActivity;

    // Device parameter
    protected String m_deviceName;
    protected String m_deviceDesc;
    protected int m_deviceStatus;


    // MQTT
    protected String m_mqttServerUri;
    protected String m_mqttUser;
    protected String m_mqttPassword;
    protected IMqttToken m_mqttToken;

    public DeviceListItem(MainActivity mainActivity, String deviceName){

        m_mainActivity = mainActivity;
        m_deviceName = deviceName;

        this.m_deviceStatus = APP_NOT_CONNECTED;
        m_mqttToken = null;
    }

    public DeviceListItem(MainActivity mainActivity, String deviceName, String devicceDesc,
                          String mqttServerUri, String mqttUser, String mqttPassword){

        this.m_mainActivity = mainActivity;
        this.m_deviceName = deviceName;
        this.m_deviceDesc = devicceDesc;
        this.m_mqttServerUri = mqttServerUri;
        this.m_mqttUser = mqttUser;
        this.m_mqttPassword = mqttPassword;

        this.m_deviceStatus = APP_NOT_CONNECTED;
        m_mqttToken = null;
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
    abstract String getDeviceStatusText();
    abstract void onClick();

    // Methods for saving/loading class
    void deviceStore(){
        SharedPreferences settings = m_mainActivity.getSharedPreferences
                (m_mainActivity.getString(R.string.app_name) , Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = settings.edit();

        editor.putString(m_deviceName + "_m_deviceName", m_deviceName);
        editor.putString(m_deviceName + "_m_deviceDesc", m_deviceDesc);

        editor.putString(m_deviceName + "_m_mqttServerUri", m_mqttServerUri);
        editor.putString(m_deviceName + "_m_mqttUser", m_mqttUser);
        editor.putString(m_deviceName + "_m_mqttPassword", m_mqttPassword);

        editor.commit();
    }

    void deviceLoad() {
        SharedPreferences settings = m_mainActivity.getSharedPreferences
                (m_mainActivity.getString(R.string.app_name), Context.MODE_PRIVATE);

        m_deviceDesc = settings.getString(m_deviceName + "_m_deviceDesc", "");
        m_mqttServerUri = settings.getString(m_deviceName + "_m_mqttServerUri", "");
        m_mqttUser = settings.getString(m_deviceName + "_m_mqttUser", "");
        m_mqttPassword = settings.getString(m_deviceName + "_m_mqttPassword", "");

        Log.d("LOAD", "Loading...");
        Log.d("LOAD", m_deviceName);
        Log.d("LOAD", m_deviceDesc);
        Log.d("LOAD", m_mqttServerUri);
        Log.d("LOAD", m_mqttUser);
        Log.d("LOAD", m_mqttPassword);
    }
}
