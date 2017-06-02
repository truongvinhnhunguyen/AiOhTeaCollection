package com.aiohtea.aiohteacollection;

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

import java.io.UnsupportedEncodingException;

/**
 * Created by Nguyen Truong on 3/30/2017.
 */

public class SwitchListItem extends DeviceListItem {

    public static final int SW_OFF = 0;
    public static final int SW_ON = 1;
    public static final int SW_OFFLINE = 2;


    public SwitchListItem(MainActivity mainActivity, String deviceName){
        super(mainActivity, deviceName);
        m_deviceType = SWITCH_DEV_TYPE;
    }

    public SwitchListItem(MainActivity mainActivity, String deviceName, String devicceDesc,
                          String mqttServerUri, String mqttUser, String mqttPassword) {
        super(mainActivity, deviceName, devicceDesc, mqttServerUri, mqttUser, mqttPassword);
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
    public String getDeviceStatusText() {
        switch (this.m_deviceStatus) {
            case SW_OFF:
                return m_mainActivity.getString(R.string.sw_off);
            case SW_ON:
                return m_mainActivity.getString(R.string.sw_on);
            case SW_OFFLINE:
                return m_mainActivity.getString(R.string.sw_off_line);
            default:
                return m_mainActivity.getString(R.string.sw_unknown);
        }
    }

    // --------------------------------------------------------------------------------------------
    // This function is called to change switch's status
    // --------------------------------------------------------------------------------------------
    @Override
    void onClick(){

        if (m_deviceStatus == APP_NOT_CONNECTED) { // OFFLINE, connect to MQTT server

            // Prepare MQTT connection

            MqttAndroidClient client = new MqttAndroidClient(m_mainActivity, m_mqttServerUri,
                            m_deviceName+Long.toString(System.currentTimeMillis()));

            MqttConnectOptions options = new MqttConnectOptions();

            if(!m_mqttUser.matches("")) {
                options.setUserName(m_mqttUser);
                options.setPassword(m_mqttPassword.toCharArray());
            }

            try {
                m_mqttToken = client.connect(options);

                client.setCallback(new SwitchStatusListener(this));

                m_mqttToken.setActionCallback(new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        // Connected, subscribing switch status topic
                        Log.d("MQTT", "Connected successfully");
                        MainActivity.myToast
                                (m_mainActivity, m_mainActivity.getString(R.string.sw_conn_ok));
                        try {
                            IMqttToken subToken = asyncActionToken.getClient().
                                    subscribe("AiOhTea/" + m_deviceName + "/Status", 1);
                            subToken.setActionCallback(new IMqttActionListener() {
                                @Override
                                public void onSuccess(IMqttToken asyncActionToken) {
                                    // Once subcribe successfully, change switch status to offline
                                    m_deviceStatus = SW_OFFLINE;
                                    m_mainActivity.refreshDeviceList();
                                    Log.d("MQTT", "Subscribe successfully");
                                    MainActivity.myToast
                                            (m_mainActivity, m_mainActivity.getString(R.string.sw_subcribe_ok));
                                }

                                @Override
                                public void onFailure(IMqttToken asyncActionToken,
                                                      Throwable exception) {
                                    Log.d("MY-MQTT", "Could not subscribe");
                                    MainActivity.myToast
                                            (m_mainActivity, m_mainActivity.getString(R.string.sw_subcribe_err));
                                }

                            });
                        } catch (MqttException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        // Something went wrong e.g. connection timeout or firewall problems
                        Log.d("MY-MQTT", "Connection failure");
                        MainActivity.myToast
                                (m_mainActivity, m_mainActivity.getString(R.string.sw_conn_err));
                    }
                });
            } catch (MqttException e) {
                e.printStackTrace();
            }
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

                try {
                    MqttMessage message = new MqttMessage(payload);

                    m_mqttToken.getClient().publish("AiOhTea/" + m_deviceName + "/Cmd", message);
                    MainActivity.myToast
                            (m_mainActivity, m_mainActivity.getString(R.string.sw_cmd_sent));

                } catch (MqttException e) {
                    MainActivity.myToast
                            (m_mainActivity, m_mainActivity.getString(R.string.sw_cmd_err));
                    e.printStackTrace();
                }
            } else {
                MainActivity.myToast
                        (m_mainActivity, m_mainActivity.getString(R.string.sw_offline_err));
            }
        }
    }

    // --------------------------------------------------------------------------------------------
    // For listening switch status
    // --------------------------------------------------------------------------------------------
    class SwitchStatusListener implements MqttCallback {

        SwitchListItem m_switchListItem;

        SwitchStatusListener(SwitchListItem switchListItem){
            m_switchListItem = switchListItem;
        }
        @Override
        public void messageArrived(String topic, MqttMessage message) {
            // Message arrived
            // Action: check current status and update list correspondingly
            m_deviceStatus = Character.getNumericValue(message.getPayload()[0]);
            m_mainActivity.refreshDeviceList();

            Log.d("MQTT", "Message arrived");
            Log.d("MQTT", topic+":"+m_deviceStatus);
        }

        public void deliveryComplete(IMqttDeliveryToken token){
            Log.d("MQTT", "Delivery complete");
        }

        public void connectionLost(Throwable cause){
            cause.printStackTrace();
        }
    }
}


