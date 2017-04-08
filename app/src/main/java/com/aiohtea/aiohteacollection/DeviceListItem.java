package com.aiohtea.aiohteacollection;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.AdapterView;

import org.eclipse.paho.client.mqttv3.IMqttToken;

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


    public static final int APP_NOT_CONNECTED = -1;

    // MQTT
    protected String m_mqttServerUri;
    protected String m_mqttUser;
    protected String m_mqttPassword;
    protected IMqttToken m_mqttToken;

    public DeviceListItem(MainActivity mainActivity, String deviceName, String devicceDesc,
                          String mqttServerUri, String mqttUser, String mqttPassword){

        this.m_mainActivity = mainActivity;
        this.m_deviceName = deviceName;
        this.m_deviceDesc = devicceDesc;
        this.m_deviceStatus = APP_NOT_CONNECTED;
        this.m_mqttServerUri = mqttServerUri;
        this.m_mqttUser = mqttUser;
        this.m_mqttPassword = mqttPassword;
        m_mqttToken = null;
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
    abstract void onClick(Context ctx, AdapterView<?> parent, View view, int position, long id);

    // Methods for saving/loading class
    void deviceLoad(){

    }

    void deviceStore(int pos){
        SharedPreferences settings = m_mainActivity.getSharedPreferences
                (m_mainActivity.getString(R.string.app_name) , 0);

        SharedPreferences.Editor editor = settings.edit();

        String keySuffix = Integer.toString(pos);

        editor.putString("m_deviceName_"+keySuffix, m_deviceName);
        editor.putString("m_deviceDesc_"+keySuffix, m_deviceDesc);

        editor.putString("m_mqttPassword_"+keySuffix, m_mqttPassword);
        editor.putString("m_mqttServerUri_"+keySuffix, m_mqttServerUri);
        editor.putString("m_mqttUser"+keySuffix, m_mqttUser);

        // Commit the edits!
        editor.commit();
    }
}
