package com.aiohtea.aiohteacollection;

/**
 * Created by Nguyen Truong on 6/6/2017.
 * Settings data should be synced with apps when hardware device is online
 */

public class HardwareSettings {

    final static public int NUM_TIMERS = 4;
    final static public long MAX_SECS_IN_DAY = 86400;

    String m_deviceFwVer = "V1.0";

    long[] m_startTimer = {86401L, 86401L, 86401L, 86401L};
    long[] m_stopTimer = {86401L, 86401L, 86401L, 86401L};

    long m_timeZone = 25200; // +7

    /**
     * void parsePayload(byte[] payload)
     * @param payload
     * ex // Payload ex, MS6-1.0#86401#64800#54000#23400#86401#66600#55800#25200#25200#
     */

    public void parsePayload(byte[] payload){
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
     * @return text to display on MainActivity list
     */
    public String getStartAtText(){

        String s = "";

        int num;
        for (int i=1; i<NUM_TIMERS; i++) {
            if(m_startTimer[i] < MAX_SECS_IN_DAY) {
                num = (int) (m_startTimer[i] / 3600);
                if (num < 10)
                    s += '0';
                s += num;
                s += ":";

                num = (int) ((m_startTimer[i] % 3600) / 60);
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
     * @return text to display on MainActivity list
     */
    public String getStopAtText(){

        String s = "";

        int num;
        for (int i=1; i<NUM_TIMERS; i++) {
            if(m_stopTimer[i] < MAX_SECS_IN_DAY) {
                num = (int) (m_stopTimer[i] / 3600);
                if (num < 10)
                    s += '0';
                s += num;
                s += ":";

                num = (int) ((m_stopTimer[i] % 3600) / 60);
                if (num < 10)
                    s += '0';
                s += num;
            }else{
                s += "- -:- -";
            }

            if (i != NUM_TIMERS - 1)
                s += " | ";
        }

        return s;
    }

    /**
     *
     * @return text to display on MainActivity list
     */
    public String getStartEveryText(){
        String s = "";

        if (m_startTimer[0] < MAX_SECS_IN_DAY) {
            s += m_startTimer[0];
            s += " secs";
        }else{
            s = "- - - - - secs";
        }

        return s;
    }

    /**
     *
     * @return text to display on MainActivity list
     */
    public String getStopEveryText(){
        String s = "";
        if (m_stopTimer[0] < MAX_SECS_IN_DAY) {
            s += m_stopTimer[0];
            s += " secs";
        }else{
            s = "- - - - - secs";
        }

        return s;
    }

}
