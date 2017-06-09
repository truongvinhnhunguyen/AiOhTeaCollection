package com.aiohtea.aiohteacollection;

import java.util.TimeZone;

/**
 * Created by Nguyen Truong on 6/6/2017.
 * Settings data should be synced with apps when hardware device is online
 */

public class HardwareSettings {

    final static public int NUM_TIMERS = 4;
    final static public long MAX_SECS_IN_DAY = 86400;

    String m_deviceFwVer = "V1.0";
    int m_timerActive = 0; // 0: Inactive; 1: Active
    long[] m_startTimer = {86401L, 86401L, 86401L, 86401L}; // Secs in day (UTC timezone)
    long[] m_stopTimer = {86401L, 86401L, 86401L, 86401L};
    long m_timeZone = 25200; // +7


    long m_appTzOffset = 0;

    public HardwareSettings(){
        m_appTzOffset = TimeZone.getDefault().getRawOffset()/1000; // App timezone offset in seconds
    }

    /**
     *
     * @param payloadString
     */

    public HardwareSettings(String payloadString){
        m_appTzOffset = TimeZone.getDefault().getRawOffset()/1000; // App timezone offset in seconds
        parseSettingsPayload(payloadString.getBytes());
    }


    /**
     * public String toPayloadString()
     * @return data encoded to String that can be used to send via MQTT or Intent between Activities
     */
    public String toPayloadString(){
        String s = m_deviceFwVer + '#';

        for (int i=0; i<NUM_TIMERS; i++){
            s += m_startTimer[i];
            s += '#';
        }

        for (int i=0; i<NUM_TIMERS; i++){
            s += m_stopTimer[i];
            s += '#';
        }

        s += m_timeZone;
        s += '#';

        // Timer active
        s += m_timerActive;
        s += '#';

        return s;
    }

    /**
     * public boolean isEnabled(int timerIdx)
     * @param timerIdx
     * @return
     */
    public boolean isEnabled(int timerIdx, boolean startOrStop){
        if(startOrStop == true)
            return (m_startTimer[timerIdx] <= MAX_SECS_IN_DAY);
        else
            return (m_stopTimer[timerIdx] <= MAX_SECS_IN_DAY);
    }

    /**
     *
     * @param timerIdx
     * @return
     */
    public String getValueString(int timerIdx, boolean startOrStop){
        String s = "";
        int num;
        long timer;

        if(startOrStop)
            timer = m_startTimer[timerIdx];
        else
            timer = m_stopTimer[timerIdx];

        timer += m_appTzOffset;

        timer = timer % MAX_SECS_IN_DAY;

        if(isEnabled(timerIdx, startOrStop)) {
            num = (int) (timer / 3600);
            if (num < 10)
                s += '0';
            s += num;
            s += ":";

            num = (int) ((timer % 3600) / 60);
            if (num < 10)
                s += '0';
            s += num;
        }

        return s;
    }

    /**
     * public void setValue (int timerIdx, boolean startOrStop, String valueString)
     * @param timerIdx
     * @param startOrStop
     * @param valueString
     */
    public void setTimerValue (int timerIdx, boolean startOrStop, String valueString){

        long[] timerPointer;

        if (startOrStop)
            timerPointer = m_startTimer;
        else
            timerPointer = m_stopTimer;

        if(valueString.length() > 5)
        {
            timerPointer[timerIdx] = MAX_SECS_IN_DAY + 1;
        } else {
            int t = 60 * (Integer.valueOf(valueString.substring(0, 2)) * 60 + Integer.valueOf(valueString.substring(3, 5)));

            timerPointer[timerIdx] = (MAX_SECS_IN_DAY - m_appTzOffset + t) % MAX_SECS_IN_DAY;
        }
    }

    /**
     *
     * @return text to display on MainActivity list
     */
    public String getTimerText(boolean startOrStop){

        String s = "";
        int num;
        long[] timerPointer;

        if(startOrStop)
            timerPointer = m_startTimer;
        else
            timerPointer = m_stopTimer;

        for (int i=1; i<NUM_TIMERS; i++) {

            if(timerPointer[i] <= MAX_SECS_IN_DAY) {
                long timer = (timerPointer[i] + m_appTzOffset)% MAX_SECS_IN_DAY;

                num = (int) (timer / 3600);
                if (num < 10)
                    s += '0';
                s += num;
                s += ":";

                num = (int) ((timer % 3600) / 60);
                if (num < 10)
                    s += '0';
                s += num;
            } else {
                s += "- -:- -";
            }

            if (i != NUM_TIMERS - 1)
                s += " | ";
        }

        return s;
    }

    /**
     *
     * @param startOrStop
     * @param valueString
     */
    public void setIntervalValue(boolean startOrStop, String valueString){
        long timer;

        if(valueString.equals("")){
            timer = MAX_SECS_IN_DAY + 1;
        }else {
            timer = Long.parseLong(valueString);
        }

        if(startOrStop)
            m_startTimer[0] = timer;
        else
            m_stopTimer[0] = timer;

    }


    /**
     *
     * @return text to display on MainActivity list
     */
    public String getIntervalText(boolean startOrStop){
        String s = "";
        long timer;

        if(startOrStop)
            timer = m_startTimer[0];
        else
            timer = m_stopTimer[0];

        if (timer <= MAX_SECS_IN_DAY) {
            s += timer;
            s += " secs";
        }else{
            s = "- - - - - secs";
        }

        return s;
    }

    /**
     * void parsePayload(byte[] payload)
     * @param payload
     * ex // Payload ex, MS6-1.0#86401#64800#54000#23400#86401#66600#55800#25200#25200#
     */

    public void parseSettingsPayload(byte[] payload){
        int idx = 0;

        // Get firmware version
        m_deviceFwVer = "";
        while (payload[idx] != '#') {
            m_deviceFwVer += (char) payload[idx];
            idx++;
        }
        idx++;

        String s;

        // Get Start timers
        for (int i=0; i<NUM_TIMERS; i++){
            s="";
            while (payload[idx] != '#') {
                s += (char) payload[idx];
                idx++;
            }
            m_startTimer[i] = Long.valueOf(s).longValue();
            idx++;
        }

        // Get Stop timers
        for (int i=0; i<NUM_TIMERS; i++){
            s="";
            while (payload[idx] != '#') {
                s += (char) payload[idx];
                idx++;
            }
            m_stopTimer[i] = Long.valueOf(s).longValue();
            idx++;
        }

        // Get timezone
        s="";
        while (payload[idx] != '#') {
            s += (char) payload[idx];
            idx++;
        }
        m_timeZone = Long.valueOf(s).longValue();
    }

    /**
     *
     * @return
     */
    public String constructTimerSettingPayload(){
        String payload = "4#";

        for (int i=0; i<NUM_TIMERS; i++){
            payload += m_startTimer[i];
            payload += '#';
            payload += m_stopTimer[i];
            payload += '#';
        }

        return payload;

    }
}
