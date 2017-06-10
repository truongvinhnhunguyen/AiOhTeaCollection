package com.aiohtea.aiohteacollection;

import android.util.Log;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by Nguyen Truong on 3/30/2017.
 */

public class SwitchListItem extends DeviceListItem {

    public static final int SW_OFF = 0;
    public static final int SW_ON = 1;

    private String m_statusChangedTime = "00:00 - 11-Sep-1970";


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
    public int getDevStatusImgRscId(){
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

    @Override
    public int getTimerStatusImgRscId(){
        if(isTimerActive())
            return R.mipmap.timer_on_button;
        else
            return R.mipmap.timer_off_button;
    }

    // --------------------------------------------------------------------------------------------
    // Called when DeviceList View on MainActivity is being established
    // --------------------------------------------------------------------------------------------
    @Override
    public String getDeviceStatusText(MainActivity mainActivity) {
        switch (this.m_deviceStatus) {
            case SW_OFF:
                return mainActivity.getString(R.string.sw_off) + ": "+ m_statusChangedTime;
            case SW_ON:
                return mainActivity.getString(R.string.sw_on) + ": "+ m_statusChangedTime;
            case DEV_OFFLINE:
                return mainActivity.getString(R.string.sw_off_line);
            default:
                return mainActivity.getString(R.string.sw_unknown);
        }
    }

    // --------------------------------------------------------------------------------------------
    // void commInit(MainActivity mainActivity)
    // For Switch, subscribe "Status" and "Settings" to initiate the switch
    // --------------------------------------------------------------------------------------------
    public void commInit(MainActivity mainActivity){
        MyMqttConnection conn = mainActivity.getConnByName(m_connnName);

        if(conn == null)
            return;

        if((conn.subscribe("AiOhTea/" + m_deviceName + "/Status")!= 0)
                    || conn.subscribe("AiOhTea/" + m_deviceName + "/Settings")!=0){
            MainActivity.myToast (mainActivity, mainActivity.getString(R.string.sw_subcribe_err));
            return;
        }

        m_deviceStatus = DEV_OFFLINE;
        mainActivity.refreshDeviceList();
    }

    // --------------------------------------------------------------------------------------------
    // void commRelease()
    // --------------------------------------------------------------------------------------------
    public void commRelease(MainActivity mainActivity){

    }

    // --------------------------------------------------------------------------------------------
    // This function is called to change switch's status
    // --------------------------------------------------------------------------------------------
    @Override
    void iconClicked (MainActivity mainActivity){
        byte[] payload = {'0'};

        switch (m_deviceStatus) {
            case SW_OFF:
            payload[0] = '1';
            break;

            case SW_ON:
            payload[0] = '0';
            break;
        }

        String result = commandHardware(mainActivity, payload);

        MainActivity.myToast(mainActivity, result);
    }

    // --------------------------------------------------------------------------------------------
    //
    // --------------------------------------------------------------------------------------------
    public void mqttMessageArrive(MainActivity mainActivity, String lastLevelTopic, byte[] payload){

        // Processing Status message
        if(lastLevelTopic.equals("Status")){

            m_deviceStatus = Character.getNumericValue(payload[0]);

            // Clear timers if device offline
            if(m_deviceStatus == DEV_OFFLINE)
                m_hwSettings.clearTimers();

            String s = "";
            int size = payload.length - 1;

            for (int i=2; i<size; i++){
                s += (char)payload[i];
            }

            Date d = new Date(Long.valueOf(s).longValue()*1000);
            DateFormat df = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT);

            m_statusChangedTime = df.format(d);



        } else {
            // Processing Settings message
            if (lastLevelTopic.equals("Settings")) {
                m_hwSettings.parseSettingsPayload(payload);
                Log.d("SETTINGS_ARRIVED", m_hwSettings.constructTimerSettingPayload());
            }
        }

        mainActivity.refreshDeviceList();
    }

    // --------------------------------------------------------------------------------------------
    //
    // --------------------------------------------------------------------------------------------
    public String commandHardware (MainActivity mainActivity, byte[] payload){
        MyMqttConnection conn = mainActivity.getConnByName(m_connnName);

        if(conn == null)
            return mainActivity.getString(R.string.sw_cmd_err) + ": No connection";


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

        return result;
    }
}