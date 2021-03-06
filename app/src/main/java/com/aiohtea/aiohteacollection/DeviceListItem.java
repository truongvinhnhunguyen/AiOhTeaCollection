package com.aiohtea.aiohteacollection;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by Nguyen Truong on 3/30/2017.
 */

abstract public class DeviceListItem {

    // Constants
    public static final int DEV_NOT_SYNCED = -1;
    public static final int DEV_OFFLINE = 2;
    public static final int SWITCH_DEV_TYPE = 1;

    public static final int MQTT_STATUS_TOPIC = 1;
    public static final int MQTT_CMD_TOPIC = 2;
    public static final int MQTT_SETTINGS_TOPIC = 3;


    protected int m_deviceType;
    protected int m_deviceStatus;


    protected String m_deviceId;

    // Strored Device parameter
    protected String m_deviceName;
    protected String m_devicePassword;
    protected String m_deviceDesc;
    protected String m_connnName;

    /**
     *
     * @return
     */
    public HardwareSettings getHwSettings() {
        return m_hwSettings;
    }

    // Hardware settings
    HardwareSettings m_hwSettings;
    


    public DeviceListItem(String deviceId){
        m_deviceId = deviceId;

        m_hwSettings = new HardwareSettings();

        this.m_deviceStatus = DEV_NOT_SYNCED;
    }

    public DeviceListItem(String deviceId, String deviceName, String devicePassword, String devicceDesc,
                          String connnName){

        this.m_deviceId = deviceId;
        this.m_deviceName = deviceName;
        this.m_devicePassword = devicePassword;
        this.m_deviceDesc = devicceDesc;
        this.m_connnName = connnName;

        m_hwSettings = new HardwareSettings();

        this.m_deviceStatus = DEV_NOT_SYNCED;
    }

    public String getDeviceId(){
        return m_deviceId;
    }


    public String getDeviceName() {
        return m_deviceName;
    }

    public String getDeviceDesc() {
        return m_deviceDesc;
    }

    public String getConnName() {
        return m_connnName;
    }

    public int getDeviceStatus() {
        return m_deviceStatus;
    }

    public void setDeviceStatus(int deviceStatus) {
        this.m_deviceStatus = deviceStatus;
    }


    // Methods for device list
    abstract int getDevStatusImgRscId();
    abstract int getTimerStatusImgRscId();
    abstract String getDeviceStatusText(MainActivity mainActivity);

    // THOSE METHOD CALLED TO DISPLAY IN DEVICE LIST BUT MUST REDESIGN IF DEVICE DOESN'T HAVE TIMER
    public String getOnEveyText(){
        return m_hwSettings.getIntervalFullText(true);
    }

    public String getOffEveyText(){
        return m_hwSettings.getIntervalFullText(false);
    }

    public String getOnAtText(){
        return m_hwSettings.getTimerFullText(true);
    }

    public String getOffAtText(){
        return m_hwSettings.getTimerFullText(false);
    }

    public String getHwSettingPayloadString(){
        return m_hwSettings.toPayloadString();
    }

    public boolean isTimerActive(){
        return m_hwSettings.isTimerActive();
    }

    // Specific behaviors
    abstract void commInit(MainActivity mainActivity);
    abstract void commRelease(MainActivity mainActivity);
    abstract void iconClicked(MainActivity mainActivity);
    abstract void mqttMessageArrive(MainActivity mainActivity, String lastLevelTopic, byte[] payload);
    abstract String commandHardware (MainActivity mainActivity, byte[] payload);

    // Methods for saving/loading class
    void deviceStore(MainActivity mainActivity){
        SharedPreferences settings = mainActivity.getSharedPreferences
                (mainActivity.getString(R.string.app_name) , Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = settings.edit();

        editor.putString(m_deviceId + "_m_deviceName", m_deviceName);
        editor.putString(m_deviceId + "_m_devicePassword", m_devicePassword);
        editor.putString(m_deviceId + "_m_deviceDesc", m_deviceDesc);
        editor.putString(m_deviceId + "_m_connnName", m_connnName);

        editor.commit();

        Log.d("STORE_DEV", "Name: "+m_deviceName);
        Log.d("STORE_DEV", "Pass: "+m_devicePassword);
        Log.d("STORE_DEV", "Desc: "+m_deviceDesc);
        Log.d("STORE_DEV", "Conn: "+m_connnName);
    }

    // Methods for saving/loading class
    void deviceClear(MainActivity mainActivity){
        SharedPreferences settings = mainActivity.getSharedPreferences
                (mainActivity.getString(R.string.app_name) , Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = settings.edit();

        editor.remove(m_deviceId + "_m_deviceName");
        editor.remove(m_deviceId + "_m_devicePassword");
        editor.remove(m_deviceId + "_m_deviceDesc");
        editor.remove(m_deviceId + "_m_connnName");

        editor.commit();
    }

    void deviceLoad(MainActivity mainActivity) {
        SharedPreferences settings = mainActivity.getSharedPreferences
                (mainActivity.getString(R.string.app_name), Context.MODE_PRIVATE);

        m_deviceName = settings.getString(m_deviceId + "_m_deviceName", "");
        m_devicePassword = settings.getString(m_deviceId + "_m_devicePassword", "");
        m_deviceDesc = settings.getString(m_deviceId + "_m_deviceDesc", "");
        m_connnName = settings.getString(m_deviceId + "_m_connnName", "");


        Log.d("DevListItem.deviceLoad", "Loading...");
        Log.d("LOAD_DEV", "Name: "+m_deviceName);
        Log.d("LOAD_DEV", "Pass: "+m_devicePassword);
        Log.d("LOAD_DEV", "Desc: "+m_deviceDesc);
        Log.d("LOAD_DEV", "Conn: "+m_connnName);
    }

    public String getDeviceTopicID(){
        return "AiOhTea/" + m_deviceName + "." + m_devicePassword + "/";
    }

    public String getMqttTopic(int topicType){
        String s = getDeviceTopicID();

        switch (topicType){
            case MQTT_CMD_TOPIC:
                s += "Cmd";
                break;

            case MQTT_SETTINGS_TOPIC:
                s += "Settings";
                break;

            case MQTT_STATUS_TOPIC:
                s += "Status";
                break;
        }

        return s;
    }
}
