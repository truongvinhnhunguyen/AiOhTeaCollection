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


    public SwitchListItem(MainActivity mainActivity, String deviceName, String devicceDesc,
                          String mqttServerUri, String mqttUser, String mqttPassword) {
        super(mainActivity, deviceName, devicceDesc, mqttServerUri, mqttUser, mqttPassword);
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
            default:
                return R.mipmap.aiohtea_sw_offline;
        }
    }

    // --------------------------------------------------------------------------------------------
    // Called when DeviceList View on MainActivity is being established
    // --------------------------------------------------------------------------------------------
    @Override
    public String getDeviceStatusText() {
        switch (this.m_deviceStatus) {
            case SW_OFF:
                return "STATUS: OFF";
            case SW_ON:
                return "STATUS: ON";
            case SW_OFFLINE:
                return "STATUS: OFFLINE";
            default:
                return "Tap to connect server";
        }
    }


    // --------------------------------------------------------------------------------------------
    // Called when DeviceList View on MainActivity is clicked
    // --------------------------------------------------------------------------------------------
    @Override
    void onClick(Context ctx, AdapterView<?> parent, View view, int position, long id){

        if (m_deviceStatus == APP_NOT_CONNECTED) { // OFFLINE, connect to MQTT server
            // Prepare MQTT connection
            MqttAndroidClient client = new MqttAndroidClient(ctx, m_mqttServerUri, m_deviceName+"APP");

            MqttConnectOptions options = new MqttConnectOptions();
            options.setUserName(m_mqttUser);
            options.setPassword(m_mqttPassword.toCharArray());
            //options.setAutomaticReconnect(true);

            try {
                m_mqttToken = client.connect(options);

                client.setCallback(new SwitchStatusListener(this));

                m_mqttToken.setActionCallback(new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        // Connected, subscribing switch status topic
                        Log.d("MQTT", "Connected successfully");
                        try {
                            IMqttToken subToken = asyncActionToken.getClient().
                                    subscribe("AiOhTea/" + m_deviceName + "/Status", 1);
                            subToken.setActionCallback(new IMqttActionListener() {
                                @Override
                                public void onSuccess(IMqttToken asyncActionToken) {
                                    // The message was published
                                    Log.d("MQTT", "Message came");
                                }

                                @Override
                                public void onFailure(IMqttToken asyncActionToken,
                                                      Throwable exception) {
                                    Log.d("MY-MQTT", "Could not subscribe");
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
                    }
                });
            } catch (MqttException e) {
                e.printStackTrace();
            }
        } else { // Online, send cmd to MQTT server

            byte[] payload = {'0'};

            switch (m_deviceStatus){
                case SW_OFF: payload[0] = '1'; break;
                case SW_ON: payload[0] = '0'; break;
            }

            try {
                MqttMessage message = new MqttMessage(payload);
                message.setRetained(true);

                m_mqttToken.getClient().publish("AiOhTea/" + m_deviceName + "/Cmd", message);
            } catch (MqttException e) {
                e.printStackTrace();
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
            m_mainActivity.refreshDeviceList(m_switchListItem);

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


