package com.aiohtea.aiohteacollection;

/*
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
*/

import java.io.UnsupportedEncodingException;

/**
 * Created by Nguyen Truong on 3/30/2017.
 */

public class SwitchListItem extends DeviceListItem {

    public static final int SW_OFF = 0;
    public static final int SW_ON = 1;
    public static final int SW_OFFLINE = 2;


    public SwitchListItem(String deviceName){
        super(deviceName);
        m_deviceType = SWITCH_DEV_TYPE;
    }

    public SwitchListItem(String deviceName, String devicceDesc, String connName){
        super(deviceName, devicceDesc, connName);
        m_deviceType = SWITCH_DEV_TYPE;
    }

    // --------------------------------------------------------------------------------------------
    // Called when DeviceList View on MainActivity is being established
    // --------------------------------------------------------------------------------------------
    @Override
    public int getStatusImgRscId(){
        switch (this.m_deviceStatus) {
            case 0:
                return R.mipmap.aiohtea_sw_off;
            case 1:
                return R.mipmap.aiohtea_sw_on;
            case 2:
                return R.mipmap.aiohtea_sw_offline;
            default:
                return R.mipmap.aiohtea_sw_no_info;
        }
    }

    // --------------------------------------------------------------------------------------------
    // Called when DeviceList View on MainActivity is being established
    // --------------------------------------------------------------------------------------------
    @Override
    public String getDeviceStatusText(MainActivity mainActivity) {
        switch (this.m_deviceStatus) {
            case SW_OFF:
                return mainActivity.getString(R.string.sw_off);
            case SW_ON:
                return mainActivity.getString(R.string.sw_on);
            case SW_OFFLINE:
                return mainActivity.getString(R.string.sw_off_line);
            default:
                return mainActivity.getString(R.string.sw_unknown);
        }
    }

    // --------------------------------------------------------------------------------------------
    // This function is called to change switch's status
    // --------------------------------------------------------------------------------------------
    @Override
    void onClick(MainActivity mainActivity){

        MyMqttConnection conn = mainActivity.getConnByName(m_connnName);
        if(conn == null)
            return;

        if (m_deviceStatus == APP_NOT_CONNECTED) { // OFFLINE, connect to MQTT server

            conn.subscribe("AiOhTea/" + m_deviceName + "/Status");
            conn.subscribe("AiOhTea/" + m_deviceName + "/Settings");

        } else { // App connected to MQTT server, send cmd to MQTT server
            if (m_deviceStatus != SW_OFFLINE) { // Send command if switch online
                byte[] payload = {'0'};

                switch (m_deviceStatus) {
                    case SW_OFF:
                        payload[0] = '1';
                        break;
                    case SW_ON:
                        payload[0] = '0';
                        break;
                }

                int error = conn.publish("AiOhTea/" + m_deviceName + "/Cmd", payload);

                String result;

                switch (error){

                    case 0:
                        result = mainActivity.getString(R.string.sw_cmd_sent);
                        break;

                    case 1:
                        result = mainActivity.getString(R.string.sw_cmd_err);
                        break;

                    default:
                        result = mainActivity.getString(R.string.sw_offline_err);
                }

                MainActivity.myToast(mainActivity, result);
            } else {
                MainActivity.myToast (mainActivity, mainActivity.getString(R.string.sw_offline_err));
            }
        }
    }
}


