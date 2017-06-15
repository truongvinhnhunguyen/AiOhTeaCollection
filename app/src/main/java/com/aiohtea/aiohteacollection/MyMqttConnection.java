package com.aiohtea.aiohteacollection;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.MailTo;
import android.util.Log;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;

/**
 * Created by Nguyen Truong on 6/4/2017.
 */

public class MyMqttConnection {

    // Stored properties
    private String m_connName;
    private String m_mqttServerUri; // such as: tcp://m10.cloudmqtt.com:14110
    private String m_mqttUser;
    private String m_mqttPassword;

    // Not stored properties
    MqttAndroidClient m_client;


    MyMqttConnection(String connName){
        m_connName = connName;

        m_client = null;
    }

    MyMqttConnection(String connName, String mqttServerUri, String mqttUser, String mqttPassword){
        m_connName = connName;
        m_mqttServerUri = mqttServerUri;
        m_mqttUser = mqttUser;
        m_mqttPassword = mqttPassword;

        m_client = null;
    }

    // --------------------------------------------------------------------------------------------
    // getconnName()
    // --------------------------------------------------------------------------------------------
    String getconnName(){
        return m_connName;
    }


    // --------------------------------------------------------------------------------------------
    // Return 0 -> connect successfully
    // --------------------------------------------------------------------------------------------
    public int connect(final MainActivity mainActivity){

        // Prepare MQTT connection
        m_client = new MqttAndroidClient(mainActivity, m_mqttServerUri, MqttClient.generateClientId());

        MqttConnectOptions options = new MqttConnectOptions();

        if(!m_mqttUser.matches("")) {
            options.setUserName(m_mqttUser);
            options.setPassword(m_mqttPassword.toCharArray());
        }

        try {

            IMqttToken mqttToken;

            mqttToken = m_client.connect(options);

            // Set callback once subscribed topics have message or other events
            m_client.setCallback(new MyMqttConnCallback(mainActivity));

            // Set callback for m_client.connect result
            class Xxx implements IMqttActionListener {

                MainActivity m_mainActivity;

                Xxx(MainActivity mainActivity1){
                    m_mainActivity = mainActivity;
                }

                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // Connected
                    // MainActivity.myToast(m_mainActivity, "Successfully connected: " + m_connName);
                    m_mainActivity.commInit();
                };

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    MainActivity.myToast(m_mainActivity, "Connection failure: " + m_connName);
                }
            }

            mqttToken.setActionCallback(new Xxx (mainActivity));

        } catch (MqttException e) {
            e.printStackTrace();
            return 1;
        }

        return 0;
    }

    public void refresh(MainActivity mainActivity){
        if(m_client != null) {
            if(m_client.isConnected()) {
                m_client.registerResources(mainActivity);
                mainActivity.commInit();
                // MainActivity.myToast(mainActivity, "Refreshing conn:" + m_connName);
            }
            else {
                connect(mainActivity);
                // MainActivity.myToast(mainActivity, "Reconnecting:" + m_connName);
            }
        }
    }

    public void disconnect(){

        if(m_client != null)
            m_client.unregisterResources();

        /*
        try {
            if(m_client != null) {
                m_client.disconnect();
                m_client.unregisterResources();
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }
        */
    }

    // --------------------------------------------------------------------------------------------
    // Return 0 -> subscribe successfully
    // Return 1 -> no connection, subscribe failed
    // --------------------------------------------------------------------------------------------
    public int subscribe (String topic){

        if(!m_client.isConnected()){
            return 1; // No connection
        }

        Log.d("MyMqttConn.subscribe", "Start subscribing: " + topic);

        try {
            IMqttToken subToken = m_client. subscribe(topic, 1);
            subToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d("MyMqttConn.subscribe", "Subscribe successfully");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken,
                                      Throwable exception) {
                    Log.d("MyMqttConn.subscribe", "Could not subscribe");
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
            return 1;
        }
        return 0;
    }


    // --------------------------------------------------------------------------------------------
    // Return 0 -> publish successfully
    // --------------------------------------------------------------------------------------------
    public int publish(String topic, byte[] payload){

        if((m_client != null) && m_client.isConnected()){
            try {
                MqttMessage message = new MqttMessage(payload);

                IMqttDeliveryToken token = m_client.publish(topic, message);

                return 0;

            } catch (MqttException e) {
                e.printStackTrace();
            }
        }

        return 1;
    }

    // --------------------------------------------------------------------------------------------
    // Load connection data
    // --------------------------------------------------------------------------------------------
    void connLoad(Context ctx){

        SharedPreferences settings = ctx.getSharedPreferences
                (ctx.getString(R.string.app_name), Context.MODE_PRIVATE);

        m_mqttServerUri = settings.getString(m_connName + "_m_mqttServerUri", "");
        m_mqttUser = settings.getString(m_connName + "_m_mqttUser", "");
        m_mqttPassword = settings.getString(m_connName + "_m_mqttPassword", "");

        Log.d("LOAD", "Loading...");
        Log.d("LOAD", m_connName);
        Log.d("LOAD", m_mqttServerUri);
        Log.d("LOAD", m_mqttUser);
        Log.d("LOAD", m_mqttPassword);
    }

    // --------------------------------------------------------------------------------------------
    // Store connection data
    // --------------------------------------------------------------------------------------------
    void connStore(MainActivity mainActivity){
        SharedPreferences settings = mainActivity.getSharedPreferences
                (mainActivity.getString(R.string.app_name) , Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = settings.edit();

        editor.putString(m_connName + "_m_connName", m_connName);

        editor.putString(m_connName + "_m_mqttServerUri", m_mqttServerUri);
        editor.putString(m_connName + "_m_mqttUser", m_mqttUser);
        editor.putString(m_connName + "_m_mqttPassword", m_mqttPassword);

        editor.commit();
    }

    // --------------------------------------------------------------------------------------------
    // tcp://m10.cloudmqtt.com:14110 => m10.cloudmqtt.com
    // --------------------------------------------------------------------------------------------
    public String getServerAddress(){
        return m_mqttServerUri.substring(m_mqttServerUri.lastIndexOf('/')+1, m_mqttServerUri.lastIndexOf(':'));
    }

    // --------------------------------------------------------------------------------------------
    // tcp://m10.cloudmqtt.com:14110 => 14110
    // --------------------------------------------------------------------------------------------
    public String getSeverPort(){
        return m_mqttServerUri.substring(m_mqttServerUri.lastIndexOf(':')+1, m_mqttServerUri.length());
    }

    // --------------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------------
    public String getMqttUser(){
        return m_mqttUser;
    }

    // --------------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------------
    public String getMqttPassword() {
        return m_mqttPassword;
    }
    // --------------------------------------------------------------------------------------------
    // For listening switch status
    // WARNING MAY GET MEMORY CONFILT WHILE MQTT LIB RUNNING IN DIFFERENT THREAD WITH MAIN_ATIVITY
    // --------------------------------------------------------------------------------------------
    class MyMqttConnCallback implements MqttCallback {

        MainActivity m_mainActivity;

        MyMqttConnCallback (MainActivity mainActivity){
            m_mainActivity = mainActivity;
        }

        @Override
        public void messageArrived(String topic, MqttMessage message) {
            // Message arrived
            // Action: check current status and update list correspondingly
            m_mainActivity.mqttMessageArrive(m_connName, topic, message.getPayload());

            Log.d("MyMqttConn.msgArrived", "Message arrived");
            Log.d("MyMqttConn.msgArrived", topic+":" + message.toString());
        }

        public void deliveryComplete(IMqttDeliveryToken token){
            Log.d("MQTT", "Delivery complete");
        }

        public void connectionLost(Throwable cause){
            cause.printStackTrace();
        }
    }
}
